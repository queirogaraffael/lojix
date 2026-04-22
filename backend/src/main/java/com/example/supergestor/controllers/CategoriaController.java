package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.CategoriaService;
import com.example.supergestor.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.dtos.categoria.CategoriaUpdateDTO;
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

@Tag(name = "Categoria")
@RestController
@RequestMapping("/api/categorias")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
            summary = "Criar nova categoria",
            description = "Cria uma nova categoria no sistema"
    )
    @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de regra de negócio")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(
            @RequestBody @Valid CategoriaRequestDTO categoriaRequestDTO
    ) {
        CategoriaResponseDTO categoriaCriada = categoriaService.criarCategoria(categoriaRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(categoriaCriada.getId())
                .toUri();

        return ResponseEntity.created(uri).body(categoriaCriada);
    }

    @Operation(
            summary = "Buscar categoria por ID",
            description = "Recupera uma categoria pelo seu identificador"
    )
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> getCategoriaById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }

    @Operation(
            summary = "Listar categorias paginadas",
            description = "Retorna categorias de forma paginada"
    )
    @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> getCategoriasPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(categoriaService.getCategoriasPaginados(page, size));
    }

    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza os dados de uma categoria existente"
    )
    @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> updateCategoria(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaUpdateDTO categoriaUpdateDTO
    ) {
        return ResponseEntity.ok(categoriaService.updateCategoria(id, categoriaUpdateDTO));
    }
}

