package com.user.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.user.management.constants.Constants.USER_HEALTH_CHECK;
import static com.user.management.constants.RESTUriConstants.HEALTH_CHECK;
import static com.user.management.constants.RESTUriConstants.PUBLIC;

@RestController(PUBLIC)
public class HealthController {
    @GetMapping(HEALTH_CHECK)
    public ResponseEntity<String> healthController() {
        return ResponseEntity.ok(USER_HEALTH_CHECK);
    }
}
