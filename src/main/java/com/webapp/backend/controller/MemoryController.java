package com.webapp.backend.controller;

import com.webapp.backend.memory.MemoryManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/memory-management")
@Tag(name = "Memory Management", description = "APIs for managing memory cache")
public class MemoryController {

    @Autowired
    private MemoryManager memoryManager;

    @PostMapping("/put/{sessionId}")
    @Operation(summary = "Store session data in memory")
    public ResponseEntity<String> putSessionData(@PathVariable String sessionId, @RequestBody byte[] data) {
        memoryManager.putSessionData(sessionId, data);
        return ResponseEntity.ok("Session data stored successfully.");
    }

    @GetMapping("/get/{sessionId}")
    @Operation(summary = "Retrieve session data from memory")
    public ResponseEntity<String> getSessionData(@PathVariable String sessionId) {
        Optional<byte[]> data = memoryManager.getSessionData(sessionId);
        return data.map(bytes -> ResponseEntity.ok(new String(bytes)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/remove/{sessionId}")
    @Operation(summary = "Remove session data from memory")
    public ResponseEntity<String> removeSessionData(@PathVariable String sessionId) {
        memoryManager.removeSessionData(sessionId);
        return ResponseEntity.ok("Session data removed successfully.");
    }
}