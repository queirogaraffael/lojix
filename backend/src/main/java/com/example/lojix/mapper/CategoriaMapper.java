package com.example.lojix.mapper;

import com.example.lojix.domain.entity.Categoria;
import com.example.lojix.dto.categoria.CategoriaRequestDTO;
import com.example.lojix.dto.categoria.CategoriaResponseDTO;
import com.example.lojix.dto.categoria.CategoriaUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    Categoria toEntity(CategoriaRequestDTO categoriaRequestDTO);
    CategoriaResponseDTO toResponseDTO(Categoria categoria);

    void updateCategoriaFromDTO(CategoriaUpdateDTO categoriaUpdateDTO, @MappingTarget Categoria categoria);
}
