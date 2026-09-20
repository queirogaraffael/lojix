package com.example.lojix.mapper;

import com.example.lojix.domain.entity.Produto;
import com.example.lojix.dto.produto.ProdutoRequestDTO;
import com.example.lojix.dto.produto.ProdutoResponseDTO;
import com.example.lojix.dto.produto.ProdutoUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.AfterMapping;
import java.time.LocalDate;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    Produto toEntity(ProdutoRequestDTO produtoRequestDTO);

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "promocaoId", source = "promocao.id")
    @Mapping(target = "precoBase", source = "preco")
    @Mapping(target = "precoPromocional", ignore = true)
    @Mapping(target = "emPromocao", ignore = true)
    @Mapping(target = "nomePromocao", ignore = true)
    ProdutoResponseDTO toResponse(Produto produto);

    @AfterMapping
    default void calculatePromocao(Produto produto, @MappingTarget ProdutoResponseDTO dto) {
        if (produto.getPromocao() != null && Boolean.TRUE.equals(produto.getPromocao().getAtivada())) {
            LocalDate hoje = LocalDate.now();
            LocalDate inicio = produto.getPromocao().getInicio();
            LocalDate fim = produto.getPromocao().getFim();
            
            boolean valido = (inicio == null || !hoje.isBefore(inicio)) && 
                             (fim == null || !hoje.isAfter(fim));
                             
            if (valido) {
                dto.setEmPromocao(true);
                dto.setNomePromocao(produto.getPromocao().getNome());
                BigDecimal desconto = produto.getPreco()
                    .multiply(produto.getPromocao().getTaxaDeDesconto())
                    .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                dto.setPrecoPromocional(produto.getPreco().subtract(desconto));
                return;
            }
        }
        
        dto.setEmPromocao(false);
        dto.setPrecoPromocional(produto.getPreco());
        dto.setNomePromocao(null);
    }

    void updateProdutoFromDTO(ProdutoUpdateDTO dto, @MappingTarget Produto produto);
}
