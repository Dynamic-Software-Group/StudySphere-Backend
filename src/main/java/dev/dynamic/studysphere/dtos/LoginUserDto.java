package dev.dynamic.studysphere.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class LoginUserDto {
    private String email;
    private String password;
}
