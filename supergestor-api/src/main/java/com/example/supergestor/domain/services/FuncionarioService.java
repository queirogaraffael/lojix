package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Funcionario;
import com.example.supergestor.infrastructure.repositories.FuncionarioRepository;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioRequestDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioResponseDTO;
import com.example.supergestor.shared.dtos.funcionario.FuncionarioUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
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

    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioMapper = funcionarioMapper;
    }

    @Transactional
    public FuncionarioResponseDTO createFuncionario(FuncionarioRequestDTO funcionarioRequestDTO){

        Funcionario funcionario = funcionarioMapper.toEntity(funcionarioRequestDTO);

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
