package com.example.lojix.utils.builders.dtos;

import com.example.lojix.dtos.produtos.ProdutoRequestDTO;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ProdutoRequestDTOBuilder {
    public static ProdutoRequestDTO criarValido() {
        return new ProdutoRequestDTO(
                "Smartphone X",
                new BigDecimal("1500.00"),
                "Descrição do produto",
                LocalDate.now().plusDays(10)
        );
    }
}
