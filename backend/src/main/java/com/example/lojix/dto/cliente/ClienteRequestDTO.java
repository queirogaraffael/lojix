package com.example.lojix.dto.cliente;

import com.example.lojix.dto.usuario.UsuarioRequestDTO;
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
