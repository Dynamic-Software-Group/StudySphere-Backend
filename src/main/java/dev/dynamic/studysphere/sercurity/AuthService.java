package dev.dynamic.studysphere.sercurity;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final StringRedisTemplate redisTemplate;

    public AuthService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeJwt(String jwt) {
        redisTemplate.opsForValue().set(jwt, "");
    }

}
