package org.ghosty.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ghosty.dto.response.AuthResponseDTO;
import org.ghosty.dto.request.LoginRequestDTO;
import org.ghosty.dto.request.RegisterRequestDTO;
import org.ghosty.dto.request.GoogleLoginRequestDTO;
import org.ghosty.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register( @Valid  @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login( @Valid  @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponseDTO> loginWithGoogle(@Valid @RequestBody GoogleLoginRequestDTO request) {
        return ResponseEntity.ok(authService.authenticateWithGoogle(request));
    }
}