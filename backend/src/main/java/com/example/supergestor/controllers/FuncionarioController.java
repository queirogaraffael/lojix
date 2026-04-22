package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.FuncionarioService;
import com.example.supergestor.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioUpdateDTO;
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

@Tag(name = "Funcionario")
@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @Operation(
            summary = "Criar novo funcionário",
            description = "Cria um novo funcionário no sistema. **Role permitido:** ADMIN"
    )
    @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de regra de negócio")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<FuncionarioResponseDTO> createFuncionario(
            @RequestBody @Valid FuncionarioRequestDTO funcionarioRequestDTO
    ) {
        FuncionarioResponseDTO funcionarioCriado = funcionarioService.createFuncionario(funcionarioRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(funcionarioCriado.getId())
                .toUri();

        return ResponseEntity.created(uri).body(funcionarioCriado);
    }

    @Operation(
            summary = "Buscar funcionário por ID",
            description = "Retorna um funcionário pelo ID informado. **Role permitido:** ADMIN"
    )
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> getFuncionarioById(@PathVariable Long id) {
        return ResponseEntity.ok(funcionarioService.getFuncionarioById(id));
    }

    @Operation(
            summary = "Listar funcionários ativos",
            description = "Retorna funcionários ativos de forma paginada. **Role permitido:** ADMIN"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<FuncionarioResponseDTO>> getFuncionariosAtivosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(funcionarioService.getFuncionariosAtivosPaginados(page, size));
    }

    @Operation(
            summary = "Atualizar funcionário",
            description = "Atualiza os dados de um funcionário pelo ID informado. **Role permitido:** ADMIN"
    )
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDTO> updateFuncionario(
            @PathVariable Long id,
            @RequestBody @Valid FuncionarioUpdateDTO funcionarioUpdateDTO
    ) {
        return ResponseEntity.ok(funcionarioService.updateFuncionario(id, funcionarioUpdateDTO));
    }

    @Operation(
            summary = "Desligar funcionário",
            description = "Marca o funcionário como desligado no sistema. **Role permitido:** ADMIN"
    )
    @ApiResponse(responseCode = "204", description = "Funcionário desligado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{id}/desligar")
    public ResponseEntity<Void> desligarFuncionarioById(@PathVariable Long id) {
        funcionarioService.desligarFuncionarioById(id);
        return ResponseEntity.noContent().build();
    }
}
