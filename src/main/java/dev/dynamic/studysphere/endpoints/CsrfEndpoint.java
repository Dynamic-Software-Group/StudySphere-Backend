package dev.dynamic.studysphere.endpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfEndpoint {

    @GetMapping("/csrf")
    public ResponseEntity<Response> csrf() {
       return ResponseEntity.ok(new Response(200, "CSRF token generated"));
    }

}
