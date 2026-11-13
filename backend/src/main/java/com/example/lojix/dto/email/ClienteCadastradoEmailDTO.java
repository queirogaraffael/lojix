package com.example.lojix.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCadastradoEmailDTO {
    private String nomeCliente;
    private String email;
    private String cpf;
    private LocalDate membroDesde;
}

