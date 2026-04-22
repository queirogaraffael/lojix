package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.infrastructure.repositories.UsuarioRepository;
import com.example.supergestor.infrastructure.security.TokenService;
import com.example.supergestor.dtos.auth.LoginDTO;
import com.example.supergestor.dtos.auth.TokenResponseDTO;
import com.example.supergestor.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.dtos.usuario.UserContextDTO;
import com.example.supergestor.mappers.ClienteMapper;
import com.example.supergestor.mappers.FuncionarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final TokenService tokenService;
    private final FuncionarioMapper funcionarioMapper;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            TokenService tokenService,
            FuncionarioMapper funcionarioMapper,
            ClienteMapper clienteMapper, UsuarioRepository usuarioRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
        this.funcionarioMapper = funcionarioMapper;
        this.clienteMapper = clienteMapper;
        this.usuarioRepository = usuarioRepository;
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
    @Transactional
    public UserContextDTO getUserContext() {

        Usuario usuario = usuarioService.getAuthenticatedUser();

        ClienteResponseDTO cliente = clienteMapper.usuarioToClienteResponseDTO(usuario);
        FuncionarioResponseDTO funcionario = funcionarioMapper.usuarioToFuncionarioResponseDTO(usuario);

        return new UserContextDTO(cliente, funcionario);
    }

}
