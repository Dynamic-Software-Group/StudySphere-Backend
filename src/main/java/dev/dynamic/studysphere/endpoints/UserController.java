package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.model.User;
import dev.dynamic.studysphere.model.UserRepository;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PatchMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity updateUser(@RequestBody UpdateUserRequest request) {
        String initialEmail = jwtUtil.getEmail(request.getToken());

        if (userRepository.findByEmail(initialEmail).isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userRepository.findByEmail(initialEmail).get();

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getName() != null) {
            user.setUsername(request.getName());
        }

        userRepository.save(user);

        return ResponseEntity.ok().body("User updated successfully");
    }

    @GetMapping(path = "/get", produces = "application/json")
    public ResponseEntity getUser(@RequestParam String token) {
        String email = jwtUtil.getEmail(token);

        if (userRepository.findByEmail(email).isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userRepository.findByEmail(email).get();

        return ResponseEntity.ok().body(user.toString());
    }

    @Data
    @Getter
    public static class UpdateUserRequest {
        private final String token;
        private final String email;
        private final String name;
    }
}
