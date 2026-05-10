package com.example.lojix.domain.services;

import com.example.lojix.domain.entities.Usuario;
import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import com.example.lojix.infrastructure.security.TokenService;
import com.example.lojix.dtos.auth.LoginDTO;
import com.example.lojix.dtos.auth.TokenResponseDTO;
import com.example.lojix.dtos.cliente.ClienteResponseDTO;
import com.example.lojix.dtos.funcionario.FuncionarioResponseDTO;
import com.example.lojix.dtos.usuario.UserContextDTO;
import com.example.lojix.mappers.ClienteMapper;
import com.example.lojix.mappers.FuncionarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final FuncionarioMapper funcionarioMapper;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            FuncionarioMapper funcionarioMapper,
            ClienteMapper clienteMapper, UsuarioRepository usuarioRepository
    ) {
        this.authenticationManager = authenticationManager;
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
    public UserContextDTO getUserContext(UUID userId) {

        Usuario usuario = usuarioRepository.findByIdWithAssociations(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ClienteResponseDTO cliente = clienteMapper.usuarioToClienteResponseDTO(usuario);
        FuncionarioResponseDTO funcionario = funcionarioMapper.usuarioToFuncionarioResponseDTO(usuario);

        return new UserContextDTO(cliente, funcionario);
    }

}
