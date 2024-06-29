package dev.dynamic.studysphere.auth;

import dev.dynamic.studysphere.StudysphereApplication;
import dev.dynamic.studysphere.model.Role;
import dev.dynamic.studysphere.model.User;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String secret = StudysphereApplication.dotenv.get("JWT_SECRET");
    private final long expiration = 1000 * 60 * 60 * 24 * 7;

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret).build();
    }

    public String createToken(User user) {
        Map<String, Object> claimsMap = Map.of(
                "roles", user.getRole(),
                "email", user.getEmail(),
                "username", user.getUsername()
        );

        Date now = new Date();
        Date validity = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claimsMap)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validToken(String token) {
        try {
            Claims claims = jwtParser.parseClaimsJws(token).getBody();
            return validateClaims(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        return claims.getExpiration().after(new Date());
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public String getEmail(String token) {
        return getEmail(parseJwtClaims(token));
    }

    private Role getRoles(Claims claims) {
        return (Role) claims.get("roles");
    }
}
