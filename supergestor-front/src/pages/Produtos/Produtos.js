import React, { useState } from 'react';
import { mockProdutos, mockPromocoes } from '../../mocks/db';
import './Produtos.css';

export const Produtos = () => {
  const [produtos, setProdutos] = useState(mockProdutos);
  const [promocoes] = useState(mockPromocoes);

  const getNomePromocao = (promocaoId) => {
    if (!promocaoId) return 'N/A';
    const promo = promocoes.find((p) => p.id === promocaoId);
    return promo ? promo.nome : 'N/A';
  };

  const calcularPrecoPromocional = (produto) => {
    if (!produto.promocaoId) {
      return produto.precoAtual;
    }
    const promo = promocoes.find((p) => p.id === produto.promocaoId);
    if (promo) {
      const desconto = produto.precoAtual * promo.taxaDeDesconto;
      return (produto.precoAtual - desconto).toFixed(2);
    }
    return produto.precoAtual;
  };

  const formatarData = (data) => {
    return new Date(data).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
  };

  return (
    <div className="produtos-container">
      <div className="header-container">
        <h2>Gestão de Produtos</h2>
        <button className="btn-novo">Novo Produto</button>
      </div>

      <table className="produtos-tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Tipo</th>
            <th>Preço Atual</th>
            <th>Promoção</th>
            <th>Preço Promocional</th>
            <th>Data de Validade</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {produtos.map((produto) => (
            <tr key={produto.id}>
              <td>{produto.nome}</td>
              <td>{produto.tipo}</td>
              <td>R$ {produto.precoAtual.toFixed(2)}</td>
              <td>{getNomePromocao(produto.promocaoId)}</td>
              <td>R$ {calcularPrecoPromocional(produto)}</td>
              <td>{formatarData(produto.dataValidade)}</td>
              <td className="acoes">
                <button className="btn-editar">Editar</button>
                <button className="btn-remover">Remover</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};