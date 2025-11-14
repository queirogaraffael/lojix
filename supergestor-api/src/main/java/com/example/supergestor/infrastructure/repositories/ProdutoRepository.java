package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO;
import com.example.supergestor.shared.dtos.promocao.PromocaoProdutoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("SELECT new com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO( " +
            "p.id, " +
            "p.nome, " +
            "p.preco, " +
            "p.descricao, " +
            "p.dataValidade, " +
            "p.promocao.id, " +
            "p.categoria.id ) " +
            "FROM Produto p " +
            "WHERE p.id = :id AND p.produtoAtivo = :ativo")
    Optional<ProdutoResponseDTO> findProdutoById(@Param("id") Long id,
                                                 @Param("ativo") boolean ativo);

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO(" +
                    "p.id, " +
                    "p.nome, " +
                    "p.preco, " +
                    "p.descricao, " +
                    "p.dataValidade, " +
                    "p.promocao.id, " +
                    "p.categoria.id ) " +
                    "FROM Produto p " +
                    "WHERE p.produtoAtivo = :ativo",
            countQuery = "SELECT count(p) FROM Produto p WHERE p.produtoAtivo = :ativo"
    )
    Page<ProdutoResponseDTO> findAllPageable(@Param("ativo") boolean ativo, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Produto p SET p.produtoAtivo = :produtoAtivo WHERE p.id = :id")
    void desativarProduto(@Param("id") Long id, @Param("produtoAtivo") boolean produtoAtivo);

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO( " +
                    "p.id, " +
                    "p.nome, " +
                    "p.preco, " +
                    "p.descricao, " +
                    "p.dataValidade, " +
                    "p.promocao.id, " +
                    "p.categoria.id ) " +
                    "FROM Produto p " +
                    "JOIN p.categoria c " +
                    "WHERE p.produtoAtivo = :produtoAtivo " +
                    "AND c.id = :idCategoria",
            countQuery = "SELECT count(p) FROM Produto p JOIN p.categoria c " +
                    "WHERE p.produtoAtivo = :produtoAtivo " +
                    "AND c.id = :idCategoria"
    )
    Page<ProdutoResponseDTO> findPageableByCategoriaId(
            @Param("idCategoria") Long idCategoria,
            @Param("produtoAtivo") boolean produtoAtivo,
            Pageable pageable
    );

}
