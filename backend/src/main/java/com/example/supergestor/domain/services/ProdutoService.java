package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Categoria;
import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.infrastructure.repositories.CategoriaRepository;
import com.example.supergestor.infrastructure.repositories.ProdutoRepository;
import com.example.supergestor.dtos.produtos.ProdutoRequestDTO;
import com.example.supergestor.dtos.produtos.ProdutoResponseDTO;
import com.example.supergestor.dtos.produtos.ProdutoUpdateDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.mappers.ProdutoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoMapper produtoMapper;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
        this.categoriaRepository = categoriaRepository;
    }

    @CachePut(value = "produtosCache", key = "#result.id")
    @Transactional
    public ProdutoResponseDTO createProduto(Long idCategoria, ProdutoRequestDTO dto) {

        log.info("Criando produto na categoria id={}", idCategoria);

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada ao criar produto. id={}", idCategoria);
                    return new ResourceNotFoundException("Categoria não encontrada");
                });

        Produto produto = produtoMapper.toEntity(dto);
        produto.setCategoria(categoria);

        Produto saved = produtoRepository.save(produto);

        log.info("Produto criado com id={} na categoria id={}", saved.getId(), idCategoria);

        return produtoMapper.toResponse(saved);
    }

    @Cacheable(value = "produtosCache", key = "#id")
    @Transactional(readOnly = true)
    public ProdutoResponseDTO getProdutoById(Long id) {

        log.debug("Buscando produto id={}", id);

        return produtoRepository.findProdutoById(id, true)
                .orElseThrow(() -> {
                    log.warn("Produto não encontrado id={}", id);
                    return new ResourceNotFoundException("Produto com id " + id + " não encontrado.");
                });
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> getProdutosPaginados(int page, int size) {

        log.debug("Listando produtos paginados page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        return produtoRepository.findAllPageable(true, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> getProdutosPaginadosByCategoriaId(Long idCategoria, int page, int size) {

        log.debug("Listando produtos por categoria id={} page={} size={}", idCategoria, page, size);

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada ao listar produtos paginados. id={}", idCategoria);
                    return new ResourceNotFoundException("Categoria com id " + idCategoria + " não encontrada");
                });

        Pageable pageable = PageRequest.of(page, size);

        return produtoRepository.findPageableByCategoriaId(categoria.getId(), true, pageable);
    }

    @CachePut(value = "produtosCache", key = "#result.id")
    @Transactional
    public ProdutoResponseDTO updateProdutoById(Long idProduto, ProdutoUpdateDTO dto) {

        log.info("Atualizando produto id={}", idProduto);

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> {
                    log.warn("Tentativa de atualizar produto inexistente id={}", idProduto);
                    return new ResourceNotFoundException("Produto com id " + idProduto + " não encontrado.");
                });

        produtoMapper.updateProdutoFromDTO(dto, produto);
        Produto updated = produtoRepository.save(produto);

        log.info("Produto id={} atualizado com sucesso", idProduto);

        return produtoMapper.toResponse(updated);
    }

    @CacheEvict(value = "produtosCache", key = "#id")
    @Transactional
    public void desativarProdutoById(Long id) {

        log.info("Desativando produto id={}", id);

        produtoRepository.desativarProduto(id, false);

        log.info("Produto id={} desativado", id);
    }
}

