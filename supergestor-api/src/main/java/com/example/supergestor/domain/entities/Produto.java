package com.example.supergestor.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promocao_id", nullable = true)
    private Promocao promocao;

    public BigDecimal getPreco(){
        if(promocao != null &&  promocao.promocaoEstaAtiva()){
            return preco.multiply(BigDecimal.ONE.subtract(promocao.getTaxaDeDesconto()));
        }

        return preco;
    }
}
