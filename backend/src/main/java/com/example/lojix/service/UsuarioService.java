package com.example.lojix.service;

import com.example.lojix.infrastructure.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

import com.example.lojix.common.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Carregando usuário pelo username: {}", username);

        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Usuário não encontrado: {}", username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
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

