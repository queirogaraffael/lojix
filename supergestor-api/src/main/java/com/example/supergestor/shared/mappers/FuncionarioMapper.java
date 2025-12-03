package com.example.supergestor.shared.mappers;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {

    @Mapping(target = "usuarioResponseDTO.id", source = "usuario.id")
    @Mapping(target = "usuarioResponseDTO.name", source = "usuario.name")
    @Mapping(target = "usuarioResponseDTO.username", source = "usuario.username")
    @Mapping(target = "usuarioResponseDTO.email", source = "usuario.email")
    @Mapping(target = "usuarioResponseDTO.cpf", source = "usuario.cpf")
    FuncionarioResponseDTO entityToRespondeDTO(Funcionario funcionario);

    @Mapping(source = "usuarioRequestDTO.name", target = "usuario.name")
    @Mapping(source = "usuarioRequestDTO.username", target = "usuario.username")
    @Mapping(source = "usuarioRequestDTO.fotoPerfilBase64", target = "usuario.foto", qualifiedByName = "base64ToBytes")
    @Mapping(source = "usuarioRequestDTO.email", target = "usuario.email")
    @Mapping(source = "usuarioRequestDTO.password", target = "usuario.password")
    @Mapping(source = "usuarioRequestDTO.cpf", target = "usuario.cpf")
    Funcionario toEntity(FuncionarioRequestDTO funcionarioRequestDTO);

    @Mapping(source = "usuario.foto", target = "usuario.foto")
    void updateFuncionarioFromDTO(FuncionarioUpdateDTO dto, @MappingTarget Funcionario funcionario);

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