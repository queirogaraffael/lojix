import React, { useState } from 'react';
import { mockProdutos, mockPromocoes } from '../../mocks/db';
import { Modal } from '../../components/Modal/Modal';
import { ProdutoForm } from './ProdutoForm';
import './Produtos.css';

export const Produtos = () => {
  const [produtos, setProdutos] = useState(mockProdutos);
  const [promocoes] = useState(mockPromocoes);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [produtoAtual, setProdutoAtual] = useState(null);

  const getNomePromocao = (promocaoId) => {
    if (!promocaoId) return 'N/A';
    const promo = promocoes.find((p) => p.id === promocaoId);
    return promo ? promo.nome : 'N/A';
  };

  const calcularPrecoPromocional = (produto) => {
    if (!produto.promocaoId) {
      return produto.precoAtual.toFixed(2);
    }
    const promo = promocoes.find((p) => p.id === produto.promocaoId);
    if (promo) {
      const desconto = produto.precoAtual * promo.taxaDeDesconto;
      return (produto.precoAtual - desconto).toFixed(2);
    }
    return produto.precoAtual.toFixed(2);
  };

  const formatarData = (data) => {
    return new Date(data).toLocaleDateString('pt-BR', { timeZone: 'UTC' });
  };

  const handleOpenModal = (produto = null) => {
    setProdutoAtual(produto);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setProdutoAtual(null);
    setIsModalOpen(false);
  };

  const handleSave = (produto) => {
    if (produto.id) {
      // Editar
      setProdutos(
        produtos.map((p) => (p.id === produto.id ? produto : p))
      );
    } else {
      // Criar
      const novoProduto = {
        ...produto,
        id: `prod${new Date().getTime()}`,
      };
      setProdutos([...produtos, novoProduto]);
    }
    handleCloseModal();
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja remover este produto?')) {
      setProdutos(produtos.filter((p) => p.id !== id));
    }
  };

  return (
    <div className="produtos-container">
      <div className="header-container">
        <h2>Gestão de Produtos</h2>
        <button className="btn-novo" onClick={() => handleOpenModal(null)}>
          Novo Produto
        </button>
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
                <button
                  className="btn-editar"
                  onClick={() => handleOpenModal(produto)}
                >
                  Editar
                </button>
                <button
                  className="btn-remover"
                  onClick={() => handleDelete(produto.id)}
                >
                  Remover
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={produtoAtual ? 'Editar Produto' : 'Novo Produto'}
      >
        <ProdutoForm
          produto={produtoAtual}
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};