package dev.dynamic.studysphere.endpoints;

import lombok.Data;

@Data
public class Response {
    private final int status;
    private final String message;
}
