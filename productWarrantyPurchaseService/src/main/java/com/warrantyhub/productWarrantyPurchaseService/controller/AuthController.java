package com.warrantyhub.productWarrantyPurchaseService.controller;

import com.warrantyhub.productWarrantyPurchaseService.dto.LoginRequest;
import com.warrantyhub.productWarrantyPurchaseService.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        String token = authService.login(request);

        return ResponseEntity.ok(token);
    }
}