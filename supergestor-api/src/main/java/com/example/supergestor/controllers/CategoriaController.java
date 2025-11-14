package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.CategoriaService;
import com.example.supergestor.shared.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Categoria")
@RestController
@RequestMapping("/api/categorias")
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
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de validação de negócio")
    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(@RequestBody CategoriaRequestDTO categoriaRequestDTO) {
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
            description = "Retorna uma categoria específica usando seu ID"
    )
    @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponseDTO> getCategoriaById(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(idCategoria));
    }

    @Operation(
            summary = "Listar categorias paginadas",
            description = "Retorna uma lista paginada de categorias"
    )
    @ApiResponse(responseCode = "200", description = "Categorias retornadas com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> getCategoriasPaginados(
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
    @PutMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponseDTO> updateCategoria(
            @PathVariable Long idCategoria,
            @RequestBody CategoriaUpdateDTO categoriaUpdateDTO
    ) {
        return ResponseEntity.ok(categoriaService.updateCategoria(idCategoria, categoriaUpdateDTO));
    }
}
