package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query(
            value = "SELECT new com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO(" +
                    "c.id, " +
                    "c.tempoFidelidade, " +
                    "new com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO(" +
                    "u.id, u.name, u.username, u.email, u.cpf" +
                    ")" +
                    ") " +
                    "FROM Cliente c JOIN c.usuario u",
            countQuery = "SELECT count(c) FROM Cliente c"
    )
    Page<ClienteResponseDTO> findAllPageable(Pageable pageable);

}
