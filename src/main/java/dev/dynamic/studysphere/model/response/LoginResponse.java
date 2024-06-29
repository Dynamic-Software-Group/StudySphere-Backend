package dev.dynamic.studysphere.model.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LoginResponse {
    private String email;
    private String token;

    @Override
    public String toString() {
        return STR."{   'email':'\{email}\{'\''}   , 'token':'\{token}\{'\''}\{'}'}";
    }
}
