package dev.dynamic.studysphere.auth;

import dev.dynamic.studysphere.model.User;
import dev.dynamic.studysphere.model.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MailSender {

    private final JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    public MailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(User user) throws MessagingException {
        UUID token = generateVerificationToken();
        String email = buildVerificationEmail(user, token);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiration(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        helper.setTo(user.getEmail());
        helper.setFrom("admin@polarix.host");
        helper.setSubject("StudySphere Email Verification");
        helper.setText(email, true);

        javaMailSender.send(message);
    }

    private UUID generateVerificationToken() {
        return UUID.randomUUID();
    }

    // Junky email template below
    private String buildVerificationEmail(User user, UUID token) {
        String verificationLink = "http://localhost:8080/api/v1/auth/verify?userId=" + user.getId() + "&token=" + token; // Replace with your actual link
        verificationLink = verificationLink.replace(";", "%%;");

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>Confirmation Page</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;
                }
                .email-container {
                    width: 100%;
                    max-width: 600px;
                    margin: 0 auto;
                    padding: 20px;
                    background-color: #ffffff;
                    box-shadow: 0px 0px 10px 0px rgba(0,0,0,0.1);
                }
                .confirm-button {
                    display: inline-block;
                    font-weight: 400;
                    text-align: center;
                    white-space: nowrap;
                    vertical-align: middle;
                    user-select: none;
                    border: 1px solid transparent;
                    padding: .375rem .75rem;
                    font-size: 1rem;
                    line-height: 1.5;
                    border-radius: .25rem;
                    transition: color .15s ease-in-out,background-color .15s ease-in-out,border-color .15s ease-in-out,box-shadow .15s ease-in-out;
                    color: #fff;
                    background-color: #007bff;
                    border-color: #007bff;
                    text-decoration: none;
                }
                .confirm-button:hover {
                    color: #fff;
                    background-color: #0056b3;
                    border-color: #004499;
                }
                .link-display {
                    margin-top: 20px;
                    font-size: 0.9rem;
                    color: #333;
                }
                h1, p {
                    text-align: center;
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <h1>Welcome to StudySphere!</h1>
                <p>Please confirm your email address by clicking the button below:</p>
                <a href=""" + verificationLink + """
                 class="confirm-button">Confirm</a>
                <p class="link-display">Link: """ + verificationLink + """
            </p>
            </div>
        </body>
        </html>
        """;
        html = html.replace("%", "%%"); // Escape any % characters in the HTML string
        return String.format(html, verificationLink, verificationLink);
    }
}