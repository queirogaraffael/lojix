package com.example.supergestor.mappers;

import com.example.supergestor.domain.entities.Promocao;
import com.example.supergestor.dtos.promocao.PromocaoRequestDTO;
import com.example.supergestor.dtos.promocao.PromocaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    PromocaoResponseDTO toResponseDTO(Promocao promocao);

    Promocao toEntity(PromocaoRequestDTO promocaoRequestDTO);

}
