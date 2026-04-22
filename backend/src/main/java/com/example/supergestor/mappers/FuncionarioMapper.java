package com.example.supergestor.mappers;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.dtos.funcionario.FuncionarioUpdateDTO;
import com.example.supergestor.shared.utils.Base64Converter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = {Base64Converter.class, UsuarioMapper.class}
)
public interface FuncionarioMapper {

    @Mapping(target = "usuarioResponseDTO.id", source = "usuario.id")
    @Mapping(target = "usuarioResponseDTO.name", source = "usuario.name")
    @Mapping(target = "usuarioResponseDTO.username", source = "usuario.username")
    @Mapping(target = "usuarioResponseDTO.email", source = "usuario.email")
    @Mapping(target = "usuarioResponseDTO.cpf", source = "usuario.cpf")
    @Mapping(target = "usuarioResponseDTO.fotoPerfilBase64", source = "usuario.foto", qualifiedByName = "toBase64")
    FuncionarioResponseDTO entityToResponseDTO(Funcionario funcionario);

    @Mapping(source = "usuarioRequestDTO.name",     target = "usuario.name")
    @Mapping(source = "usuarioRequestDTO.username", target = "usuario.username")
    @Mapping(source = "usuarioRequestDTO.fotoPerfilBase64", target = "usuario.foto", qualifiedByName = "toBytes")
    @Mapping(source = "usuarioRequestDTO.email",    target = "usuario.email")
    @Mapping(source = "usuarioRequestDTO.password", target = "usuario.password")
    @Mapping(source = "usuarioRequestDTO.cpf",      target = "usuario.cpf")
    Funcionario toEntity(FuncionarioRequestDTO funcionarioRequestDTO);

    @Mapping(source = "usuarioUpdateDTO", target = "usuario")
    void updateFuncionarioFromDTO(FuncionarioUpdateDTO dto, @MappingTarget Funcionario funcionario);

    @Mapping(target = "id", source = "funcionario.id")
    @Mapping(target = "cargo", source = "funcionario.cargo")
    @Mapping(target = "salario", source = "funcionario.salario")
    @Mapping(target = "usuarioResponseDTO.id", source = "id")
    @Mapping(target = "usuarioResponseDTO.name", source = "name")
    @Mapping(target = "usuarioResponseDTO.username", source = "username")
    @Mapping(target = "usuarioResponseDTO.email", source = "email")
    @Mapping(target = "usuarioResponseDTO.cpf", source = "cpf")
    FuncionarioResponseDTO usuarioToFuncionarioResponseDTO(Usuario usuario);

    default Funcionario getFuncionario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return usuario.getFuncionario();
    }

}
