package com.example.lojix.mappers;

import com.example.lojix.domain.entities.Produto;
import com.example.lojix.dtos.produtos.ProdutoRequestDTO;
import com.example.lojix.dtos.produtos.ProdutoResponseDTO;
import com.example.lojix.dtos.produtos.ProdutoUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    Produto toEntity(ProdutoRequestDTO produtoRequestDTO);

    @Mapping(target = "categoriaId", source = "categoria.id")
    ProdutoResponseDTO toResponse(Produto produto);

    void updateProdutoFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto produto);
}
