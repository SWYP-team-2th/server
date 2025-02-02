package com.swyp8team2.auth.application;

import com.swyp8team2.common.exception.ErrorCode;
import com.swyp8team2.common.exception.InternalServerException;
import com.swyp8team2.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private static final long ACCESS_TOKEN_EXPIRATION_HOUR = 2;
    private static final long REFRESH_TOKEN_EXPIRATION_HOUR = 24 * 14;

    private final Key key;
    private final Clock clock;
    private final String issuer;

    public JwtProvider(
            @Value("${jwt.token.secret-key}") String key,
            @Value("${jwt.token.issuer}") String issuer,
            Clock clock) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.clock = clock;
        this.issuer = issuer;
    }

    public TokenPair createToken(JwtClaim claim) {
        return new TokenPair(createAccessToken(claim), createRefreshToken(claim));
    }

    public String createAccessToken(JwtClaim claim) {
        return createToken(claim, ACCESS_TOKEN_EXPIRATION_HOUR);
    }

    public String createRefreshToken(JwtClaim claim) {
        return createToken(claim, REFRESH_TOKEN_EXPIRATION_HOUR);
    }

    private String createToken(JwtClaim claim, long expiration) {
        Instant now = clock.instant();
        Instant expiredAt = now.plus(expiration, ChronoUnit.HOURS);

        return Jwts.builder()
                .claim(JwtClaim.ID, claim.id())
                .setIssuedAt(Date.from(now))
                .setIssuer(issuer)
                .setExpiration(Date.from(expiredAt))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtClaim parseToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .setSigningKey(key)
                    .build();

            Claims claims = parser.parseClaimsJws(token)
                    .getBody();
            String userId = (String) claims.get(JwtClaim.ID);
            return new JwtClaim(Long.parseLong(userId));
        } catch (ExpiredJwtException e) {
            log.trace("Expired Jwt Token: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            log.trace("Invalid Jwt Token: {}", e.getMessage());
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new InternalServerException();
        }
    }
}
