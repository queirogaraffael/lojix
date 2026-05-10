package com.example.lojix.controllers;

import com.example.lojix.domain.entities.Categoria;
import com.example.lojix.domain.entities.Produto;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repositories.*;
import com.example.lojix.dtos.produtos.ProdutoRequestDTO;
import com.example.lojix.dtos.produtos.ProdutoUpdateDTO;
import com.example.lojix.utils.ConstantesRotasEndpoints;
import com.example.lojix.utils.AuthTestFactory;
import com.example.lojix.utils.TestAuthContext;
import com.example.lojix.utils.builders.dtos.ProdutoRequestDTOBuilder;
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
    private AuthTestFactory authTestFactory;

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



    @BeforeEach
    void setup() {
        funcionarioRepository.deleteAll();
        clienteRepository.deleteAll();
        produtoRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testCreateProdutoSuccess() throws Exception {

        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        ProdutoRequestDTO requestDTO = ProdutoRequestDTOBuilder.criarValido();
        String json = objectMapper.writeValueAsString(requestDTO);
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/categoria/{idCategoria}", categoria.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value(requestDTO.getNome()))
                .andExpect(jsonPath("$.categoriaId").value(categoria.getId()));
    }

    @Test
    void testCreateProdutoInvalidCategory() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        ProdutoRequestDTO requestDTO = ProdutoRequestDTOBuilder.criarValido();
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
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        Produto savedProduto = produtoRepository.save(new Produto(null, "Tablet Pro", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria));

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduto.getId()))
                .andExpect(jsonPath("$.nome").value(savedProduto.getNome()))
                .andExpect(jsonPath("$.categoriaId").value(categoria.getId()));
    }

    @Test
    void testGetProdutosPaginadosSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Categoria categoria1 = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        Categoria categoria2 = categoriaRepository.save(new Categoria(null, "Roupas", null));

        produtoRepository.save(new Produto(null, "P1", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria1));
        produtoRepository.save(new Produto(null, "P2", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria1));

        produtoRepository.save(new Produto(null, "P3", new BigDecimal("10.00"), false, "Desc", LocalDate.now().plusDays(1), null, categoria2));

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
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Categoria categoria1 = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        Categoria categoria2 = categoriaRepository.save(new Categoria(null, "Roupas", null));

        produtoRepository.save(new Produto(null, "P-Cat1-1", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria1));
        produtoRepository.save(new Produto(null, "P-Cat1-2", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria1));
        produtoRepository.save(new Produto(null, "P-Cat2-1", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria2));

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
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        Produto savedProduto = produtoRepository.save(new Produto(null, "Old Name", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria));

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
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Eletrônicos", null));
        Produto savedProduto = produtoRepository.save(new Produto(null, "Deactivate Product", new BigDecimal("10.00"), true, "Desc", LocalDate.now().plusDays(1), null, categoria));

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}/desativar", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PRODUTOS + "/{id}", savedProduto.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}