package com.example.lojix.controller;

import com.example.lojix.infrastructure.security.AuthenticatedUser;
import com.example.lojix.service.UsuarioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.lojix.dto.usuario.FotoResponseDTO;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PatchMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FotoResponseDTO> atualizarFoto(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("foto") MultipartFile foto) {
        String fotoUrl = usuarioService.atualizarFoto(principal.getId(), foto);
        return ResponseEntity.ok(new FotoResponseDTO(fotoUrl));
    }
}
