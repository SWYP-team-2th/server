package com.swyp8team2.common.presentation;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class HttpLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/docs")) {
            chain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        chain.doFilter(requestWrapper, responseWrapper);
        long end = System.currentTimeMillis();

        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // logback 설정 해야됨
        MDC.put("requestId", requestId);

        try {
            log.info("""
                            |
                            | [REQUEST] {} {} {} ({}s)
                            | Headers : {}
                            | RequestBody : {}
                            | RequestParams : {}
                            | Response : {}
                            """.trim(),
                    request.getMethod(),
                    request.getRequestURI(),
                    HttpStatus.valueOf(responseWrapper.getStatus()),
                    (end - start) / 1000.0,
                    getHeaders(request),
                    getRequestBody(requestWrapper),
                    getRequestParameters(request),
                    getResponseBody(responseWrapper)
            );
            responseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            log.error("Logging Error", e);
        } finally {
            MDC.clear();
        }
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder("{\n");

        List<String> loggingHeaders = List.of(
                HttpHeaders.USER_AGENT,
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.COOKIE,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.HOST,
                HttpHeaders.REFERER,
                HttpHeaders.ORIGIN
        );
        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            if (loggingHeaders.contains(headerName)) {
                sb.append("\t%s=%s,\n".formatted(headerName, request.getHeader(headerName)));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    private String getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        StringBuilder params = new StringBuilder();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                params.append(key).append("=").append(value).append("&");
            }
        }

        if (!params.isEmpty()) {
            params.deleteCharAt(params.length() - 1);
        }

        return params.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        assert wrapper != null;

        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
            try {
                return new String(buf, wrapper.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
        return "";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        assert wrapper != null;

        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length > 0) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String responseBody = new String(buf, StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(responseBody);
            payload = gson.toJson(jsonElement);
        }
        wrapper.copyBodyToResponse();
        return Objects.isNull(payload) ? "" : payload;
    }
}
