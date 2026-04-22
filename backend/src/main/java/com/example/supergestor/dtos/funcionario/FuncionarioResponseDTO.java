package com.example.supergestor.dtos.funcionario;

import com.example.supergestor.dtos.usuario.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioResponseDTO {

    private Long id;
    private String cargo;
    private BigDecimal salario;
    private UsuarioResponseDTO usuarioResponseDTO;
}
