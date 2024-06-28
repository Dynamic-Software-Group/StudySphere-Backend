package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.StudysphereApplication;
import dev.dynamic.studysphere.endpoints.ratelimit.WithRateLimitProtection;
import dev.dynamic.studysphere.entities.User;
import dev.dynamic.studysphere.entities.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/login/with_username")
public class LoginEndpoint {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @WithRateLimitProtection
    public ResponseEntity<Response> login(@RequestBody final String username, @RequestBody final String password) {
        if (!userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new Response(404, "User does not exist"));
        }

        User user = userRepository.findByUsername(username).get();
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new Response(403, "Incorrect password"));
        }


        return ResponseEntity.ok(new Response(200, "ok"));
    }
}
