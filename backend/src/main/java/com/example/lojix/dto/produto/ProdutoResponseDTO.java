package com.example.lojix.dto.produto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoResponseDTO {
    private Long id;

    private String nome;
    private BigDecimal precoBase;
    private BigDecimal precoPromocional;
    private boolean emPromocao;
    private String nomePromocao;

    private String descricao;
    private LocalDate dataValidade;
    
    private Integer quantidadeEstoque;
    
    private Long promocaoId;
    private Long categoriaId;

}
