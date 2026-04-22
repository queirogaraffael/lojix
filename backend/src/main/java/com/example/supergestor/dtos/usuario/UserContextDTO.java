package com.example.supergestor.dtos.usuario;

import com.example.supergestor.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioResponseDTO;
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
