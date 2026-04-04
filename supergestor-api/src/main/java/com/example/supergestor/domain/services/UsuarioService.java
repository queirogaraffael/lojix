package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.infrastructure.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import com.example.supergestor.shared.exceptions.UserNotAuthenticatedException;
import com.example.supergestor.mappers.UsuarioMapper;
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

    public Usuario getAuthenticatedUser() {

        log.debug("Obtendo usuário autenticado do contexto de segurança");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {

            log.warn("Tentativa de acesso sem autenticação");
            throw new UserNotAuthenticatedException("Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Optional<?> optional && optional.isPresent() && optional.get() instanceof Usuario) {
            Usuario user = (Usuario) optional.get();
            log.debug("Usuário autenticado obtido via Optional: id={}", user.getId());
            return user;
        }

        if (principal instanceof Usuario user) {
            log.debug("Usuário autenticado: id={}", user.getId());
            return user;
        }

        log.error("Tipo inesperado de principal: {}", principal.getClass().getName());
        throw new UserNotAuthenticatedException("Tipo de principal inesperado ou usuário não encontrado.");
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getCurrentUser() {

        log.info("Obtendo dados do usuário autenticado");

        Usuario user = getAuthenticatedUser();
        UsuarioResponseDTO response = usuarioMapper.toUserResponseDTO(user);

        log.debug("Dados do usuário retornados: id={}", user.getId());

        return response;
    }

}

