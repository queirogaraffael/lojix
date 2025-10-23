package com.example.supergestor.infrastructure.repositories;

import com.example.supergestor.domain.entities.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocaoRepository extends JpaRepository<Promocao, Long> {
}
