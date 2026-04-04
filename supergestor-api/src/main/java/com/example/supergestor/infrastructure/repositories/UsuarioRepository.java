package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByCpf(String cpf);

    @Query("""
    SELECT u FROM Usuario u
    LEFT JOIN FETCH u.funcionario
    LEFT JOIN FETCH u.cliente
    WHERE u.username = :username
    """)
    Optional<Usuario> findByUsernameWithAssociations(@Param("username") String username);

}
