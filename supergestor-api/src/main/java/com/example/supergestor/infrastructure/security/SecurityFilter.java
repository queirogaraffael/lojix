package com.example.supergestor.infrastructure.security;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = recoverToken(request);
            if (token != null) {
                String username = tokenService.validateToken(token);
                if (username != null) {
                    Optional<Usuario> user = usuarioRepository.findByUsername(username);
                    if (user.isPresent()) {

                        var authentication = new UsernamePasswordAuthenticationToken(
                                user, null, user.get().getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Erro na autenticação: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Falha na autenticação");
            return;
        }
        filterChain.doFilter(request, response);
    }


    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

}