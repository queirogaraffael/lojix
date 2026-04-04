package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Funcionario f SET f.ativo = :ativo WHERE f.id = :id")
    void atualizaStatusFuncionario(@Param("id") Long id, @Param("ativo") boolean ativo);

    boolean existsById(Long id);

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO(" +
                    "f.id, " +
                    "f.cargo, " +
                    "f.salario, " +
                    "new com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO(" +
                    "u.id, u.name, u.username, u.email, u.cpf" +
                    ")" +
                    ") " +
                    "FROM Funcionario f JOIN f.usuario u WHERE f.ativo = :ativo",
            countQuery = "SELECT count(f) FROM Funcionario f WHERE f.ativo = :ativo"
    )
    Page<FuncionarioResponseDTO> findAllPageable(@Param("ativo") boolean ativo, Pageable pageable);
}
