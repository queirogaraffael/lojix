package com.example.lojix.utils.builders.dtos;

import com.example.lojix.dtos.promocao.PromocaoRequestDTO;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PromocaoRequestDTOBuilder {
    public static PromocaoRequestDTO criarValido(String name) {
        return new PromocaoRequestDTO(
                name,
                new BigDecimal("0.15"),
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );
    }
}
