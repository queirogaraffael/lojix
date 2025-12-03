package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.domain.enums.UserRole;
import com.example.supergestor.domain.repositories.FuncionarioRepository;
import com.example.supergestor.domain.repositories.UsuarioRepository;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.exceptions.UsuarioJaExisteException;
import com.example.supergestor.shared.mappers.FuncionarioMapper;
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
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            FuncionarioMapper funcionarioMapper,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
        this.usuarioRepository = usuarioRepository;
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
        String senhaPlana = funcionario.getUsuario().getPassword();
        funcionario.getUsuario().setPassword(passwordEncoder.encode(senhaPlana));

        Funcionario salvo = funcionarioRepository.save(funcionario);
        log.info("Funcionário criado com id={} para CPF={}", salvo.getId(), cpf);

        return funcionarioMapper.entityToResponseDTO(salvo);
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
        return funcionarioMapper.entityToResponseDTO(funcionario);
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

        return funcionarioMapper.entityToResponseDTO(salvo);
    }

    @CacheEvict(value = "funcionariosCache", key = "#id")
    @Transactional
    public void desligarFuncionarioById(Long id) {
        log.info("Desligando funcionário id={}", id);
        
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de desligar funcionário inexistente id={}", id);
                    return new ResourceNotFoundException("Funcionario com id " + id + " não encontrado");
                });

        if ("admin@supergestor.com".equalsIgnoreCase(funcionario.getUsuario().getEmail())) {
            throw new IllegalArgumentException("O administrador principal não pode ser removido ou desativado.");
        }

        funcionarioRepository.atualizaStatusFuncionario(id, false);
        log.info("Funcionário id={} desligado com sucesso", id);
    }
}