package com.example.supergestor.controllers;

import com.example.supergestor.domain.services.PromocaoService;
import com.example.supergestor.shared.dtos.promocao.PromocaoRequestDTO;
import com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Promocao")
@RestController
@RequestMapping("/api/promocoes")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(PromocaoService promocaoService) {
        this.promocaoService = promocaoService;
    }

    @Operation(
            summary = "Criar nova promoção",
            description = "Cria uma nova promoção. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promoção criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor | Erro de regra de negócio")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<PromocaoResponseDTO> createPromocao(
            @RequestBody PromocaoRequestDTO promocaoRequestDTO
    ) {
        PromocaoResponseDTO promocaoCriada = promocaoService.createPromocao(promocaoRequestDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(promocaoCriada.getId())
                .toUri();

        return ResponseEntity.created(uri).body(promocaoCriada);
    }

    @Operation(
            summary = "Desativar promoção",
            description = "Desativa uma promoção pelo ID. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoção desativada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Promoção não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{idPromocao}/desativar")
    public ResponseEntity<Void> desativarPromocao(@PathVariable Long idPromocao) {
        promocaoService.desativarPromocaoById(idPromocao);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Associar promoção a produto",
            description = "Vincula uma promoção a um produto específico. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoção associada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto ou promoção não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{idPromocao}/associar/{idProduto}")
    public ResponseEntity<Void> associarPromocao(
            @PathVariable Long idPromocao,
            @PathVariable Long idProduto
    ) {
        promocaoService.associarPromocaoAProduto(idProduto, idPromocao);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remover promoção de produto",
            description = "Remove a promoção vinculada de um produto específico. **Roles permitidos:** ADMIN, FUNCIONARIO"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoção removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/remover/{idProduto}")
    public ResponseEntity<Void> removerPromocaoProduto(@PathVariable Long idProduto) {
        promocaoService.removerPromocaoProduto(idProduto);
        return ResponseEntity.noContent().build();
    }
}
