package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.infrastructure.repositories.CategoriaRepository;
import com.example.supergestor.shared.dtos.categoria.CategoriaRequestDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaResponseDTO;
import com.example.supergestor.shared.dtos.categoria.CategoriaUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.mappers.CategoriaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;
    private CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public CategoriaResponseDTO criarCategoria(CategoriaRequestDTO categoriaRequestDTO){
        Categoria categoria = categoriaMapper.toEntity(categoriaRequestDTO);

        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return categoriaMapper.toResponseDTO(categoriaSalva);
    }

    public CategoriaResponseDTO getCategoriaById(Long idCategoria){
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(()-> new ResourceNotFoundException("Categoia com id " + idCategoria + " não encontrada"));

        return categoriaMapper.toResponseDTO(categoria);
    }

    public Page<CategoriaResponseDTO> getCategoriasPaginados(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return categoriaRepository.findAllPageable(pageable);
    }

    public CategoriaResponseDTO updateCategoria(Long idCategoria, CategoriaUpdateDTO categoriaUpdateDTO){
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(()-> new ResourceNotFoundException("Categoia com id " + idCategoria + " não encontrada"));

        categoriaMapper.updateCategoriaFromDTO(categoriaUpdateDTO, categoria);

        return categoriaMapper.toResponseDTO(categoriaRepository.save(categoria));
    }


}
