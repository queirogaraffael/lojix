package com.example.lojix.dto.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
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

    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal preco;

    @Size(max = 255, message = "A descrição não pode ter mais que 255 caracteres.")
    private String descricao;

    @Future(message = "A data de validade deve estar no futuro.")
    private LocalDate dataValidade;

    @Min(value = 0, message = "O estoque não pode ser negativo")
    private Integer quantidadeEstoque;
}
