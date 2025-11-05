package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toUserResponseDTO(Usuario usuario);
}
