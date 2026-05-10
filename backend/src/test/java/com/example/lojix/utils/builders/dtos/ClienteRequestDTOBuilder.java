package com.example.lojix.utils.builders.dtos;

import com.example.lojix.dtos.cliente.ClienteRequestDTO;
import java.time.LocalDate;

public class ClienteRequestDTOBuilder {
    public static ClienteRequestDTO criarValido(String cpfSuffix) {
        return new ClienteRequestDTO(
                LocalDate.now(),
                UsuarioRequestDTOBuilder.criarValido("Client", cpfSuffix)
        );
    }
}
