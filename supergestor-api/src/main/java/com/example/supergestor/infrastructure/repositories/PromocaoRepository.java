package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Promocao;
import com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Produto p SET p.promocao = NULL WHERE p.promocao.id = :idPromocao")
    void removerPromocaoDosProdutos(@Param("idPromocao") Long idPromocao);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Promocao p SET p.ativada = :ativada WHERE p.id = :idPromocao")
    void alterarStatusPromocao(@Param("idPromocao") Long idPromocao,
                               @Param("ativada") boolean ativada);


    @Query("SELECT new com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO( " +
            "p.id, " +
            "p.nome, " +
            "p.taxaDeDesconto, " +
            "p.inicio, " +
            "p.fim ) " +
            "FROM Promocao p " +
            "WHERE p.id = :id AND p.ativada = :ativado")
    Optional<PromocaoResponseDTO> findPromocaoById(@Param("id") Long id,
                                                   @Param("ativado") boolean ativado);

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO( " +
                    "p.id, " +
                    "p.nome, " +
                    "p.taxaDeDesconto, " +
                    "p.inicio, " +
                    "p.fim ) " +
                    "FROM Promocao p " +
                    "WHERE p.ativada = :ativado",
            countQuery = "SELECT count(p) FROM Promocao p WHERE p.ativada = :ativado"
    )
    Page<PromocaoResponseDTO> findAllPageable(@Param("ativado") boolean ativado, Pageable pageable);

}
