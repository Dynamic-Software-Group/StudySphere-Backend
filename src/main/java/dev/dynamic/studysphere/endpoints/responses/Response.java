package dev.dynamic.studysphere.endpoints.responses;

import lombok.Data;

@Data
public class Response {
    private final int status;
    private final String message;
}
