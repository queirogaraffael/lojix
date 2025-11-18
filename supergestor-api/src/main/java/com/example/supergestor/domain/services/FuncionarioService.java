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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioMapper funcionarioMapper;
    private final UsuarioRepository usuarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper, UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public FuncionarioResponseDTO createFuncionario(FuncionarioRequestDTO funcionarioRequestDTO){

        boolean usuarioJaExiste = usuarioRepository.existsByCpf(funcionarioRequestDTO.getUsuarioRequestDTO().getCpf());

        if(usuarioJaExiste){
            throw new UsuarioJaExisteException("Usuario com CPF " + funcionarioRequestDTO.getUsuarioRequestDTO().getCpf() + " já cadastrado.");
        }

        Funcionario funcionario = funcionarioMapper.toEntity(funcionarioRequestDTO);

        funcionario.getUsuario().setRole(UserRole.FUNCIONARIO);

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        return funcionarioMapper.entityToRespondeDTO(funcionarioSalvo);
    }

    @Transactional(readOnly = true)
    public FuncionarioResponseDTO getFuncionarioById(Long id){
        Funcionario funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Funcionario com id " + id + " não encontrado"));
        return funcionarioMapper.entityToRespondeDTO(funcionario);
    }

    @Transactional(readOnly = true)
    public Page<FuncionarioResponseDTO> getFuncionariosAtivosPaginados(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return funcionarioRepository.findAllPageable(true, pageable);
    }

    @Transactional
    public FuncionarioResponseDTO updateFuncionario(Long id, FuncionarioUpdateDTO funcionarioUpdateDTO){
        Funcionario funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Funcionario com id " + id + " não encontrado"));

        funcionarioMapper.updateFuncionarioFromDTO(funcionarioUpdateDTO, funcionario);

        Funcionario funcionarioSalvo = funcionarioRepository.save(funcionario);

        return funcionarioMapper.entityToRespondeDTO(funcionarioSalvo);
    }

    @Transactional
    public void desligarFuncionarioById(Long id){

        if(!funcionarioRepository.existsById(id)){
            throw new ResourceNotFoundException("Funcionario com id " + id + " não encontrado");
        }

        funcionarioRepository.atualizaStatusFuncionario(id, false);
    }
}
