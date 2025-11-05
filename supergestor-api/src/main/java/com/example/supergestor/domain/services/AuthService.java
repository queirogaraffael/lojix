package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.infrastructure.security.TokenService;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public TokenResponseDTO login(LoginDTO data) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword())
        );

        var user = (Usuario) auth.getPrincipal();

        String jwt = tokenService.generateToken(user);

        return new TokenResponseDTO(jwt);
    }


}
