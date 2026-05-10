package com.example.lojix.mapper;

import com.example.lojix.domain.entity.Promocao;
import com.example.lojix.dto.promocao.PromocaoRequestDTO;
import com.example.lojix.dto.promocao.PromocaoResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromocaoMapper {

    PromocaoResponseDTO toResponseDTO(Promocao promocao);

    Promocao toEntity(PromocaoRequestDTO promocaoRequestDTO);

}
