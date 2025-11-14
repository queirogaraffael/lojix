package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.infrastructure.security.TokenService;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.shared.dtos.usuario.UserContextDTO;
import com.example.supergestor.shared.mappers.ClienteMapper;
import com.example.supergestor.shared.mappers.FuncionarioMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final FuncionarioMapper funcionarioMapper;
    private final ClienteMapper clienteMapper;

    public AuthService(AuthenticationManager authenticationManager, UsuarioService usuarioService, TokenService tokenService, FuncionarioMapper funcionarioMapper, ClienteMapper clienteMapper) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
        this.funcionarioMapper = funcionarioMapper;
        this.clienteMapper = clienteMapper;
    }

    public TokenResponseDTO login(LoginDTO data) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword()));

        var user = (Usuario) auth.getPrincipal();

        String jwt = tokenService.generateToken(user);

        return new TokenResponseDTO(jwt);
    }

    @Transactional
    public UserContextDTO getUserContext() {
        Usuario usuario = usuarioService.getAuthenticatedUser();

        ClienteResponseDTO cliente = clienteMapper.usuarioToClienteResponseDTO(usuario);
        FuncionarioResponseDTO funcionario = funcionarioMapper.usuarioToFuncionarioResponseDTO(usuario);

        return new UserContextDTO(
                cliente,
                funcionario
        );
    }
}
