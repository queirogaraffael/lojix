package com.example.lojix.service;

import com.example.lojix.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

import com.example.lojix.common.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.domain.enums.UserRole;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;

    public UsuarioService(UsuarioRepository usuarioRepository, StorageService storageService) {
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        log.info("Carregando usuário pela credencial de login: {}", identifier);

        Optional<Usuario> usuarioOpt;

        // 1. Roteamento de Alta Performance
        if (identifier.contains("@")) {
            usuarioOpt = usuarioRepository.findByEmail(identifier);
        } else if (identifier.matches("\\d{11}")) {
            usuarioOpt = usuarioRepository.findByCpf(identifier);
        } else {
            usuarioOpt = usuarioRepository.findByUsername(identifier);
        }

        Usuario usuario = usuarioOpt.orElseThrow(() -> {
            log.warn("Tentativa de login falhou, credencial não encontrada: {}", identifier);
            return new UsernameNotFoundException("Credenciais inválidas: " + identifier);
        });

        // 2. Bloqueio da Porta para Clientes
        if (usuario.getRole() == UserRole.CLIENTE) {
            log.warn("Tentativa de login de Cliente bloqueada: {}", identifier);
            throw new DisabledException("Acesso de clientes ainda não está liberado no sistema.");
        }

        return usuario;
    }

    @Transactional
    public String atualizarFoto(UUID usuarioId, MultipartFile file) {
        log.info("Atualizando foto do usuário: {}", usuarioId);
        
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        String fotoKey = storageService.uploadFoto(usuarioId, file);
        usuarioRepository.updateFotoKey(usuarioId, fotoKey);
        
        return storageService.gerarPresignedUrl(fotoKey);
    }
}

