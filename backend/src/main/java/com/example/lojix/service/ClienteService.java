package com.example.lojix.service;

import com.example.lojix.domain.entity.Cliente;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repository.ClienteRepository;
import com.example.lojix.infrastructure.repository.UsuarioRepository;
import com.example.lojix.dto.cliente.ClienteRequestDTO;
import com.example.lojix.dto.cliente.ClienteResponseDTO;
import com.example.lojix.common.exception.ResourceNotFoundException;
import com.example.lojix.common.exception.UsuarioJaExisteException;
import com.example.lojix.mapper.ClienteMapper;
import com.example.lojix.util.MaskUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;


    public ClienteService(
            ClienteRepository clienteRepository,
            ClienteMapper clienteMapper,
            UsuarioRepository usuarioRepository,
            StorageService storageService,
            PasswordEncoder passwordEncoder
    ) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
    }

    @CachePut(value = "clientesCache", key = "#result.id")
    @Transactional
    public ClienteResponseDTO createCliente(ClienteRequestDTO clienteRequestDTO) {

        log.info("Iniciando criação de cliente com CPF {}",
                MaskUtils.maskCpf(clienteRequestDTO.getUsuarioRequestDTO().getCpf()));

        boolean usuarioJaExiste =
                usuarioRepository.existsByCpf(clienteRequestDTO.getUsuarioRequestDTO().getCpf());

        if (usuarioJaExiste) {
            log.warn("Tentativa de cadastro com CPF já existente: {}",
                    MaskUtils.maskCpf(clienteRequestDTO.getUsuarioRequestDTO().getCpf()));
            throw new UsuarioJaExisteException(
                    "Usuario com CPF "
                            + clienteRequestDTO.getUsuarioRequestDTO().getCpf()
                            + " já cadastrado."
            );
        }

        Cliente cliente = clienteMapper.toEntity(clienteRequestDTO);
        cliente.setMembroDesde(java.time.LocalDate.now());

        cliente.getUsuario().setRole(UserRole.CLIENTE);
        cliente.getUsuario().setPassword(passwordEncoder.encode(cliente.getUsuario().getPassword()));

        Cliente clienteSalvo = clienteRepository.save(cliente);

        log.info("Cliente criado com sucesso. ID: {}", clienteSalvo.getId());

        ClienteResponseDTO responseDTO = clienteMapper.entityToResponseDTO(clienteSalvo);
        if (clienteSalvo.getUsuario() != null && clienteSalvo.getUsuario().getFotoKey() != null) {
            responseDTO.getUsuario().setFotoUrl(storageService.gerarPresignedUrl(clienteSalvo.getUsuario().getFotoKey()));
        }
        return responseDTO;
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

        ClienteResponseDTO responseDTO = clienteMapper.entityToResponseDTO(cliente);
        if (cliente.getUsuario() != null && cliente.getUsuario().getFotoKey() != null) {
            responseDTO.getUsuario().setFotoUrl(storageService.gerarPresignedUrl(cliente.getUsuario().getFotoKey()));
        }
        return responseDTO;
    }

    @Cacheable(value = "clientesPageCache", key = "#page + '-' + #size")
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> getClientesPaginados(int page, int size){
        log.debug("Listando clientes paginados. page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<ClienteResponseDTO> result = clienteRepository.findAllPageable(pageable);

        log.info("Página de clientes retornada: {} itens", result.getNumberOfElements());

        return result;
    }
}
