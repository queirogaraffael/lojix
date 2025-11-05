package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Cliente;
import com.example.supergestor.infrastructure.repositories.ClienteRepository;
import com.example.supergestor.shared.dtos.cliente.ClienteRequestDTO;
import com.example.supergestor.shared.dtos.cliente.ClienteResponseDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.mappers.ClienteMapper;
import com.example.supergestor.shared.mappers.UsuarioMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    @Transactional
    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO){

        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return clienteMapper.entityToRespondeDTO(clienteSalvo);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO getClienteById(Long id){
        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Usuario com id " + id + " nao encontrado"));

        return clienteMapper.entityToRespondeDTO(cliente);
    }

}
