package com.example.lojix.dtos.usuario;

import com.example.lojix.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserContextDTO {

    private UUID id;
    private String name;
    private String username;
    private String email;
    private String foto;
    private UserRole role;
    private UUID clienteId;
    private UUID funcionarioId;
}
