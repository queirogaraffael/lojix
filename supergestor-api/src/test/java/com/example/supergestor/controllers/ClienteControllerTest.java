package com.example.supergestor.controllers;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.infrastructure.repositories.ClienteRepository;
import com.example.supergestor.infrastructure.repositories.FuncionarioRepository;
import com.example.supergestor.infrastructure.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import com.example.supergestor.utils.ConstantesRotasEndpoints;
import com.example.supergestor.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private ClienteRequestDTO createValidClienteRequestDTO(String cpfSuffix) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String uniqueCpf = "123456789" + cpfSuffix;
        String uniqueUsername = "client_user_" + uniqueSuffix;
        String uniqueEmail = "client_" + uniqueSuffix + "@test.com";

        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO(
                "Client Test " + uniqueSuffix,
                null,
                uniqueUsername,
                uniqueCpf,
                uniqueEmail,
                TestUtils.DEFAULT_RAW_PASSWORD
        );
        return new ClienteRequestDTO(LocalDate.now(), usuarioDTO);
    }

    private Cliente createAndSaveClientePrecondition(String name) {
        return testUtils.createAndSaveClientePrecondition(name);
    }

    @Test
    void testCreateClienteSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        ClienteRequestDTO requestDTO = createValidClienteRequestDTO("00");

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_CLIENTES)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.usuario.username").value(requestDTO.getUsuarioRequestDTO().getUsername()));
    }

    @Test
    void testCreateClienteConflictUserExists() throws Exception {
        Funcionario preconditionFunc = testUtils.createAndSaveFuncionarioPrecondition("conflict_func", "Cargo", BigDecimal.ONE);

        Map<String, String> authData = testUtils.authenticateAs(UserRole.FUNCIONARIO);
        String token = authData.get("token");

        String existingCpf = preconditionFunc.getUsuario().getCpf();

        ClienteRequestDTO requestDTO = createValidClienteRequestDTO("99");
        requestDTO.getUsuarioRequestDTO().setCpf(existingCpf);
        requestDTO.getUsuarioRequestDTO().setUsername("unique_user_name_for_conflict");
        requestDTO.getUsuarioRequestDTO().setEmail("unique_email_for_conflict@test.com");

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_CLIENTES)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("j\u00e1 cadastrado")));
    }

    @Test
    void testGetClienteByIdSuccess() throws Exception {
        Cliente savedCliente = createAndSaveClientePrecondition("client_for_get");

        Map<String, String> authAdmin = testUtils.createAndAuthenticateAdmin();
        String token = authAdmin.get("token");

        Long clienteId = savedCliente.getId();

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES + "/{id}", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.usuario.email").value(savedCliente.getUsuario().getEmail()));
    }

    @Test
    void testGetClienteByIdNotFound() throws Exception {

        Map<String, String> authData = testUtils.authenticateAs(UserRole.FUNCIONARIO);
        String token = authData.get("token");
        Long nonExistentId = 999L;

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES + "/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientesPaginadosSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");

        createAndSaveClientePrecondition("cli_pag_1");
        createAndSaveClientePrecondition("cli_pag_2");
        createAndSaveClientePrecondition("cli_pag_3");

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES )
                        .header("Authorization", "Bearer " + token)
                        .param("page", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(3));
    }
}