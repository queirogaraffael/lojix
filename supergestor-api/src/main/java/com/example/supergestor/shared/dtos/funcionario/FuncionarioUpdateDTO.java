package com.example.supergestor.shared.dtos.funcionario;

import com.example.supergestor.shared.dtos.usuario.UsuarioUpdateDTO;
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
