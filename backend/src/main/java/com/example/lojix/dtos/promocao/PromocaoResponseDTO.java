package com.example.lojix.dtos.promocao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromocaoResponseDTO extends RepresentationModel<PromocaoResponseDTO> {
    private Long id;
    private String nome;
    private BigDecimal taxaDeDesconto;
    private LocalDate inicio;
    private LocalDate fim;
}
