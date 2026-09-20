package com.example.lojix.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal preco;
    private boolean produtoAtivo = true;

    private String descricao;
    private LocalDate dataValidade;

    @jakarta.validation.constraints.Min(value = 0, message = "O estoque não pode ser negativo")
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "promocao_id", nullable = true)
    private Promocao promocao;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @ToString.Exclude
    private Categoria categoria;
}
