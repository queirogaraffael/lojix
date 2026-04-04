package com.example.supergestor.utils;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.services.AuthService;
import com.example.supergestor.infrastructure.repositories.ClienteRepository;
import com.example.supergestor.infrastructure.repositories.FuncionarioRepository;
import com.example.supergestor.infrastructure.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TestUtils {

    public static final String DEFAULT_RAW_PASSWORD = "test_password";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    public Usuario createUsuarioPrecondition(String uniqueSuffix, UserRole userRole) {
        String uniqueUsername = "_test_user_" + uniqueSuffix;
        String uniqueCpf = "123456789" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 2);
        String uniqueEmail = String.format("%s@supergestor.com", uniqueSuffix);

        Usuario usuario = new Usuario();
        usuario.setName("User Test " + uniqueSuffix);
        usuario.setUsername(uniqueUsername);
        usuario.setEmail(uniqueEmail);
        usuario.setCpf(uniqueCpf);
        usuario.setPassword(passwordEncoder.encode(DEFAULT_RAW_PASSWORD));
        usuario.setRole(userRole);

        return usuario;
    }

    public Funcionario createAndSaveFuncionarioPrecondition(String uniqueSuffix, String cargo, BigDecimal salario) {
        Usuario usuario = createUsuarioPrecondition(uniqueSuffix, UserRole.FUNCIONARIO);

        Funcionario funcionario = new Funcionario();
        funcionario.setCargo(cargo);
        funcionario.setSalario(salario);

        funcionario.setUsuario(usuario);
        usuario.setFuncionario(funcionario);

        return funcionarioRepository.save(funcionario);
    }

    public Cliente createAndSaveClientePrecondition(String uniqueSuffix) {
        Usuario usuario = createUsuarioPrecondition(uniqueSuffix, UserRole.CLIENTE);

        Cliente cliente = new Cliente();
        cliente.setTempoFidelidade(LocalDate.now());

        cliente.setUsuario(usuario);

        return clienteRepository.save(cliente);
    }

    public Map<String, String> authenticateUser(String username, String rawPassword) throws Exception {
        LoginDTO loginDTO = new LoginDTO(username, rawPassword);

        TokenResponseDTO tokenResponse = authService.login(loginDTO);

        Map<String, String> authData = new HashMap<>();
        authData.put("token", tokenResponse.getToken());
        authData.put("username", username);
        authData.put("password", rawPassword);
        return authData;
    }


    public Map<String, String> createAndAuthenticateAdmin() throws Exception {
        String adminUsername = "admin";
        String adminPassword = "senhaSuperSecreta456";

        if (!usuarioRepository.existsByUsername(adminUsername)) {
            Usuario admin = new Usuario();
            admin.setName("Administrador Padrão");
            admin.setEmail("admin@meuapp.com");
            admin.setUsername(adminUsername);
            admin.setCpf("123456789");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);
            usuarioRepository.save(admin);
        }

        return authenticateUser(adminUsername, adminPassword);
    }

    public Map<String, String> createAndAuthenticateFuncionario(String uniqueSuffix) throws Exception {
        Funcionario funcionario = createAndSaveFuncionarioPrecondition(uniqueSuffix, "Funcao Teste", BigDecimal.TEN);

        return authenticateUser(funcionario.getUsuario().getUsername(), DEFAULT_RAW_PASSWORD);
    }

    public Map<String, String> createAndAuthenticateCliente(String uniqueSuffix) throws Exception {
        Cliente cliente = createAndSaveClientePrecondition(uniqueSuffix);

        return authenticateUser(cliente.getUsuario().getUsername(), DEFAULT_RAW_PASSWORD);
    }


    public Map<String, String> authenticateAs(UserRole role) throws Exception {
        String suffix = role.name().toLowerCase() + "_" + UUID.randomUUID().toString().substring(0, 4);

        if (role == UserRole.ADMIN) {
            return createAndAuthenticateAdmin();
        } else if (role == UserRole.FUNCIONARIO) {
            return createAndAuthenticateFuncionario(suffix);
        } else if (role == UserRole.CLIENTE) {
            return createAndAuthenticateCliente(suffix);
        }
        throw new IllegalArgumentException("Role não suportada para autenticação direta em TestUtils.");
    }

    public Map<String, String> authenticateAndCreateCliente(String baseUsername) throws Exception {
        return createAndAuthenticateCliente(baseUsername);
    }
}