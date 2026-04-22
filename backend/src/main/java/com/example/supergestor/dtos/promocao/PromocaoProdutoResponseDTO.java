package com.example.supergestor.dtos.promocao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromocaoProdutoResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal taxaDeDesconto;
}
