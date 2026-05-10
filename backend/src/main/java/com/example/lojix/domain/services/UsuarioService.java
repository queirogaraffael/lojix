package com.example.lojix.domain.services;

import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

}

