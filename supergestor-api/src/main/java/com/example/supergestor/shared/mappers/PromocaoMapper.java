package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Promocao;
import com.example.supergestor.shared.dtos.promocao.PromocaoRequestDTO;
import com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    PromocaoResponseDTO toResponseDTO(Promocao promocao);

    Promocao toEntity(PromocaoRequestDTO promocaoRequestDTO);

}
