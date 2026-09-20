package com.example.lojix.util.builder.dto;

import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.dto.funcionario.FuncionarioRequestDTO;
import java.math.BigDecimal;

public class FuncionarioRequestDTOBuilder {

    private UserRole role = UserRole.ATENDENTE;

    public static FuncionarioRequestDTO criarValido(String cpfSuffix) {
        return new FuncionarioRequestDTO(
                "Gerente",
                new BigDecimal("5000.00"),
                UserRole.ATENDENTE,
                UsuarioRequestDTOBuilder.criarValido("Func", cpfSuffix)
        );
    }

    public FuncionarioRequestDTOBuilder comRole(UserRole role) {
        this.role = role;
        return this;
    }
}
