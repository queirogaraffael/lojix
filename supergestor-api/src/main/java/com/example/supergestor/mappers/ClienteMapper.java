package com.example.supergestor.mappers;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.domain.entities.Usuario;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.utils.Base64Converter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = Base64Converter.class
)
public interface ClienteMapper {

    @Mapping(target = "usuario.id", source = "usuario.id")
    @Mapping(target = "usuario.name", source = "usuario.name")
    @Mapping(target = "usuario.username", source = "usuario.username")
    @Mapping(target = "usuario.email", source = "usuario.email")
    @Mapping(target = "usuario.cpf", source = "usuario.cpf")
    @Mapping(target = "usuario.fotoPerfilBase64", source = "usuario.foto", qualifiedByName = "toBase64")
    ClienteResponseDTO entityToResponseDTO(Cliente cliente);

    @Mapping(source = "usuarioRequestDTO.name",     target = "usuario.name")
    @Mapping(source = "usuarioRequestDTO.username", target = "usuario.username")
    @Mapping(source = "usuarioRequestDTO.fotoPerfilBase64", target = "usuario.foto", qualifiedByName = "toBytes")
    @Mapping(source = "usuarioRequestDTO.email",    target = "usuario.email")
    @Mapping(source = "usuarioRequestDTO.password", target = "usuario.password")
    @Mapping(source = "usuarioRequestDTO.cpf",      target = "usuario.cpf")
    Cliente toEntity(ClienteRequestDTO clienteRequestDTO);

    @Mapping(target = "id", source = "cliente.id")
    @Mapping(target = "tempoFidelidade", source = "cliente.tempoFidelidade")
    @Mapping(target = "usuario.id", source = "id")
    @Mapping(target = "usuario.name", source = "name")
    @Mapping(target = "usuario.username", source = "username")
    @Mapping(target = "usuario.email", source = "email")
    @Mapping(target = "usuario.cpf", source = "cpf")
    ClienteResponseDTO usuarioToClienteResponseDTO(Usuario usuario);

    default Cliente getCliente(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        return usuario.getCliente();
    }
}
