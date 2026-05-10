package com.example.lojix.infrastructure.repository;

import com.example.lojix.domain.entity.Categoria;
import com.example.lojix.dto.categoria.CategoriaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query(
            value = "SELECT new com.example.lojix.dto.categoria.CategoriaResponseDTO( " +
                    "c.id, " +
                    "c.nome ) " +
                    "FROM Categoria c",
            countQuery = "SELECT count(c) FROM Categoria c"
    )
    Page<CategoriaResponseDTO> findAllPageable(Pageable pageable);

}
