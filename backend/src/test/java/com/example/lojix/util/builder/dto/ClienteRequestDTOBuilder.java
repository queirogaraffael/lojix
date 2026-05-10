package com.example.lojix.util.builder.dtos;

import com.example.lojix.dto.cliente.ClienteRequestDTO;
import java.time.LocalDate;

public class ClienteRequestDTOBuilder {
    public static ClienteRequestDTO criarValido(String cpfSuffix) {
        return new ClienteRequestDTO(
                LocalDate.now(),
                UsuarioRequestDTOBuilder.criarValido("Client", cpfSuffix)
        );
    }
}
