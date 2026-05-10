package com.example.lojix.controllers;

import com.example.lojix.domain.entities.Categoria;
import com.example.lojix.domain.entities.Produto;
import com.example.lojix.domain.entities.Promocao;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repositories.*;
import com.example.lojix.dtos.promocao.PromocaoRequestDTO;
import com.example.lojix.utils.ConstantesRotasEndpoints;
import com.example.lojix.utils.AuthTestFactory;
import com.example.lojix.utils.TestAuthContext;
import com.example.lojix.utils.builders.dtos.PromocaoRequestDTOBuilder;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PromocaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthTestFactory authTestFactory;

    @Autowired
    private PromocaoRepository promocaoRepository;

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
        promocaoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void testCreatePromocaoSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        PromocaoRequestDTO requestDTO = PromocaoRequestDTOBuilder.criarValido("Promo Teste");
        String json = objectMapper.writeValueAsString(requestDTO);

        mockMvc.perform(post(ConstantesRotasEndpoints.ROTA_PROMOCOES)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Promo Teste"))
                .andExpect(jsonPath("$.taxaDeDesconto").value(0.15));
    }

    @Test
    void testDesativarPromocaoSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();
        Promocao savedPromocao = promocaoRepository.save(new Promocao(null, "Promo to Deactivate", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/{idPromocao}/desativar", savedPromocao.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Promocao deactivated = promocaoRepository.findById(savedPromocao.getId()).get();
        assert(deactivated.getAtivada().equals(false));
    }

    @Test
    void testAssociarPromocaoAProdutoSuccess() throws Exception {

        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Diversos", null));
        Produto produto1 = produtoRepository.save(new Produto(null, "P1", BigDecimal.TEN, true, "D1", LocalDate.now().plusDays(1), null, categoria));
        Promocao savedPromocao = promocaoRepository.save(new Promocao(null, "Promo for Product", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PROMOCOES+ "/{idPromocao}/associar/{idProduto}", savedPromocao.getId(), produto1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Produto updatedProduto = produtoRepository.findById(produto1.getId()).get();
        assert(updatedProduto.getPromocao().getId().equals(savedPromocao.getId()));
    }

    @Test
    void testRemoverPromocaoProdutoSuccess() throws Exception {

        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Promocao savedPromocao = promocaoRepository.save(new Promocao(null, "Promo to Remove", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));

        Categoria categoria = categoriaRepository.save(new Categoria(null, "Diversos", null));
        Produto produto2 = produtoRepository.save(new Produto(null, "P2", BigDecimal.ONE, true, "D2", LocalDate.now().plusDays(1), null, categoria));

        Produto produtoWithPromo = produtoRepository.findById(produto2.getId()).get();
        produtoWithPromo.setPromocao(savedPromocao);
        produtoRepository.save(produtoWithPromo);

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/remover/{idProduto}", produto2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Produto updatedProduto = produtoRepository.findById(produto2.getId()).get();
        assert(updatedProduto.getPromocao() == null);
    }

    @Test
    void testAssociarPromocaoAProdutoNotFound() throws Exception {

        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Diversos", null));
        Produto produto1 = produtoRepository.save(new Produto(null, "P1", BigDecimal.TEN, true, "D1", LocalDate.now().plusDays(1), null, categoria));
        Promocao savedPromocao = promocaoRepository.save(new Promocao(null, "Promo for Product", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));
        Long nonExistentId = 999L;

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/{idPromocao}/associar/{idProduto}", savedPromocao.getId(), nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/{idPromocao}/associar/{idProduto}", nonExistentId, produto1.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPromocaoByIdSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        Promocao savedPromocao = promocaoRepository.save(new Promocao(null, "Promo Buscar ID", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/{id}", savedPromocao.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPromocao.getId()))
                .andExpect(jsonPath("$.nome").value("Promo Buscar ID"))
                .andExpect(jsonPath("$.taxaDeDesconto").value(0.15));
    }


    @Test
    void testGetPromocaoByIdNotFound() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsFuncionario();
        String token = authData.token();

        Long nonExistentId = 999L;

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PROMOCOES + "/{id}", nonExistentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPromocoesAtivasPaginadasSuccess() throws Exception {
        TestAuthContext authData = authTestFactory.authenticateAsAdmin();
        String token = authData.token();

        promocaoRepository.save(new Promocao(null, "Promo 1", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));
        promocaoRepository.save(new Promocao(null, "Promo 2", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));
        promocaoRepository.save(new Promocao(null, "Promo 3", new BigDecimal("0.15"), LocalDate.now(), LocalDate.now().plusDays(30), true, null));

        mockMvc.perform(get(ConstantesRotasEndpoints.ROTA_PROMOCOES)
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(3));
    }
}