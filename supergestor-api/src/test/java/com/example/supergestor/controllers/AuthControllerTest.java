package com.example.supergestor.controllers;

import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.ClienteRepository;
import com.example.supergestor.domain.repositories.FuncionarioRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.auth.LoginDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestUtils testUtils;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("login_success");
        String username = authData.get("username");

        LoginDTO loginDTO = new LoginDTO(username, TestUtils.DEFAULT_RAW_PASSWORD);
        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void testLoginFailureInvalidPassword() throws Exception {

        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("invalid_pass");
        String username = authData.get("username");

        LoginDTO loginDTO = new LoginDTO(username, "senhaErrada");
        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginFailureInvalidUsername() throws Exception {
        LoginDTO loginDTO = new LoginDTO("usuarioInexistente", TestUtils.DEFAULT_RAW_PASSWORD);
        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserContextAdmin() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.id").isEmpty())
                .andExpect(jsonPath("$.funcionario.id").isEmpty());
    }

    @Test
    void testGetUserContextFuncionario() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("get_func");
        String token = authData.get("token");
        String username = authData.get("username");
        BigDecimal expectedSalario = BigDecimal.TEN;

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.id").isEmpty())
                .andExpect(jsonPath("$.funcionario").exists())
                .andExpect(jsonPath("$.funcionario.usuarioResponseDTO.username").value(username))
                .andExpect(jsonPath("$.funcionario.cargo").value("Funcao Teste"))
                .andExpect(jsonPath("$.funcionario.salario").value(expectedSalario.doubleValue()));
    }

    @Test
    void testGetUserContextForbidden() throws Exception {
        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me"))
                .andExpect(status().isUnauthorized());
    }
}