package com.example.lojix.mapper;

import com.example.lojix.domain.entity.Usuario;
import com.example.lojix.dto.usuario.UsuarioRequestDTO;
import com.example.lojix.dto.usuario.UsuarioResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toUserResponseDTO(Usuario usuario);

}
