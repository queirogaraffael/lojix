package com.example.supergestor.shared.dtos.usuario;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioUpdateDTO {

    @NotNull
    private String fotoPerfilBase64;
}
