package com.example.supergestor.domain.services;

import com.example.supergestor.domain.entities.Produto;
import com.example.supergestor.domain.entities.Promocao;
import com.example.supergestor.domain.repositories.ProdutoRepository;
import com.example.supergestor.domain.repositories.PromocaoRepository;
import com.example.supergestor.shared.dtos.promocao.PromocaoRequestDTO;
import com.example.supergestor.shared.dtos.promocao.PromocaoResponseDTO;
import com.example.supergestor.shared.exceptions.ResourceNotFoundException;
import com.example.supergestor.shared.mappers.PromocaoMapper;
import org.springframework.stereotype.Service;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final ProdutoRepository produtoRepository;
    private final PromocaoMapper promocaoMapper;

    public PromocaoService(PromocaoRepository promocaoRepository, ProdutoRepository produtoRepository, PromocaoMapper promocaoMapper) {
        this.promocaoRepository = promocaoRepository;
        this.produtoRepository = produtoRepository;
        this.promocaoMapper = promocaoMapper;
    }

    public PromocaoResponseDTO createPromocao(PromocaoRequestDTO promocaoRequestDTO){
        Promocao promocao = promocaoMapper.toEntity(promocaoRequestDTO);
        promocao.setAtivada(true);

        Promocao promocaoSalva = promocaoRepository.save(promocao);

        return promocaoMapper.toResponseDTO(promocaoSalva);
    }

    public void desativarPromocaoById(Long idPromocao){
        Promocao promocao = promocaoRepository.findById(idPromocao).orElseThrow(()-> new ResourceNotFoundException("Promocao não com id: " + idPromocao + " não encontrado."));

        promocaoRepository.removerPromocaoDosProdutos(promocao.getId());

        promocaoRepository.alterarStatusPromocao(idPromocao, false);
    }

    public void associarPromocaoAProduto(Long idProduto, Long idPromocao){

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(()-> new ResourceNotFoundException("Produto com id: " + idProduto + " não encontrado."));
        Promocao promocao = promocaoRepository.findById(idPromocao).orElseThrow(()-> new ResourceNotFoundException("Promocao não com id: " + idPromocao + " não encontrado."));

        produto.setPromocao(promocao);

        produtoRepository.save(produto);
    }

    public void removerPromocaoProduto(Long idProduto){

        Produto produto = produtoRepository.findById(idProduto).orElseThrow(()-> new ResourceNotFoundException("Produto com id: " + idProduto + " não encontrado."));
        produto.setPromocao(null);

        produtoRepository.save(produto);
    }
}
