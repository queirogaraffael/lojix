package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Produto p SET p.promocao = NULL WHERE p.promocao.id = :idPromocao")
    void removerPromocaoDosProdutos(@Param("idPromocao") Long idPromocao);

    @Transactional
    @Modifying
    @Query("UPDATE Promocao p SET p.ativada = :ativada WHERE p.id = :idPromocao")
    void alterarStatusPromocao(@Param("idPromocao") Long idPromocao,
                               @Param("ativada") boolean ativada);
}
