package com.example.lojix.util.builder.dto;

import com.example.lojix.dto.funcionario.FuncionarioRequestDTO;
import java.math.BigDecimal;

public class FuncionarioRequestDTOBuilder {
    public static FuncionarioRequestDTO criarValido(String cpfSuffix) {
        return new FuncionarioRequestDTO(
                "Gerente",
                new BigDecimal("5000.00"),
                UsuarioRequestDTOBuilder.criarValido("Func", cpfSuffix)
        );
    }
}
