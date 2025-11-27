package com.example.supergestor.controllers;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.ClienteRepository;
import com.example.supergestor.domain.repositories.FuncionarioRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioUpdateDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioUpdateDTO;
import com.example.supergestor.utils.ConstantesRotasEndpoints;
import com.example.supergestor.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private FuncionarioRequestDTO createValidFuncionarioRequestDTO(String cpfSuffix) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String uniqueCpf = "123456789" + cpfSuffix;
        String uniqueUsername = "func_user_" + uniqueSuffix;
        String uniqueEmail = "func_" + uniqueSuffix + "@test.com";

        UsuarioRequestDTO usuarioDTO = new UsuarioRequestDTO(
                "Func Test " + uniqueSuffix,
                null,
                uniqueUsername,
                uniqueCpf,
                uniqueEmail,
                TestUtils.DEFAULT_RAW_PASSWORD
        );
        return new FuncionarioRequestDTO("Gerente", new BigDecimal("5000.00"), usuarioDTO);
    }

    private Funcionario createAndSaveFuncionarioPrecondition(String name) {
        return testUtils.createAndSaveFuncionarioPrecondition(name, "Vendedor", BigDecimal.valueOf(2500.00));
    }

    @Test
    void testCreateFuncionarioSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        FuncionarioRequestDTO requestDTO = createValidFuncionarioRequestDTO("00");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.cargo").value("Gerente"));
    }

    @Test
    void testCreateFuncionarioConflictUserExists() throws Exception {
        Funcionario preconditionFunc = createAndSaveFuncionarioPrecondition("pre_func");

        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");

        String existingCpf = preconditionFunc.getUsuario().getCpf();

        FuncionarioRequestDTO requestDTO = createValidFuncionarioRequestDTO("99");
        requestDTO.getUsuarioRequestDTO().setCpf(existingCpf);
        requestDTO.getUsuarioRequestDTO().setUsername("unique_user_name_for_conflict");
        requestDTO.getUsuarioRequestDTO().setEmail("unique_email_for_conflict@test.com");

        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("j\u00e1 cadastrado")));
    }


    @Test
    void testCreateFuncionarioUnauthorized() throws Exception {
        FuncionarioRequestDTO requestDTO = createValidFuncionarioRequestDTO("11");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateFuncionarioForbiddenForFunctionary() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("forbid_func");
        String token = authData.get("token");
        FuncionarioRequestDTO requestDTO = createValidFuncionarioRequestDTO("05");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetFuncionarioByIdSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Funcionario savedFuncionario = createAndSaveFuncionarioPrecondition("get_func");

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS + "/{id}", savedFuncionario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedFuncionario.getId()))
                .andExpect(jsonPath("$.cargo").value("Vendedor"));
    }

    @Test
    void testGetFuncionariosAtivosPaginadosSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");

        createAndSaveFuncionarioPrecondition("func1");
        Funcionario func2 = createAndSaveFuncionarioPrecondition("func2");
        createAndSaveFuncionarioPrecondition("func3");

        funcionarioRepository.atualizaStatusFuncionario(func2.getId(), false);

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testUpdateFuncionarioSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Funcionario savedFuncionario = createAndSaveFuncionarioPrecondition("update_func");

        FuncionarioUpdateDTO updateDTO = new FuncionarioUpdateDTO(
                "Supervisor",
                new BigDecimal("6000.00"),
                new UsuarioUpdateDTO("new_base64_photo")
        );
        String json = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(put(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS + "/{id}", savedFuncionario.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cargo").value("Supervisor"))
                .andExpect(jsonPath("$.salario").value(6000.00));
    }

    @Test
    void testDesligarFuncionarioSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Funcionario savedFuncionario = createAndSaveFuncionarioPrecondition("deactivate_func");

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS + "/{id}/desligar", savedFuncionario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Funcionario deactivated = funcionarioRepository.findById(savedFuncionario.getId()).get();
        assert(!deactivated.isAtivo());
    }
}