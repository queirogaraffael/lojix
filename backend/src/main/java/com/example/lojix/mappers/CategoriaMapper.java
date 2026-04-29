package com.example.lojix.mappers;

import com.example.lojix.domain.entities.Categoria;
import com.example.lojix.dtos.categoria.CategoriaRequestDTO;
import com.example.lojix.dtos.categoria.CategoriaResponseDTO;
import com.example.lojix.dtos.categoria.CategoriaUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO categoriaRequestDTO);
    CategoriaResponseDTO toResponseDTO(Categoria categoria);

    void updateCategoriaFromDTO(CategoriaUpdateDTO categoriaUpdateDTO, @MappingTarget Categoria categoria);
}
