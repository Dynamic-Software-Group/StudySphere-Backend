package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.dtos.LoginUserDto;
import dev.dynamic.studysphere.dtos.RegisterUserDto;
import dev.dynamic.studysphere.endpoints.responses.LoginResponse;
import dev.dynamic.studysphere.entities.User;
import dev.dynamic.studysphere.security.AuthenticationService;
import dev.dynamic.studysphere.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationEndpoint {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationEndpoint(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpiration());

        return ResponseEntity.ok(loginResponse);
    }
}