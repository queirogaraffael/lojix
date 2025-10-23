package com.example.supergestor.shared.dtos.funcionario;

import com.example.supergestor.shared.dtos.usuario.UsuarioUpdateDTO;

import java.math.BigDecimal;

public class FuncionarioUpdateDTO {

    private String cargo;
    private BigDecimal salario;

    private UsuarioUpdateDTO usuario;
}
