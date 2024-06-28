package dev.dynamic.studysphere.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/protected/test")
public class TestAuthenticatedApi {

    @GetMapping
    public String test() {
        return "You are authenticated!";
    }
}
