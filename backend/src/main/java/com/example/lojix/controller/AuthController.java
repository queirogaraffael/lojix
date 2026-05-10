package com.example.lojix.controller;

import com.example.lojix.service.AuthService;
import com.example.lojix.dto.auth.LoginDTO;
import com.example.lojix.dto.auth.TokenResponseDTO;
import com.example.lojix.dto.usuario.UserContextDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.example.lojix.infrastructure.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Login",
            description = "Realiza o login do usuário e retorna um token JWT"
    )
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @Operation(
            summary = "Obter contexto do usuário autenticado",
            description = "Retorna informações sobre o usuário atualmente logado, como IDs e vínculos."
    )
    @ApiResponse(responseCode = "200", description = "Contexto retornado com sucesso")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping("/me")
    public ResponseEntity<UserContextDTO> getUserContext(@AuthenticationPrincipal AuthenticatedUser principal) {
        UserContextDTO context = authService.getUserContext(principal.getId());
        return ResponseEntity.ok(context);
    }
}
