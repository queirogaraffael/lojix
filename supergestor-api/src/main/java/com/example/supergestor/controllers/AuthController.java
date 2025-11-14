package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.AuthService;
import com.example.supergestor.domain.services.UsuarioService;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import com.example.supergestor.shared.dtos.usuario.UserContextDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Login", description = "Realiza o login do usuário e retorna um token JWT")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @GetMapping("/me")
    public ResponseEntity<UserContextDTO> getUserContext() {
        UserContextDTO context = authService.getUserContext();
        return ResponseEntity.ok(context);
    }

}
