package com.javaoffers.nexus.pkg.auth;

import com.javaoffers.nexus.config.NexusProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * 对应 Go pkg/auth/jwt.go：HS256 签发与解析，claim 含 userId。
 */
@Component
public class JwtUtil {

    private final NexusProperties props;
    private final SecretKey key;

    public JwtUtil(NexusProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getAuth().getJwtSecret().getBytes());
    }

    public String generateToken(Long userId) {
        long expireMs = props.getAuth().getJwtExpire() * 1000L;
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuer("nexus")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expireMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 token，返回 userId；失败返回 null。
     */
    public Long parseToken(String token) {
        try {
            Claims c = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            Object uid = c.get("userId");
            if (uid instanceof Number) {
                return ((Number) uid).longValue();
            }
            if (uid instanceof String) {
                return Long.parseLong((String) uid);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
