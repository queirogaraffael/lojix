import React, { useState, useEffect } from 'react';
import { listarProdutos, criarProduto, atualizarProduto, desativarProduto } from '../../services/produtosService';
import { listarCategorias } from '../../services/categoriasService';
import { listarPromocoes, associarProduto, removerAssociacaoProduto } from '../../services/promocoesService';
import { Modal } from '../../components/Modal/Modal';
import { ProdutoForm } from './ProdutoForm';
import './Produtos.css';

export const Produtos = () => {
  const [produtos, setProdutos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [promocoes, setPromocoes] = useState([]);
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [produtoAtual, setProdutoAtual] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [prodRes, catRes, promoRes] = await Promise.all([
        listarProdutos(0, 50),
        listarCategorias(0, 50),
        listarPromocoes(0, 50)
      ]);

      setProdutos(prodRes.data.content);
      setCategorias(catRes.data.content);
      setPromocoes(promoRes.data.content);
    } catch (error) {
      console.error(error);
      alert("Erro ao carregar produtos.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const getNomeCategoria = (catId) => {
    const cat = categorias.find(c => c.id === catId);
    return cat ? cat.nome : 'Desconhecida';
  };

  const getNomePromocao = (promoId) => {
    if (!promoId) return '—';
    const promo = promocoes.find(p => p.id === promoId);
    return promo ? promo.nome : '—';
  };



  const formatarData = (data) => {
    if (!data) return '-';
    return new Date(data + 'T12:00:00').toLocaleDateString('pt-BR');
  };

  const handleOpenModal = (produto = null) => {
    setProdutoAtual(produto);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setProdutoAtual(null);
    setIsModalOpen(false);
  };

  const handleSave = async (dados) => {
    try {
      const { categoriaId, promocaoId, ...produtoDto } = dados;

      if (produtoAtual) {
        await atualizarProduto(produtoAtual.id, produtoDto);

        if (promocaoId !== produtoAtual.promocaoId) {
            if (promocaoId) {
                await associarProduto(promocaoId, produtoAtual.id);
            } else {
                await removerAssociacaoProduto(produtoAtual.id);
            }
        }
        alert('Produto atualizado com sucesso!');

      } else {
        const response = await criarProduto(categoriaId, produtoDto);
        const novoId = response.data.id;

        if (promocaoId) {
            await associarProduto(promocaoId, novoId);
        }
        alert('Produto criado com sucesso!');
      }

      fetchData();
      handleCloseModal();
    } catch (error) {
      console.error(error);
      const msg = error.response?.data?.message || "Erro ao salvar.";
      alert(`Erro: ${msg}`);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja desativar este produto?')) {
      try {
        await desativarProduto(id);
        alert('Produto desativado!');
        fetchData();
      } catch (error) {
        console.error(error);
      }
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

      {loading ? <p>Carregando...</p> : (
        <table className="produtos-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Categoria</th>
              <th>Preço (R$)</th>
              <th>Estoque</th>
              <th>Promoção</th>
              <th>Preço Final</th>
              <th>Validade</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtos.map((prod) => {
                return (
                    <tr key={prod.id}>
                    <td>{prod.nome}</td>
                    <td>{getNomeCategoria(prod.categoriaId)}</td>
                    <td style={{ textDecoration: prod.emPromocao ? 'line-through' : 'none', color: prod.emPromocao ? '#999' : 'inherit' }}>
                        {prod.precoBase.toFixed(2)}
                    </td>
                    <td>{prod.quantidadeEstoque}</td>
                    <td>
                      {prod.emPromocao ? (
                        <span style={{ backgroundColor: '#ffc107', padding: '2px 6px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>
                          {prod.nomePromocao}
                        </span>
                      ) : (
                        getNomePromocao(prod.promocaoId)
                      )}
                    </td>
                    <td style={{ fontWeight: 'bold', color: prod.emPromocao ? '#28a745' : 'inherit' }}>
                        {prod.precoPromocional.toFixed(2)}
                    </td>
                    <td>{formatarData(prod.dataValidade)}</td>
                    <td className="acoes">
                        <button className="btn-editar" onClick={() => handleOpenModal(prod)}>Editar</button>
                        <button className="btn-remover" onClick={() => handleDelete(prod.id)}>Excluir</button>
                    </td>
                    </tr>
                );
            })}
          </tbody>
        </table>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={produtoAtual ? 'Editar Produto' : 'Novo Produto'}
      >
        <ProdutoForm
          produto={produtoAtual}
          categorias={categorias}
          promocoes={promocoes}
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};