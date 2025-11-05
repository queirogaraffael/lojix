package com.example.supergestor.shared.dtos.produtos;

import com.example.supergestor.shared.dtos.promocao.PromocaoProdutoResponseDTO;
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

    private PromocaoProdutoResponseDTO promocaoResponseDTO;
    private Long categoriaId;

}
