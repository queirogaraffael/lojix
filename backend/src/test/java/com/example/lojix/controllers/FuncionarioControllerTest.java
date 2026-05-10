package com.example.lojix.controllers;

import com.example.lojix.domain.entities.Funcionario;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repositories.ClienteRepository;
import com.example.lojix.infrastructure.repositories.FuncionarioRepository;
import com.example.lojix.infrastructure.repositories.UsuarioRepository;
import com.example.lojix.dtos.funcionario.FuncionarioRequestDTO;
import com.example.lojix.dtos.funcionario.FuncionarioUpdateDTO;
import com.example.lojix.dtos.usuario.UsuarioRequestDTO;
import com.example.lojix.dtos.usuario.UsuarioUpdateDTO;
import com.example.lojix.utils.ConstantesRotasEndpoints;
import com.example.lojix.utils.AuthTestFactory;
import com.example.lojix.utils.TestAuthContext;
import com.example.lojix.utils.builders.FuncionarioTestBuilder;
import com.example.lojix.utils.builders.UsuarioTestBuilder;
import com.example.lojix.utils.builders.dtos.FuncionarioRequestDTOBuilder;
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

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthTestFactory authTestFactory;

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

    @Test
    void testCreateFuncionarioSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        FuncionarioRequestDTO requestDTO = FuncionarioRequestDTOBuilder.criarValido("00");
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
        Funcionario preconditionFunc = FuncionarioTestBuilder.novo()
                .comCargo("Vendedor")
                .comSalario(BigDecimal.valueOf(2500.00))
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("pre_func").comRole(UserRole.FUNCIONARIO))
                .build();
        funcionarioRepository.save(preconditionFunc);

        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        String existingCpf = preconditionFunc.getUsuario().getCpf();

        FuncionarioRequestDTO requestDTO = FuncionarioRequestDTOBuilder.criarValido("99");
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
        FuncionarioRequestDTO requestDTO = FuncionarioRequestDTOBuilder.criarValido("11");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateFuncionarioForbiddenForFunctionary() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        FuncionarioRequestDTO requestDTO = FuncionarioRequestDTOBuilder.criarValido("05");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetFuncionarioByIdSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Funcionario savedFuncionario = FuncionarioTestBuilder.novo()
                .comCargo("Vendedor")
                .comSalario(BigDecimal.valueOf(2500.00))
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("get_func").comRole(UserRole.FUNCIONARIO))
                .build();
        funcionarioRepository.save(savedFuncionario);

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS + "/{id}", savedFuncionario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedFuncionario.getId()))
                .andExpect(jsonPath("$.cargo").value("Vendedor"));
    }

    @Test
    void testGetFuncionariosAtivosPaginadosSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        funcionarioRepository.save(FuncionarioTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("func1").comRole(UserRole.FUNCIONARIO)).build());
        Funcionario func2 = funcionarioRepository.save(FuncionarioTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("func2").comRole(UserRole.FUNCIONARIO)).build());
        funcionarioRepository.save(FuncionarioTestBuilder.novo().comUsuarioBuilder(UsuarioTestBuilder.novo().comName("func3").comRole(UserRole.FUNCIONARIO)).build());

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
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Funcionario savedFuncionario = FuncionarioTestBuilder.novo()
                .comCargo("Vendedor")
                .comSalario(BigDecimal.valueOf(2500.00))
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("update_func").comRole(UserRole.FUNCIONARIO))
                .build();
        funcionarioRepository.save(savedFuncionario);

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
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Funcionario savedFuncionario = FuncionarioTestBuilder.novo()
                .comCargo("Vendedor")
                .comSalario(BigDecimal.valueOf(2500.00))
                .comUsuarioBuilder(UsuarioTestBuilder.novo().comName("deactivate_func").comRole(UserRole.FUNCIONARIO))
                .build();
        funcionarioRepository.save(savedFuncionario);

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_FUNCIONARIOS + "/{id}/desligar", savedFuncionario.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Funcionario deactivated = funcionarioRepository.findById(savedFuncionario.getId()).get();
        assert(!deactivated.isAtivo());
    }
}