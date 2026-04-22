package com.example.supergestor.dtos.cliente;

import com.example.supergestor.dtos.usuario.UsuarioResponseDTO;
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
