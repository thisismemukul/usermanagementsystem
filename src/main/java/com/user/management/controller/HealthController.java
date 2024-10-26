package com.user.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/public/health")
    public ResponseEntity<String> healthController(){
    return ResponseEntity.ok("Health OK");
    }


    @GetMapping("/hi")
    public ResponseEntity<String> hi(){
        return ResponseEntity.ok("ji OK");
    }
}
