package com.example.supergestor.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal preco;
    private boolean produtoAtivo;
    private String descricao;
    private LocalDate dataValidade;

    @ManyToOne // carrega por podrao, mesmo que você coloque LAZY
    @JoinColumn(name = "promocao_id", nullable = true)
    private Promocao promocao;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @ToString.Exclude
    private Categoria categoria;
}
