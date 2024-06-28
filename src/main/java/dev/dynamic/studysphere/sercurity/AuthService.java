package dev.dynamic.studysphere.sercurity;

import dev.dynamic.studysphere.StudysphereApplication;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final StringRedisTemplate redisTemplate;

    public AuthService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createJWT(String user) {
        String jwt = Jwts.builder()
                .setSubject(user)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(SignatureAlgorithm.RS256, StudysphereApplication.dotenv.get("JWT_SECRET"))
                .compact();

        storeJwt(jwt);
        return jwt;
    }

    public void storeJwt(String jwt) {
        redisTemplate.opsForValue().set(jwt, "");
    }
}
