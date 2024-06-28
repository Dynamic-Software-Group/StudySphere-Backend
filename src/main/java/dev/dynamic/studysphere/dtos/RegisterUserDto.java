package dev.dynamic.studysphere.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
}
