package com.ecommerce.api_gateway;

import com.ecommerce.api_gateway.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Simple demo auth - in production use a real user service
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = jwtUtil.generateToken(username, "ADMIN");
            return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "username", username
            ));
        } else if ("user".equals(username) && "user123".equals(password)) {
            String token = jwtUtil.generateToken(username, "USER");
            return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "username", username
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, String>> validate(
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);
                return ResponseEntity.ok(Map.of(
                    "status", "valid",
                    "username", username
                ));
            }
        }
        return ResponseEntity.status(401).body(Map.of("status", "invalid"));
    }
}