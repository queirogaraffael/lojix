package com.example.lojix.dto.cliente;

import com.example.lojix.dto.usuario.UsuarioRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteRequestDTO {

    @NotNull(message = "O tempo de fidelidade não pode ser nulo.")
    @PastOrPresent(message = "O tempo de fidelidade deve ser uma data passada ou atual.")
    private LocalDate tempoFidelidade;

    @Valid
    @NotNull
    private UsuarioRequestDTO usuarioRequestDTO;

}
