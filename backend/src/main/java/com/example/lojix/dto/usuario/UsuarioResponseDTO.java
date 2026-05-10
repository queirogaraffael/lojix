package com.example.lojix.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {

    public UsuarioResponseDTO(UUID id, String name, String username, String email, String cpf) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.cpf = cpf;
    }

    private UUID id;
    private String name;
    private String username;
    private String email;
    private String cpf;
    private String fotoPerfilBase64;
}
