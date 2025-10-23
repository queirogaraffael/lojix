package com.example.supergestor.shared.dtos.produtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto é obrigatório.")
    @Size(max = 255, message = "O nome não pode exceder 255 caracteres.")
    private String nome;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser um valor positivo")
    private BigDecimal preco;

    @Size(max = 1000, message = "A descrição não pode exceder 1000 caracteres.")
    private String descricao;

    @NotNull(message = "A data de validade é obrigatória.")
    private LocalDate dataValidade;
}
