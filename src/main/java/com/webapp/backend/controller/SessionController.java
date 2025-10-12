package com.webapp.backend.controller;

import com.webapp.backend.session.SessionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/session-management")
@Tag(name = "Session Management", description = "APIs for managing user sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login/{userId}")
    public ResponseEntity<String> login(@PathVariable String userId) {
        String response = sessionService.login(userId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logout(@PathVariable String userId) {
        String response = sessionService.logout(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<String> getSession(@PathVariable String userId) {
        Optional<String> session = sessionService.getSession(userId);
        return session.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}
