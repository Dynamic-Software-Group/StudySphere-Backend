package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.model.Role;
import dev.dynamic.studysphere.model.User;
import dev.dynamic.studysphere.model.UserRepository;
import dev.dynamic.studysphere.model.request.LoginRequest;
import dev.dynamic.studysphere.model.response.ErrorResponse;
import dev.dynamic.studysphere.model.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("Login request received");
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).get();

            String token = jwtUtil.createToken(user);
            System.out.println(token);
            LoginResponse loginResponse = new LoginResponse(user.getEmail(), token);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(403).body(new ErrorResponse(HttpStatus.FORBIDDEN, "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody final String username, @RequestBody final String email, @RequestBody final String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.USER);
        user.setUsername(username);
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }
}
