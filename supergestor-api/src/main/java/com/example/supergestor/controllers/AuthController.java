package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.AuthService;
import com.example.supergestor.domain.services.UsuarioService;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getCurrentUser() {
        return ResponseEntity.ok(usuarioService.getCurrentUser());
    }
}