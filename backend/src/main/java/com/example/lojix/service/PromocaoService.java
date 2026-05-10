package com.example.lojix.service;

import com.example.lojix.domain.entity.Produto;
import com.example.lojix.domain.entity.Promocao;
import com.example.lojix.infrastructure.repository.ProdutoRepository;
import com.example.lojix.infrastructure.repository.PromocaoRepository;
import com.example.lojix.dto.promocao.PromocaoRequestDTO;
import com.example.lojix.dto.promocao.PromocaoResponseDTO;
import com.example.lojix.shared.exception.ResourceNotFoundException;
import com.example.lojix.mapper.PromocaoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProdutoRepository produtoRepository;
    private final PromocaoMapper promocaoMapper;
    private final CacheManager cacheManager;

    public PromocaoService(PromocaoRepository promocaoRepository, ProdutoRepository produtoRepository, PromocaoMapper promocaoMapper, CacheManager cacheManager) {
        this.promocaoRepository = promocaoRepository;
        this.produtoRepository = produtoRepository;
        this.promocaoMapper = promocaoMapper;
        this.cacheManager = cacheManager;
    }

    @CachePut(value = "promocaoCache", key = "#result.id")
    @Transactional
    public PromocaoResponseDTO createPromocao(PromocaoRequestDTO dto) {

        log.info("Criando promoção '{}'", dto.getNome());

        Promocao promocao = promocaoMapper.toEntity(dto);
        promocao.setAtivada(true);

        Promocao saved = promocaoRepository.save(promocao);

        log.info("Promoção criada com id={}", saved.getId());

        return promocaoMapper.toResponseDTO(saved);
    }

    @Cacheable(value = "promocaoCache", key = "#id")
    @Transactional(readOnly = true)
    public PromocaoResponseDTO getPromocaoById(Long id) {

        log.debug("Buscando promocao id={}", id);

        return promocaoRepository.findPromocaoById(id, true)
                .orElseThrow(() -> {
                    log.warn("Promocao não encontrada id={}", id);
                    return new ResourceNotFoundException("Promocao com id " + id + " não encontrada.");
                });
    }

    @Transactional(readOnly = true)
    public Page<PromocaoResponseDTO> getPromocoesAtivasPaginadas(int page, int size) {

        log.debug("Listando promocao paginadas page={} size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        return promocaoRepository.findAllPageable(true, pageable);
    }

    @CacheEvict(value = "promocaoCache", key = "#idPromocao")
    @Transactional
    public void desativarPromocaoById(Long idPromocao) {

        log.info("Desativando promoção id={}", idPromocao);

        Promocao promocao = promocaoRepository.findById(idPromocao)
                .orElseThrow(() -> {
                    log.warn("Tentativa de desativar promoção inexistente id={}", idPromocao);
                    return new ResourceNotFoundException("Promocao com id " + idPromocao + " não encontrada.");
                });

        List<Long> idsProdutos = produtoRepository.findProdutoIdsByPromocaoId(idPromocao);

        Cache produtosCache = cacheManager.getCache("produtosCache");
        if (produtosCache != null) {
            idsProdutos.forEach(produtosCache::evict);
        }

        promocaoRepository.removerPromocaoDosProdutos(promocao.getId());
        promocaoRepository.alterarStatusPromocao(idPromocao, false);

        log.info("Promoção id={} desativada e caches afetados invalidados", idPromocao);
    }


    @CacheEvict(value = "produtosCache", key = "#idProduto")
    @Transactional
    public void associarPromocaoAProduto(Long idProduto, Long idPromocao) {

        log.info("Associando promoção id={} ao produto id={}", idPromocao, idProduto);

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> {
                    log.warn("Produto id={} não encontrado ao tentar associar promoção", idProduto);
                    return new ResourceNotFoundException("Produto com id " + idProduto + " não encontrado.");
                });

        Promocao promocao = promocaoRepository.findById(idPromocao)
                .orElseThrow(() -> {
                    log.warn("Promoção id={} não encontrada ao tentar associar ao produto id={}", idPromocao, idProduto);
                    return new ResourceNotFoundException("Promocao com id " + idPromocao + " não encontrada.");
                });

        produto.setPromocao(promocao);
        produtoRepository.save(produto);

        log.info("Promoção id={} associada ao produto id={}", idPromocao, idProduto);
    }

    @CacheEvict(value = "produtosCache", key = "#idProduto")
    @Transactional
    public void removerPromocaoProduto(Long idProduto) {

        log.info("Removendo promoção do produto id={}", idProduto);

        Produto produto = produtoRepository.findById(idProduto)
                .orElseThrow(() -> {
                    log.warn("Tentativa de remover promoção de produto inexistente id={}", idProduto);
                    return new ResourceNotFoundException("Produto com id " + idProduto + " não encontrado.");
                });

        produto.setPromocao(null);
        produtoRepository.save(produto);

        log.info("Promoção removida do produto id={}", idProduto);
    }
}
