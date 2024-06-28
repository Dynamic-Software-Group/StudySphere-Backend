package dev.dynamic.studysphere.endpoints.ratelimit;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {

    public static final String ERROR_MESSAGE = "Too many requests at endpoint %s from IP %s. Please try again after %s seconds.";
    private final ConcurrentHashMap<String, List<Long>> requestCounts = new ConcurrentHashMap<>();

    @Value("${APP_RATE_LIMIT:#{200}}")
    private int ratelimit;

    @Value("${APP_RATE_DURATION:#{60000}}")
    private long duration;

    @Before("@annotation(WithRateLimitProtection)")
    public void rateLimitCheck() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final String key = attributes.getRequest().getRemoteAddr();
        final long now = System.currentTimeMillis();
        requestCounts.putIfAbsent(key, List.of(now));
        requestCounts.get(key).add(now);
        cleanUpRequestCounts(now);
        if (requestCounts.get(key).size() > ratelimit) {
            final long timeToWait = (requestCounts.get(key).get(0) + duration - now) / 1000;
            throw new RateLimitException(String.format(ERROR_MESSAGE, attributes.getRequest().getRequestURI(), key, timeToWait));
        }
    }

    private void cleanUpRequestCounts(final long now) {
        requestCounts.values().forEach(list -> list.removeIf(this::timeIsTooOld));
    }

    private boolean timeIsTooOld(final long time) {
        return System.currentTimeMillis() - time > duration;
    }
}
