package com.example.lojix.dtos.produtos;

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
    private BigDecimal preco;

    private String descricao;
    private LocalDate dataValidade;

    private Long promocaoId;
    private Long categoriaId;

}
