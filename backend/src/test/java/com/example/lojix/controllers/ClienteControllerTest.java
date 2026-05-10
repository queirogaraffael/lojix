package com.example.lojix.controllers;

import com.example.lojix.domain.entities.Cliente;
import com.example.lojix.domain.entities.Funcionario;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repositories.ClienteRepository;
import com.example.lojix.infrastructure.repositories.FuncionarioRepository;
import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import com.example.lojix.dtos.cliente.ClienteRequestDTO;
import com.example.lojix.dtos.usuario.UsuarioRequestDTO;
import com.example.lojix.utils.ConstantesRotasEndpoints;
import com.example.lojix.utils.AuthTestFactory;
import com.example.lojix.utils.TestAuthContext;
import com.example.lojix.utils.builders.ClienteTestBuilder;
import com.example.lojix.utils.builders.FuncionarioTestBuilder;
import com.example.lojix.utils.builders.UsuarioTestBuilder;
import com.example.lojix.utils.builders.dtos.ClienteRequestDTOBuilder;
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
    private AuthTestFactory authTestFactory;

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

    @Test
    void testCreateClienteSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        ClienteRequestDTO requestDTO = ClienteRequestDTOBuilder.criarValido("00");

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
        Funcionario preconditionFunc = FuncionarioTestBuilder.novo()
                .comCargo("Cargo")
                .comSalario(BigDecimal.ONE)
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("conflict_func").comRole(UserRole.FUNCIONARIO))
                .build();
        funcionarioRepository.save(preconditionFunc);

        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();

        String existingCpf = preconditionFunc.getUsuario().getCpf();

        ClienteRequestDTO requestDTO = ClienteRequestDTOBuilder.criarValido("99");
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
        Cliente c = ClienteTestBuilder.novo()
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("client_for_get").comRole(UserRole.CLIENTE))
                .build();
        Cliente savedCliente = clienteRepository.save(c);

        TestAuthContext authAdmin = authTestFactory.authenticateAsAdmin();
        String token = authAdmin.token();

        Long clienteId = savedCliente.getId();

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES + "/{id}", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteId))
                .andExpect(jsonPath("$.usuario.email").value(savedCliente.getUsuario().getEmail()));
    }

    @Test
    void testGetClienteByIdNotFound() throws Exception {

        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        Long nonExistentId = 999L;

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CLIENTES + "/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClientesPaginadosSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        clienteRepository.save(ClienteTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("cli_pag_1").comRole(UserRole.CLIENTE)).build());
        clienteRepository.save(ClienteTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("cli_pag_2").comRole(UserRole.CLIENTE)).build());
        clienteRepository.save(ClienteTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("cli_pag_3").comRole(UserRole.CLIENTE)).build());

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