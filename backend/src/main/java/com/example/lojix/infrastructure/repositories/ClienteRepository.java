package com.example.lojix.infrastructure.repositories;

import com.example.lojix.domain.entities.Cliente;
import com.example.lojix.dtos.cliente.ClienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query(
            value = "SELECT new com.example.lojix.dtos.cliente.ClienteResponseDTO(" +
                    "c.id, " +
                    "c.tempoFidelidade, " +
                    "new com.example.lojix.dtos.usuario.UsuarioResponseDTO(" +
                    "u.id, u.name, u.username, u.email, u.cpf" +
                    ")" +
                    ") " +
                    "FROM Cliente c JOIN c.usuario u",
            countQuery = "SELECT count(c) FROM Cliente c"
    )
    Page<ClienteResponseDTO> findAllPageable(Pageable pageable);

}
