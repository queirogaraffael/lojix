package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.ClienteRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.exceptions.UsuarioJaExisteException;
import com.example.supergestor.shared.mappers.ClienteMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;


    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {

        boolean usuarioJaExiste = usuarioRepository
                .existsByCpf(clienteRequestDTO.getUsuarioRequestDTO().getCpf());

        if (usuarioJaExiste) {
            throw new UsuarioJaExisteException(
                    "Usuario com CPF "
                            + clienteRequestDTO.getUsuarioRequestDTO().getCpf()
                            + " já cadastrado."
            );
        }

        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO);

        cliente.getUsuario().setRole(UserRole.CLIENTE);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return clienteMapper.entityToResponseDTO(clienteSalvo);
    }


    @Transactional(readOnly = true)
    public ClienteResponseDTO getClienteById(Long id){
        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Usuario com id " + id + " nao encontrado"));
        return clienteMapper.entityToResponseDTO(cliente);
    }

}
