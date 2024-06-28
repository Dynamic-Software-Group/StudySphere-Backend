package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupRequest {
    private final String email;
    private final String password;
    private final String username;
}
