package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.ClienteService;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
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

@Tag(name = "Cliente")
@RestController
@RequestMapping("/api/clientes")
@SecurityRequirement(name = "Bearer Authentication")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(
            summary = "Criar novo cliente",
            description = "Cria um novo cliente no sistema"
    )
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de regra de negócio")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<ClienteResponseDTO> createCliente(@RequestBody @Valid ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO clienteCriado = clienteService.createCliente(clienteRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clienteCriado.getId())
                .toUri();

        return ResponseEntity.created(uri).body(clienteCriado);
    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os dados de um cliente específico"
    )
    @ApiResponse(responseCode = "200", description = "Cliente encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.getClienteById(id));
    }

    @Operation(
            summary = "Listar clientes paginados",
            description = "Retorna clientes de forma paginada"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> getClientesPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clienteService.getClientePaginados(page, size));
    }
}
