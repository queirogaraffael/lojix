package com.example.supergestor.utils;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.ClienteRepository;
import com.example.supergestor.domain.repositories.FuncionarioRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
import com.example.supergestor.shared.dtos.auth.TokenResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public Usuario createUsuarioPrecondition(String uniqueSuffix) {
        String uniqueUsername = "_test_user_" + uniqueSuffix;
        String uniqueCpf = "123456789" + UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 2);
        String uniqueEmail = String.format("%s@supergestor.com", uniqueSuffix);

        Usuario usuario = new Usuario();
        usuario.setName("User Test " + uniqueSuffix);
        usuario.setUsername(uniqueUsername);
        usuario.setEmail(uniqueEmail);
        usuario.setCpf(uniqueCpf);
        usuario.setPassword(passwordEncoder.encode(DEFAULT_RAW_PASSWORD));

        return usuario;
    }

    public Funcionario createAndSaveFuncionarioPrecondition(String uniqueSuffix, String cargo, BigDecimal salario) {
        Usuario usuario = createUsuarioPrecondition(uniqueSuffix);

        Funcionario funcionario = new Funcionario();
        funcionario.setCargo(cargo);
        funcionario.setSalario(salario);

        funcionario.setUsuario(usuario);
        usuario.setRole(UserRole.FUNCIONARIO);

        return funcionarioRepository.save(funcionario);
    }


    public Cliente createAndSaveClientePrecondition(String uniqueSuffix) {
        Usuario usuario = createUsuarioPrecondition(uniqueSuffix);

        Cliente cliente = new Cliente();
        cliente.setTempoFidelidade(LocalDate.now());

        cliente.setUsuario(usuario);
        usuario.setRole(UserRole.CLIENTE);

        return clienteRepository.save(cliente);
    }

    public Map<String, String> authenticateUser(String username, String rawPassword) throws Exception {
        LoginDTO loginDTO = new LoginDTO(username, rawPassword);
        String json = objectMapper.writeValueAsString(loginDTO);

        MvcResult result = mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        TokenResponseDTO tokenResponse = objectMapper.readValue(responseBody, TokenResponseDTO.class);

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