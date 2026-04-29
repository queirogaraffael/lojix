package com.example.lojix.mappers;

import com.example.lojix.domain.entities.Usuario;
import com.example.lojix.dtos.usuario.UsuarioRequestDTO;
import com.example.lojix.dtos.usuario.UsuarioResponseDTO;
import com.example.lojix.dtos.usuario.UsuarioUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toUserResponseDTO(Usuario usuario);

    void updateUsuarioFromDTO(UsuarioUpdateDTO dto, @MappingTarget Usuario usuario);
}
