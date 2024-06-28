package dev.dynamic.studysphere.security;

import dev.dynamic.studysphere.StudysphereApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JWTManager extends OncePerRequestFilter {

    private final JWTValidatorService jwtValidatorService;

    public JWTManager(JWTValidatorService jwtValidatorService) {
        this.jwtValidatorService = jwtValidatorService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        System.out.println(filterChain.getClass().getName());
        if (!uri.contains("/api/v1/protected") || !uri.contains("/api/v1/admin")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        if (token != null && jwtValidatorService.isValidToken(token)) {
            Claims claims = Jwts.parser().setSigningKey(StudysphereApplication.dotenv.get("JWT_SECRET")).parseClaimsJws(token).getBody();

            Authentication auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }
}
