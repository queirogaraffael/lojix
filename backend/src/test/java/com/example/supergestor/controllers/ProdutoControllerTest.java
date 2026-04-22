package com.example.supergestor.controllers;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.infrastructure.repositories.*;
import com.example.supergestor.dtos.produtos.ProdutoRequestDTO;
import com.example.supergestor.dtos.produtos.ProdutoUpdateDTO;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        categoria1 = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        categoria2 = categoriaRepository.save(new Categoria(null, "Roupas", null));
    }

    private ProdutoRequestDTO createValidProdutoRequestDTO() {
        return new ProdutoRequestDTO(
                "Smartphone X",
                new BigDecimal("1500.00"),
                "Descrição do produto",
                LocalDate.now().plusDays(10)
        );
    }

    private Produto createAndSaveProduto(String name, Categoria categoria, boolean ativo) {
        Produto produto = new Produto(null, name, new BigDecimal("10.00"), ativo, "Desc", LocalDate.now().plusDays(1), null, categoria);
        return produtoRepository.save(produto);
    }

    @Test
    void testCreateProdutoSuccess() throws Exception {

        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        ProdutoRequestDTO requestDTO = createValidProdutoRequestDTO();
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/categoria/{idCategoria}", categoria1.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value(requestDTO.getNome()))
                .andExpect(jsonPath("$.categoriaId").value(categoria1.getId()));
    }

    @Test
    void testCreateProdutoInvalidCategory() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        ProdutoRequestDTO requestDTO = createValidProdutoRequestDTO();
        String json = objectMapper.writeValueAsString(requestDTO);
        Long nonExistentId = 999L;

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/categoria/{idCategoria}", nonExistentId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProdutoByIdSuccess() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("get_prod");
        String token = authData.get("token");
        Produto savedProduto = createAndSaveProduto("Tablet Pro", categoria1, true);

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduto.getId()))
                .andExpect(jsonPath("$.nome").value(savedProduto.getNome()))
                .andExpect(jsonPath("$.categoriaId").value(categoria1.getId()));
    }

    @Test
    void testGetProdutosPaginadosSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        createAndSaveProduto("P1", categoria1, true);
        createAndSaveProduto("P2", categoria1, true);

        createAndSaveProduto("P3", categoria2, false);

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testGetProdutosPaginadosByCategoriaIdSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        createAndSaveProduto("P-Cat1-1", categoria1, true);
        createAndSaveProduto("P-Cat1-2", categoria1, true);
        createAndSaveProduto("P-Cat2-1", categoria2, true);

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/categoria/{idCategoria}", categoria1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/categoria/{idCategoria}", categoria2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void testUpdateProdutoSuccess() throws Exception {
        Map<String, String> authData = testUtils.authenticateAs(UserRole.ADMIN);
        String token = authData.get("token");
        Produto savedProduto = createAndSaveProduto("Old Name", categoria1, true);

        ProdutoUpdateDTO updateDTO = new ProdutoUpdateDTO(
                "New Name",
                new BigDecimal("20.00"),
                "New Desc",
                LocalDate.now().plusYears(1)
        );
        String json = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(put(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{idProduto}", savedProduto.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("New Name"))
                .andExpect(jsonPath("$.preco").value(20.00));
    }

    @Test
    void testDesativarProdutoSuccess() throws Exception {
        Map<String, String> authData = testUtils.createAndAuthenticateFuncionario("deact_prod");
        String token = authData.get("token");
        Produto savedProduto = createAndSaveProduto("Deactivate Product", categoria1, true);

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}/desativar", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}