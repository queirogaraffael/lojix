package com.example.lojix.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Promocao {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @DecimalMin(value = "0.0", inclusive = true, message = "A taxa de desconto deve ser no mínimo 0.")
    @DecimalMax(value = "100.0", inclusive = true, message = "A taxa de desconto deve ser no máximo 100 (100%).")
    private BigDecimal taxaDeDesconto;

    private LocalDate inicio;
    private LocalDate fim;

    @Column(nullable = false)
    private Boolean ativada = true;
    
    @Version
    private Long version;

    @OneToMany(mappedBy = "promocao")
    @ToString.Exclude
    private Set<Produto> produtos = new HashSet<>();

}
