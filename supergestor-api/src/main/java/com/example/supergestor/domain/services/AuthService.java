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

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioService usuarioService,
            TokenService tokenService,
            FuncionarioMapper funcionarioMapper,
            ClienteMapper clienteMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.tokenService = tokenService;
        this.funcionarioMapper = funcionarioMapper;
        this.clienteMapper = clienteMapper;
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
        log.debug("Obtendo contexto do usuário autenticado.");

        Usuario usuario = usuarioService.getAuthenticatedUser();

        ClienteResponseDTO cliente = clienteMapper.usuarioToClienteResponseDTO(usuario);
        FuncionarioResponseDTO funcionario = funcionarioMapper.usuarioToFuncionarioResponseDTO(usuario);

        if (cliente == null && funcionario == null) {
            log.error("Usuário {} não é associado a Cliente nem a Funcionario.", usuario.getUsername());
        } else {
            log.info("Contexto do usuário '{}' gerado com sucesso.", usuario.getUsername());
        }

        return new UserContextDTO(cliente, funcionario);
    }
}

