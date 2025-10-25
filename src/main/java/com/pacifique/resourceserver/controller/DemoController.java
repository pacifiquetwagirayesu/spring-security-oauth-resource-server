package com.pacifique.resourceserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/demo")
    public Map<String,Object> demo(Authentication authentication) {
        return Map.of("demo", authentication);
    }
}
