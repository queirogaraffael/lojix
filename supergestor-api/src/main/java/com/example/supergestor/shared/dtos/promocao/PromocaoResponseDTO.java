package com.example.supergestor.shared.dtos.promocao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromocaoResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal taxaDeDesconto;
    private LocalDate inicio;
    private LocalDate fim;
}
