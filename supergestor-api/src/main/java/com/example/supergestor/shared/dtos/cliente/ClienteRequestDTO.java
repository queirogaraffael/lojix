package com.example.supergestor.shared.dtos.cliente;

import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    @Valid
    @NotNull
    private UsuarioRequestDTO usuarioRequestDTO;

}
