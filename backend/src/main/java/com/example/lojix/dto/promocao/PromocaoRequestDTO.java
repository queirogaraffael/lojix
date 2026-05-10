package com.example.lojix.dto.promocao;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromocaoRequestDTO {

    @NotBlank(message = "O nome da promoção é obrigatório.")
    @Size(max = 100, message = "O nome não pode exceder 100 caracteres.")
    private String nome;

    @NotNull(message = "A taxa de desconto é obrigatória.")
    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de desconto deve ser no mínimo 0.")
    @DecimalMax(value = "1.0", inclusive = true, message = "A taxa de desconto deve ser no máximo 1 (100%).")
    private BigDecimal taxaDeDesconto;

    @NotNull(message = "A data de início é obrigatória.")
    private LocalDate inicio;

    @NotNull(message = "A data de fim é obrigatória.")
    private LocalDate fim;
}
