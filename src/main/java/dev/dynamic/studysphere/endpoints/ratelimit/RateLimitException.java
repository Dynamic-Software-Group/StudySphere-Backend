package dev.dynamic.studysphere.endpoints.ratelimit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Rate limit exceeded")
public class RateLimitException extends RuntimeException {

    public RateLimitException(String message) {
        super(message);
    }

    public ApiErrorMessage toApiErrorMessage(final String path) {
        return new ApiErrorMessage(HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), getMessage(), path);
    }
}
