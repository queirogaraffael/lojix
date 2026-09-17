package com.example.lojix.service;

import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.infrastructure.security.TokenService;
import com.example.lojix.infrastructure.repository.UsuarioRepository;
import com.example.lojix.dto.auth.LoginDTO;
import com.example.lojix.dto.auth.TokenResponseDTO;
import com.example.lojix.dto.usuario.UserContextDTO;
import com.example.lojix.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;

    public AuthService(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UsuarioRepository usuarioRepository,
            StorageService storageService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
    }

    public TokenResponseDTO login(LoginDTO data) {
        log.info("Tentativa de login para o usuário '{}'", data.getUsername());

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
            );

            var user = (Usuario) auth.getPrincipal();

            log.info("Login bem-sucedido para o usuário '{}'. Gerando token.", data.getUsername());

            String jwt = tokenService.generateToken(user);

            return new TokenResponseDTO(jwt);

        } catch (Exception e) {
            log.warn("Falha de autenticação para o usuário '{}'", data.getUsername());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public UserContextDTO getUserContext(UUID userId) {

        Usuario usuario = usuarioRepository.findByIdWithAssociations(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Long clienteId = usuario.getCliente() != null ? usuario.getCliente().getId() : null;
        Long funcionarioId = usuario.getFuncionario() != null ? usuario.getFuncionario().getId() : null;
        
        String fotoUrl = storageService.gerarPresignedUrl(usuario.getFotoKey());

        return new UserContextDTO(
                usuario.getId(),
                usuario.getName(),
                usuario.getUsername(),
                usuario.getEmail(),
                fotoUrl,
                usuario.getRole(),
                clienteId,
                funcionarioId
        );
    }

}
