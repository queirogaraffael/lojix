package com.example.supergestor.shared.dtos.produtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoUpdateDTO {
    private String nome;
    private BigDecimal preco;
    private String descricao;
    private LocalDate dataValidade;
    private Long promocaoId;
}