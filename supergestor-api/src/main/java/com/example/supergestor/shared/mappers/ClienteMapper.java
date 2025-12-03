package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "usuario.id", source = "usuario.id")
    @Mapping(target = "usuario.name", source = "usuario.name")
    @Mapping(target = "usuario.username", source = "usuario.username")
    @Mapping(target = "usuario.email", source = "usuario.email")
    @Mapping(target = "usuario.cpf", source = "usuario.cpf")
    ClienteResponseDTO entityToRespondeDTO(Cliente cliente);

    @Mapping(source = "usuarioRequestDTO.name", target = "usuario.name")
    @Mapping(source = "usuarioRequestDTO.username", target = "usuario.username")
    @Mapping(source = "usuarioRequestDTO.fotoPerfilBase64", target = "usuario.foto", qualifiedByName = "base64ToBytes")
    @Mapping(source = "usuarioRequestDTO.email", target = "usuario.email")
    @Mapping(source = "usuarioRequestDTO.password", target = "usuario.password")
    @Mapping(source = "usuarioRequestDTO.cpf", target = "usuario.cpf")
    Cliente toEntity(ClienteRequestDTO clienteRequestDTO);

    @Named("base64ToBytes")
    default byte[] base64ToBytes(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return null;
        }
        try {
            if (base64.contains(",")) {
                base64 = base64.split(",")[1];
            }
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}