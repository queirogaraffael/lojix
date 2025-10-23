package com.example.supergestor.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Funcionario {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cargo;
    private BigDecimal salario;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Usuario usuario;
}
