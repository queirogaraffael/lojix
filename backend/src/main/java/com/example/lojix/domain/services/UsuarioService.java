package com.example.lojix.domain.services;

import com.example.lojix.domain.entities.Usuario;
import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import com.example.lojix.dtos.usuario.UsuarioResponseDTO;
import com.example.lojix.shared.exceptions.UserNotAuthenticatedException;
import com.example.lojix.mappers.UsuarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
@Service
@Slf4j
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
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

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {

        log.debug("Verificando existência do username: {}", username);

        boolean exists = usuarioRepository.existsByUsername(username);

        log.debug("Username '{}' existe? {}", username, exists);

        return exists;
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getCurrentUser(UUID userId) {

        log.info("Obtendo dados do usuário autenticado pelo ID: {}", userId);

        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new UserNotAuthenticatedException("Usuário não encontrado"));
                
        UsuarioResponseDTO response = usuarioMapper.toUserResponseDTO(user);

        log.debug("Dados do usuário retornados: id={}", user.getId());

        return response;
    }

}

