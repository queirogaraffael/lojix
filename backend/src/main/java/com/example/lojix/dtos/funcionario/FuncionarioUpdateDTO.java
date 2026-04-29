package com.example.lojix.dtos.funcionario;

import com.example.lojix.dtos.usuario.UsuarioUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioUpdateDTO {

    private String cargo;
    private BigDecimal salario;

    private UsuarioUpdateDTO usuarioUpdateDTO;
}
