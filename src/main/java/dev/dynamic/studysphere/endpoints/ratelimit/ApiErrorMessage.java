package dev.dynamic.studysphere.endpoints.ratelimit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ApiErrorMessage {
    private final UUID id = UUID.randomUUID();
    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final String path;
}
