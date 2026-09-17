package com.example.lojix.dto.funcionario;


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
}
