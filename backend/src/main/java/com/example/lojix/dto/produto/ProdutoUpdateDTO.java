package com.example.lojix.dto.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoUpdateDTO {

    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String nome;

    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal preco;

    @Size(max = 255, message = "A descrição pode ter no máximo 255 caracteres.")
    private String descricao;

    @Future(message = "A data de validade deve ser uma data futura.")
    private LocalDate dataValidade;
}
