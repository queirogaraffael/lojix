package com.example.lojix.dtos.usuario;

import com.example.lojix.dtos.cliente.ClienteResponseDTO;
import com.example.lojix.dtos.funcionario.FuncionarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextDTO {

    private ClienteResponseDTO cliente;
    private FuncionarioResponseDTO funcionario;
}
