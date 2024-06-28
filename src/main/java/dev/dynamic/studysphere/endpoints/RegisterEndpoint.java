package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.StudysphereApplication;
import dev.dynamic.studysphere.endpoints.ratelimit.WithRateLimitProtection;
import dev.dynamic.studysphere.entities.Role;
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
@RequestMapping("/api/v1/register")
public class RegisterEndpoint {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @WithRateLimitProtection
    public ResponseEntity<Response> register(@RequestBody final String username, @RequestBody final String password, @RequestBody final String email) {

        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new Response(400, "User already exists"));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body(new Response(400, "Email already in use"));
        }

        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

        String encodedPassword = encoder.encode(password);

        User user = new User();
        // Random ID
        user.setId((long) (Math.random() * 1000000));
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setRole(Role.USER);

        String jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(SignatureAlgorithm.RS256, StudysphereApplication.dotenv.get("JWT_SECRET"))
                .compact();

        return ResponseEntity.ok(new Response(200, "User registered successfully" + jwt));
    }

}
