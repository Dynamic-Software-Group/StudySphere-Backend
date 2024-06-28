package dev.dynamic.studysphere.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestAuth {

    @GetMapping
    public String test() {
        return "Hello World";
    }
}