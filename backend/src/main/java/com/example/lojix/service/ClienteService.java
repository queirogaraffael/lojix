package com.example.lojix.service;

import com.example.lojix.domain.entity.Cliente;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repository.ClienteRepository;
import com.example.lojix.infrastructure.repository.UsuarioRepository;
import com.example.lojix.dto.cliente.ClienteRequestDTO;
import com.example.lojix.dto.cliente.ClienteResponseDTO;
import com.example.lojix.shared.exception.ResourceNotFoundException;
import com.example.lojix.shared.exception.UsuarioJaExisteException;
import com.example.lojix.mapper.ClienteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;


    public ClienteService(
            ClienteRepository clienteRepository,
            ClienteMapper clienteMapper,
            UsuarioRepository usuarioRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @CachePut(value = "clientesCache", key = "#result.id")
    @Transactional
    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {

        log.info("Iniciando criação de cliente com CPF {}",
                clienteRequestDTO.getUsuarioRequestDTO().getCpf());

        boolean usuarioJaExiste =
                usuarioRepository.existsByCpf(clienteRequestDTO.getUsuarioRequestDTO().getCpf());

        if (usuarioJaExiste) {
            log.warn("Tentativa de cadastro com CPF já existente: {}",
                    clienteRequestDTO.getUsuarioRequestDTO().getCpf());
            throw new UsuarioJaExisteException(
                    "Usuario com CPF "
                            + clienteRequestDTO.getUsuarioRequestDTO().getCpf()
                            + " já cadastrado."
            );
        }

        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO);

        cliente.getUsuario().setRole(UserRole.CLIENTE);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        log.info("Cliente criado com sucesso. ID: {}", clienteSalvo.getId());

        return clienteMapper.entityToResponseDTO(clienteSalvo);
    }

    @Cacheable(value = "clientesCache", key = "#id")
    @Transactional(readOnly = true)
    public ClienteResponseDTO getClienteById(Long id){
        log.debug("Buscando cliente pelo ID {}", id);

        Cliente cliente = clienteRepository.findById(id).orElseThrow(() -> {
            log.error("Cliente com ID {} não encontrado", id);
            return new ResourceNotFoundException("Usuario com id " + id + " nao encontrado");
        });

        log.info("Cliente encontrado: ID {}", id);

        return clienteMapper.entityToResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> getClientePaginados(int page, int size){
        log.debug("Listando clientes paginados. page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<ClienteResponseDTO> result = clienteRepository.findAllPageable(pageable);

        log.info("Página de clientes retornada: {} itens", result.getNumberOfElements());

        return result;
    }
}
