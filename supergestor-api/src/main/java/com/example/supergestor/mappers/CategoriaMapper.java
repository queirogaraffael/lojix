package com.example.supergestor.mappers;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.shared.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO categoriaRequestDTO);
    CategoriaResponseDTO toResponseDTO(Categoria categoria);

    void updateCategoriaFromDTO(CategoriaUpdateDTO categoriaUpdateDTO, @MappingTarget Categoria categoria);
}
