package com.example.lojix.service;

import com.example.lojix.domain.entity.Funcionario;
import com.example.lojix.domain.enums.UserRole;
import com.example.lojix.infrastructure.repository.FuncionarioRepository;
import com.example.lojix.infrastructure.repository.UsuarioRepository;
import com.example.lojix.dto.funcionario.FuncionarioRequestDTO;
import com.example.lojix.dto.funcionario.FuncionarioResponseDTO;
import com.example.lojix.dto.funcionario.FuncionarioUpdateDTO;
import com.example.lojix.common.exception.ResourceNotFoundException;
import com.example.lojix.common.exception.UsuarioJaExisteException;
import com.example.lojix.mapper.FuncionarioMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final StorageService storageService;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper,
            UsuarioRepository usuarioRepository, StorageService storageService, PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
        this.usuarioRepository = usuarioRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;
    }

    @CachePut(value = "funcionariosCache", key = "#result.id")
    @Transactional
    public FuncionarioResponseDTO createFuncionario(FuncionarioRequestDTO dto) {

        String cpf = dto.getUsuarioRequestDTO().getCpf();

        log.info("Criando funcionário para CPF={}", cpf);

        boolean usuarioJaExiste = usuarioRepository.existsByCpf(cpf);

        if (usuarioJaExiste) {
            log.warn("Tentativa de cadastrar funcionário com CPF já existente: {}", cpf);
            throw new UsuarioJaExisteException("Usuario com CPF " + cpf + " já cadastrado.");
        }

        Funcionario funcionario = funcionarioMapper.toEntity(dto);
        funcionario.getUsuario().setRole(UserRole.FUNCIONARIO);
        funcionario.getUsuario().setPassword(passwordEncoder.encode(funcionario.getUsuario().getPassword()));

        Funcionario salvo = funcionarioRepository.save(funcionario);

        log.info("Funcionário criado com id={} para CPF={}", salvo.getId(), cpf);

        FuncionarioResponseDTO responseDTO = funcionarioMapper.entityToResponseDTO(salvo);
        if (salvo.getUsuario() != null && salvo.getUsuario().getFotoKey() != null) {
            responseDTO.getUsuarioResponseDTO()
                    .setFotoUrl(storageService.gerarPresignedUrl(salvo.getUsuario().getFotoKey()));
        }
        return responseDTO;
    }

    @Cacheable(value = "funcionariosCache", key = "#id")
    @Transactional(readOnly = true)
    public FuncionarioResponseDTO getFuncionarioById(Long id) {

        log.debug("Buscando funcionário id={}", id);

        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Funcionário não encontrado id={}", id);
                    return new ResourceNotFoundException("Funcionario com id " + id + " não encontrado");
                });

        FuncionarioResponseDTO responseDTO = funcionarioMapper.entityToResponseDTO(funcionario);
        if (funcionario.getUsuario() != null && funcionario.getUsuario().getFotoKey() != null) {
            responseDTO.getUsuarioResponseDTO()
                    .setFotoUrl(storageService.gerarPresignedUrl(funcionario.getUsuario().getFotoKey()));
        }
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public Page<FuncionarioResponseDTO> getFuncionariosAtivosPaginados(int page, int size) {
        log.debug("Listando funcionários ativos page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        return funcionarioRepository.findAllPageable(true, pageable);
    }

    @CachePut(value = "funcionariosCache", key = "#result.id")
    @Transactional
    public FuncionarioResponseDTO updateFuncionario(Long id, FuncionarioUpdateDTO dto) {

        log.info("Atualizando funcionário id={}", id);

        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar funcionário inexistente id={}", id);
                    return new ResourceNotFoundException("Funcionario com id " + id + " não encontrado");
                });

        funcionarioMapper.updateFuncionarioFromDTO(dto, funcionario);

        Funcionario salvo = funcionarioRepository.save(funcionario);

        log.info("Funcionário id={} atualizado com sucesso", id);

        FuncionarioResponseDTO responseDTO = funcionarioMapper.entityToResponseDTO(salvo);
        if (salvo.getUsuario() != null && salvo.getUsuario().getFotoKey() != null) {
            responseDTO.getUsuarioResponseDTO()
                    .setFotoUrl(storageService.gerarPresignedUrl(salvo.getUsuario().getFotoKey()));
        }
        return responseDTO;
    }

    @CacheEvict(value = "funcionariosCache", key = "#id")
    @Transactional
    public void desligarFuncionarioById(Long id) {

        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionario com id " + id + " não encontrado"));

        funcionario.setAtivo(false);
    }
}
