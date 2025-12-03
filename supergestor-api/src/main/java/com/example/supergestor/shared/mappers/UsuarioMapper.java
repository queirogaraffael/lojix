package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.shared.dtos.usuario.UsuarioRequestDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioResponseDTO;
import com.example.supergestor.shared.dtos.usuario.UsuarioUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioRequestDTO usuarioRequestDTO);

    UsuarioResponseDTO toUserResponseDTO(Usuario usuario);

    void updateUsuarioFromDTO(UsuarioUpdateDTO dto, @MappingTarget Usuario usuario);
}
