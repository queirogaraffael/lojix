package com.example.lojix.mappers;

import com.example.lojix.domain.entities.Promocao;
import com.example.lojix.dtos.promocao.PromocaoRequestDTO;
import com.example.lojix.dtos.promocao.PromocaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    PromocaoResponseDTO toResponseDTO(Promocao promocao);

    Promocao toEntity(PromocaoRequestDTO promocaoRequestDTO);

}
