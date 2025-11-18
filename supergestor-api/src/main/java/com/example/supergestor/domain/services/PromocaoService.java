package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.domain.entities.Promocao;
import com.example.supergestor.domain.repositories.ProdutoRepository;
import com.example.supergestor.domain.repositories.PromocaoRepository;
import com.example.supergestor.shared.dtos.promocao.PromocaoRequestDTO;
import com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.mappers.PromocaoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PromocaoService {
    private final PromocaoRepository promocaoRepository;
    private final ProdutoRepository produtoRepository;
    private final PromocaoMapper promocaoMapper;

    public PromocaoService(PromocaoRepository promocaoRepository, ProdutoRepository produtoRepository, PromocaoMapper promocaoMapper) {
        this.promocaoRepository = promocaoRepository;
        this.produtoRepository = produtoRepository;
        this.promocaoMapper = promocaoMapper;
    }

    public PromocaoResponseDTO createPromocao(PromocaoRequestDTO dto) {

        log.info("Criando promoção '{}'", dto.getNome());

        Promocao promocao = promocaoMapper.toEntity(dto);
        promocao.setAtivada(true);

        Promocao saved = promocaoRepository.save(promocao);

        log.info("Promoção criada com id={}", saved.getId());

        return promocaoMapper.toResponseDTO(saved);
    }

    public void desativarPromocaoById(Long idPromocao) {

        log.info("Desativando promoção id={}", idPromocao);

        Promocao promocao = promocaoRepository.findById(idPromocao)
                .orElseThrow(() -> {
                    log.warn("Tentativa de desativar promoção inexistente id={}", idPromocao);
                    return new ResourceNotFoundException("Promocao com id " + idPromocao + " não encontrada.");
                });

        promocaoRepository.removerPromocaoDosProdutos(promocao.getId());
        promocaoRepository.alterarStatusPromocao(idPromocao, false);

        log.info("Promoção id={} desativada", idPromocao);
    }

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
