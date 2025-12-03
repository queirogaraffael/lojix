package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import com.example.supergestor.shared.exceptions.UserNotAuthenticatedException;
import com.example.supergestor.shared.mappers.UsuarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserNotAuthenticatedException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Usuario) {
            return (Usuario) optional.get();
        }

        if (principal instanceof Usuario user) {
            return user;
        }

        throw new UserNotAuthenticatedException("Tipo de principal inesperado ou usuário não encontrado.");
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getCurrentUser() {
        Usuario user = getAuthenticatedUser();
        return usuarioMapper.toUserResponseDTO(user);
    }
}