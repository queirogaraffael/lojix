package com.example.lojix.utils.builders.dtos;

import com.example.lojix.dtos.usuario.UsuarioRequestDTO;
import java.util.UUID;

public class UsuarioRequestDTOBuilder {
    public static UsuarioRequestDTO criarValido(String prefixo, String cpfSuffix) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        return new UsuarioRequestDTO(
                prefixo + " Test " + uniqueSuffix,
                null,
                prefixo.toLowerCase() + "_user_" + uniqueSuffix,
                "123456789" + cpfSuffix,
                prefixo.toLowerCase() + "_" + uniqueSuffix + "@test.com",
                "password123"
        );
    }
}
