package com.example.lojix.controllers;

import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repositories.ClienteRepository;
import com.example.lojix.infrastructure.repositories.FuncionarioRepository;
import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import com.example.lojix.dtos.auth.LoginDTO;
import com.example.lojix.utils.ConstantesRotasEndpoints;
import com.example.lojix.utils.AuthTestFactory;
import com.example.lojix.utils.TestAuthContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
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
    private AuthTestFactory authTestFactory;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testLoginSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String username = authData.username();

        LoginDTO loginDTO = new LoginDTO(username, authData.rawPassword());
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

        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String username = authData.username();

        LoginDTO loginDTO = new LoginDTO(username, "senhaErrada");
        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginFailureInvalidUsername() throws Exception {
        LoginDTO loginDTO = new LoginDTO("usuarioInexistente", "password123");
        String json = objectMapper.writeValueAsString(loginDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetUserContextAdmin() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").isEmpty())
                .andExpect(jsonPath("$.funcionarioId").isEmpty());
    }

    @Test
    void testGetUserContextFuncionario() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        String username = authData.username();

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").isEmpty())
                .andExpect(jsonPath("$.funcionarioId").isNumber())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value(UserRole.FUNCIONARIO.name()));
    }

    @Test
    void testGetUserContextForbidden() throws Exception {
        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_AUTENTICACAO + "/me"))
                .andExpect(status().isUnauthorized());
    }
}