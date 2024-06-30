package dev.dynamic.studysphere.endpoints;

import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.auth.MailSender;
import dev.dynamic.studysphere.auth.SecurityConfig;
import dev.dynamic.studysphere.model.Role;
import dev.dynamic.studysphere.model.User;
import dev.dynamic.studysphere.model.UserRepository;
import dev.dynamic.studysphere.model.request.EmailVerificationRequest;
import dev.dynamic.studysphere.model.request.LoginRequest;
import dev.dynamic.studysphere.model.request.SignupRequest;
import dev.dynamic.studysphere.model.response.ErrorResponse;
import dev.dynamic.studysphere.model.response.LoginResponse;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityConfig passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    private MailSender mailSender;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, SecurityConfig passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        try {
            boolean correctPassword = passwordEncoder.passwordEncoder().matches(loginRequest.getPassword(), userRepository.findByEmail(loginRequest.getEmail()).get().getPassword());

            if (!correctPassword) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or bad password"));
            }

            Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(HttpStatus.UNAUTHORIZED, "User not found"));
            }

            if (!optionalUser.get().isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(HttpStatus.UNAUTHORIZED, "Email not verified"));
            }

            User user = optionalUser.get();
            String token = jwtUtil.createToken(user);
            LoginResponse loginResponse = new LoginResponse(user.getEmail(), token);
            return ResponseEntity.ok(loginResponse.toString());
        } catch (BadCredentialsException | NoSuchElementException e) {
            return ResponseEntity.status(403).body(new ErrorResponse(HttpStatus.FORBIDDEN, "Invalid username or bad password"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()));
        }
    }

    @ResponseBody
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity signup(@RequestBody SignupRequest signupRequest) {
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();
        String username = signupRequest.getUsername();
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(HttpStatus.CONFLICT, "Email already exists"));
        }

        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(HttpStatus.CONFLICT, "Username already exists"));
        }

        User user = new User();
        user.setEmail(email);
        String encodedPass = passwordEncoder.passwordEncoder().encode(password);
        user.setPassword(encodedPass);
        user.setRole(Role.USER);
        user.setUsername(username);
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    @PostMapping(value = "/request_verify")
    public ResponseEntity requestVerify(@RequestBody EmailVerificationRequest request) throws MessagingException {
        String email = jwtUtil.getEmail(request.getToken());
        Optional<User> oUser = userRepository.findByEmail(email);
        if (oUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(HttpStatus.NOT_FOUND, "User not found"));
        }

        User user = oUser.get();
        mailSender.sendVerificationEmail(user);
        return ResponseEntity.ok("Verification email sent");
    }

    @GetMapping(value = "/verify")
    public RedirectView verify(@RequestParam String userId, @RequestParam String token) {
        User user = userRepository.findById(String.valueOf(Long.parseLong(userId))).orElse(null);

        if (user == null) {
            return new RedirectView("https://app.studysphere.com/verifyError?error=notFound"); // TODO: update if needed
        }

        if (user.getEmailVerificationToken().toString().equals(token)) {
            user.setEmailVerified(true);
            userRepository.save(user);
            return new RedirectView("https://app.studysphere.com/verifySuccess");
        }

        return new RedirectView("https://app.studysphere.com/verifyError?error=invalidToken");
    }
}