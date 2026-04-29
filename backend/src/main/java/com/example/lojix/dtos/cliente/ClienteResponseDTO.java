package com.example.lojix.dtos.cliente;

import com.example.lojix.dtos.usuario.UsuarioResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteResponseDTO {

    private Long id;
    private LocalDate tempoFidelidade;
    private UsuarioResponseDTO usuario;
}
