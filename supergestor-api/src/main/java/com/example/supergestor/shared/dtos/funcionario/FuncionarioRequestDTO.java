package com.example.supergestor.shared.dtos.funcionario;

import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuncionarioRequestDTO {

    private String cargo;
    private BigDecimal salario;

    @Valid
    @NotNull
    private UsuarioRequestDTO usuarioRequestDTO;

}
