package com.example.supergestor.dtos.funcionario;

import com.example.supergestor.dtos.usuario.UsuarioRequestDTO;
import jakarta.validation.Valid;
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
public class FuncionarioRequestDTO {

    @NotBlank(message = "O cargo é obrigatório.")
    private String cargo;

    @NotNull(message = "O salário é obrigatório.")
    @DecimalMin(value = "0.0", inclusive = true, message = "O salário deve ser um valor positivo ou zero.")
    private BigDecimal salario;

    @Valid
    @NotNull(message = "Os dados do usuário são obrigatórios.")
    private UsuarioRequestDTO usuarioRequestDTO;
}
