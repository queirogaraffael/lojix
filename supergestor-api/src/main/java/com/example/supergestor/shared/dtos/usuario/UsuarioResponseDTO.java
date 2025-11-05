package com.example.supergestor.shared.dtos.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String cpf;
}
