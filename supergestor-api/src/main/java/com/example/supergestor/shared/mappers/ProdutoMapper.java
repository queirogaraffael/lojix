package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.shared.dtos.produtos.ProdutoRequestDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    Produto toEntity(ProdutoRequestDTO produtoRequestDTO);

    ProdutoResponseDTO toResponse(Produto produto);

    void updateProdutoFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto produto);

}
