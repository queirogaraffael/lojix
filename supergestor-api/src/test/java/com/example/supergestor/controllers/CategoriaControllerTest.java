package com.example.supergestor.controllers;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.infrastructure.repositories.*;
import com.example.supergestor.shared.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaUpdateDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        usuarioRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    private Categoria createAndSaveCategoria(String name) {
        Categoria categoria = new Categoria(null, name, null);
        return categoriaRepository.save(categoria);
    }

    @Test
    void testCreateCategoriaSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");

        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Eletrônicos");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_CATEGORIAS)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Eletrônicos"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void testCreateCategoriaUnauthenticated() throws Exception {
        CategoriaRequestDTO requestDTO = new CategoriaRequestDTO("Eletrônicos");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_CATEGORIAS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCategoriaByIdSuccess() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("get_cat");
        String token = authData.get("token");
        Categoria savedCategoria = createAndSaveCategoria("Livros");

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CATEGORIAS + "/{id}", savedCategoria.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategoria.getId()))
                .andExpect(jsonPath("$.nome").value("Livros"));
    }

    @Test
    void testGetCategoriaByIdNotFound() throws Exception {

        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Long nonExistentId = 999L;

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CATEGORIAS + "/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCategoriasPaginadosSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        createAndSaveCategoria("C1");
        createAndSaveCategoria("C2");

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_CATEGORIAS)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testUpdateCategoriaSuccess() throws Exception {

        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Categoria savedCategoria = createAndSaveCategoria("Antigo Nome");

        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");
        String json = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(put(ConstantesRotasEndpoints.ROTA_CATEGORIAS + "/{id}", savedCategoria.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo Nome"))
                .andExpect(jsonPath("$.id").value(savedCategoria.getId()));
    }

    @Test
    void testUpdateCategoriaForbiddenForClient() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateCliente("forbid_cat");
        String token = authData.get("token");
        Categoria savedCategoria = createAndSaveCategoria("Nome");

        CategoriaUpdateDTO updateDTO = new CategoriaUpdateDTO("Novo Nome");
        String json = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(put(ConstantesRotasEndpoints.ROTA_CATEGORIAS + "/{id}", savedCategoria.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}