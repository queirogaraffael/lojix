package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.ProdutoService;
import com.example.supergestor.shared.dtos.produtos.ProdutoRequestDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Produto")
@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(
            summary = "Criar novo produto",
            description = "Cria um novo produto vinculado a uma categoria. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de regra de negócio")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/categoria/{idCategoria}")
    public ResponseEntity<ProdutoResponseDTO> createProduto(
            @PathVariable Long idCategoria,
            @RequestBody @Valid ProdutoRequestDTO produtoRequestDTO
    ) {
        ProdutoResponseDTO produtoCriado = produtoService.createProduto(idCategoria, produtoRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(produtoCriado.getId())
                .toUri();

        return ResponseEntity.created(uri).body(produtoCriado);
    }

    @Operation(
            summary = "Buscar produto por ID",
            description = "Retorna um produto ativo pelo ID informado. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> getProdutoById(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.getProdutoById(id));
    }

    @Operation(
            summary = "Listar produtos paginados",
            description = "Retorna produtos ativos com paginação. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> getProdutosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(produtoService.getProdutosPaginados(page, size));
    }

    @Operation(
            summary = "Listar produtos por categoria",
            description = "Retorna produtos ativos de uma categoria específica. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<Page<ProdutoResponseDTO>> getProdutosPaginadosByCategoriaId(
            @PathVariable Long idCategoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(produtoService.getProdutosPaginadosByCategoriaId(idCategoria, page, size));
    }

    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto pelo ID informado. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{idProduto}")
    public ResponseEntity<ProdutoResponseDTO> updateProdutoById(
            @PathVariable Long idProduto,
            @RequestBody @Valid ProdutoUpdateDTO produtoUpdateDTO
    ) {
        return ResponseEntity.ok(produtoService.updateProdutoById(idProduto, produtoUpdateDTO));
    }

    @Operation(
            summary = "Desativar produto",
            description = "Desativa um produto pelo ID informado. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponse(responseCode = "204", description = "Produto desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativarProdutoById(@PathVariable Long id) {
        produtoService.desativarProdutoById(id);
        return ResponseEntity.noContent().build();
    }
}
