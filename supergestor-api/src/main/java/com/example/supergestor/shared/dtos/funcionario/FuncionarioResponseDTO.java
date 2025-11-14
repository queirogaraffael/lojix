package com.example.supergestor.shared.dtos.funcionario;

import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
