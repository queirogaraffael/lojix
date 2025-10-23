package com.example.supergestor.domain.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de desconto deve ser no mínimo 0.")
    @DecimalMax(value = "1.0", inclusive = true, message = "A taxa de desconto deve ser no máximo 1 (100%).")
    private BigDecimal taxaDeDesconto;

    private LocalDate inicio;
    private LocalDate fim;

    @OneToMany(mappedBy = "promocao")
    @ToString.Exclude
    private Set<Produto> produtos = new HashSet<>();

    @Transient
    public boolean promocaoEstaAtiva() {
        LocalDate hoje = LocalDate.now();
        return (inicio.isEqual(hoje) || inicio.isBefore(hoje))
                && (fim.isEqual(hoje) || fim.isAfter(hoje));
    }

}
