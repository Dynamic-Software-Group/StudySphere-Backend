package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LoginRequest {
    private final String email;
    private final String password;
}
