package com.chooz.support;

import com.chooz.support.config.RestDocsConfiguration;
import com.chooz.support.config.TestSecurityConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, TestSecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest extends WebUnitTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    protected static Attributes.Attribute constraints(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected static HeaderDescriptor authorizationHeader() {
        return headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token");
    }

    protected static String enumDescription(String description, Class<? extends Enum<?>> enumClass) {
        StringBuilder values = new StringBuilder(description + " (");
        for (Enum<?> value : enumClass.getEnumConstants()) {
            if (!values.isEmpty()) {
                values.append(", ");
            }
            values.append(value.name());
        }
        values.append(")");
        return values.toString();
    }

    protected static ParameterDescriptor[] cursorQueryParams() {
        return new ParameterDescriptor[]{
                parameterWithName("cursor").optional().description("페이지 조회 커서 값"),
                parameterWithName("size").optional().attributes(defaultValue("10")).description("페이지 크기 (기본 값 10)"),
                parameterWithName("priority").description("정렬 기준 (0: 내가남긴댓글, 1: 다른사람댓글)")
        };
    }

    protected static Attributes.Attribute defaultValue(String value) {
        return new Attributes.Attribute("defaultValue", value);
    }
}
