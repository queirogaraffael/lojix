package com.example.lojix.mapper;

import com.example.lojix.domain.entity.Produto;
import com.example.lojix.dto.produto.ProdutoRequestDTO;
import com.example.lojix.dto.produto.ProdutoResponseDTO;
import com.example.lojix.dto.produto.ProdutoUpdateDTO;
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
