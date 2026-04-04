package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.infrastructure.repositories.CategoriaRepository;
import com.example.supergestor.shared.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.mappers.CategoriaMapper;
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
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            CategoriaMapper categoriaMapper
    ) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @CachePut(value = "categoriasCache", key = "#result.id")
    @Transactional
    public CategoriaResponseDTO criarCategoria(CategoriaRequestDTO categoriaRequestDTO){
        log.info("Iniciando criação de categoria: {}", categoriaRequestDTO.getNome());

        Categoria categoria = categoriaMapper.toEntity(categoriaRequestDTO);

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        log.info("Categoria criada com sucesso. ID: {}", categoriaSalva.getId());

        return categoriaMapper.toResponseDTO(categoriaSalva);
    }

    @Cacheable(value = "categoriasCache", key = "#idCategoria")
    @Transactional(readOnly = true)
    public CategoriaResponseDTO getCategoriaById(Long idCategoria){
        log.debug("Buscando categoria pelo ID {}", idCategoria);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(() -> {
            log.error("Categoria com ID {} não encontrada", idCategoria);
            return new ResourceNotFoundException("Categoria com id " + idCategoria + " não encontrada");
        });

        log.info("Categoria encontrada: ID {}", idCategoria);

        return categoriaMapper.toResponseDTO(categoria);
    }

    public Page<CategoriaResponseDTO> getCategoriasPaginados(int page, int size){
        log.debug("Listando categorias paginadas. page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<CategoriaResponseDTO> result = categoriaRepository.findAllPageable(pageable);

        log.info("Página de categorias retornada: {} itens", result.getNumberOfElements());

        return result;
    }

    @CachePut(value = "categoriasCache", key = "#result.id")
    @Transactional
    public CategoriaResponseDTO updateCategoria(Long idCategoria, CategoriaUpdateDTO categoriaUpdateDTO){
        log.info("Atualizando categoria ID {}", idCategoria);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(() -> {
            log.error("Tentativa de atualizar categoria inexistente. ID {}", idCategoria);
            return new ResourceNotFoundException("Categoria com id " + idCategoria + " não encontrada");
        });

        categoriaMapper.updateCategoriaFromDTO(categoriaUpdateDTO, categoria);

        Categoria categoriaAtualizada = categoriaRepository.save(categoria);

        log.info("Categoria atualizada com sucesso. ID {}", categoriaAtualizada.getId());

        return categoriaMapper.toResponseDTO(categoriaAtualizada);
    }
}

