package com.example.supergestor.shared.dtos.usuario;

import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
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
