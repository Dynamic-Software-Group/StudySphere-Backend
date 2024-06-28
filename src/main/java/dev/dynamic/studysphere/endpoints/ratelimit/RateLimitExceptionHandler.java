package dev.dynamic.studysphere.endpoints.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(RateLimitExceptionHandler.class.getName());

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiErrorMessage> handleRateLimitException(final RateLimitException e, final HttpServletRequest request) {
        final ApiErrorMessage apiErrorMessage = e.toApiErrorMessage(request.getRequestURI());
        logIncommingCall(e, apiErrorMessage);
        return ResponseEntity.status(apiErrorMessage.getStatus()).body(apiErrorMessage);
    }

    private static void logIncommingCall(final RateLimitException e, final ApiErrorMessage apiErrorMessage) {
        LOGGER.warning(String.format("Rate limit exceeded for IP %s at endpoint %s. Error message: %s", apiErrorMessage.getId(), apiErrorMessage.getPath(), e.getMessage()));
    }


}
