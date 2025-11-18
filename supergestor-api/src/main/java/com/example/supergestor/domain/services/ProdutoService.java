package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.domain.repositories.CategoriaRepository;
import com.example.supergestor.domain.repositories.ProdutoRepository;
import com.example.supergestor.shared.dtos.produtos.ProdutoRequestDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoResponseDTO;
import com.example.supergestor.shared.dtos.produtos.ProdutoUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.mappers.ProdutoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.categoriaRepository = categoriaRepository;
    }

    public ProdutoResponseDTO createProduto(Long idCategoria, ProdutoRequestDTO produtoRequestDTO){
        Produto produto = produtoMapper.toEntity(produtoRequestDTO);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(()-> new ResourceNotFoundException("Categoria não encontrada"));

        produto.setCategoria(categoria);

        Produto produtoSaved = produtoRepository.save(produto);
        return produtoMapper.toResponse(produtoSaved);
    }

    public ProdutoResponseDTO getProdutoById(Long id){
        return produtoRepository.findProdutoById(id, true).orElseThrow(() -> new ResourceNotFoundException("Produto com id: "+ id + " não encontrado."));
    }

    public Page<ProdutoResponseDTO> getProdutosPaginados(int page, int size){
        Pageable pageable = PageRequest.of(page, size);

        return produtoRepository.findAllPageable(true, pageable);
    }

    public Page<ProdutoResponseDTO> getProdutosPaginadosByCategoriaId(Long idCategoria, int page, int size){

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(()-> new ResourceNotFoundException("Categoia com id " + idCategoria + " não encontrada"));

        Pageable pageable = PageRequest.of(page, size);

        return produtoRepository.findPageableByCategoriaId(categoria.getId(), true,  pageable);
    }

    public ProdutoResponseDTO updateProdutoById(Long idProduto, ProdutoUpdateDTO produtoUpdate){
        Produto produto = produtoRepository.findById(idProduto).orElseThrow(()-> new ResourceNotFoundException("Produto com id: " + idProduto + " não encontrado."));
        produtoMapper.updateProdutoFromDTO(produtoUpdate, produto);
        return produtoMapper.toResponse(produtoRepository.save(produto));
    }

    public void desativarProdutoById(Long id){
        produtoRepository.desativarProduto(id, false);
    }
}
