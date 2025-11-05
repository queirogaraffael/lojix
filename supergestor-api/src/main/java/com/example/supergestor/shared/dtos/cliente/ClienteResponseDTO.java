package com.example.supergestor.shared.dtos.cliente;

import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
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
