package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "usuarioResponseDTO.id", source = "usuario.id")
    @Mapping(target = "usuarioResponseDTO.nome", source = "usuario.nome")
    @Mapping(target = "usuarioResponseDTO.username", source = "usuario.username")
    @Mapping(target = "usuarioResponseDTO.email", source = "usuario.email")
    @Mapping(target = "usuarioResponseDTO.cpf", source = "usuario.cpf")
    ClienteResponseDTO entityToResponseDTO(Cliente cliente);


    @Mapping(source = "usuarioRequestDTO.nome", target = "usuario.nome")
    @Mapping(source = "usuarioRequestDTO.username", target = "usuario.username")
    @Mapping(source = "usuarioRequestDTO.fotoPerfilBase64", target = "usuario.foto")
    @Mapping(source = "usuarioRequestDTO.email", target = "usuario.email")
    @Mapping(source = "usuarioRequestDTO.password", target = "usuario.password")
    @Mapping(source = "usuarioRequestDTO.cpf", target = "usuario.cpf")
    Cliente toEntity(ClienteRequestDTO clienteRequestDTO);
}
