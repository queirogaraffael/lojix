package com.example.supergestor.domain.repositories;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO( " +
                    "c.id, " +
                    "c.nome ) " +
                    "FROM Categoria c",
            countQuery = "SELECT count(c) FROM Categoria c"
    )
    Page<CategoriaResponseDTO> findAllPageable(Pageable pageable);

}
