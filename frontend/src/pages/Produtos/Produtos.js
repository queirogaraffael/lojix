import React, { useState, useEffect } from 'react';
import api from '../../services/api';
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
        api.get('/produtos?page=0&size=50'),
        api.get('/categorias?page=0&size=100'),
        api.get('/promocoes?page=0&size=100')
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

  const calcularPrecoPromocional = (produto) => {
    if (!produto.promocaoId) return null;
    
    const promo = promocoes.find(p => p.id === produto.promocaoId);
    if (promo && promo.taxaDeDesconto) {
      const desconto = produto.preco * promo.taxaDeDesconto;
      return (produto.preco - desconto).toFixed(2);
    }
    return null;
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
        await api.put(`/produtos/${produtoAtual.id}`, produtoDto);

        if (promocaoId !== produtoAtual.promocaoId) {
            if (promocaoId) {
                await api.patch(`/promocoes/${promocaoId}/associar/${produtoAtual.id}`);
            } else {
                await api.patch(`/promocoes/remover/${produtoAtual.id}`);
            }
        }
        alert('Produto atualizado com sucesso!');

      } else {
        const response = await api.post(`/produtos/categoria/${categoriaId}`, produtoDto);
        const novoId = response.data.id;

        if (promocaoId) {
            await api.patch(`/promocoes/${promocaoId}/associar/${novoId}`);
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
        await api.patch(`/produtos/${id}/desativar`);
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
              <th>Promoção</th>
              <th>Preço Final</th>
              <th>Validade</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {produtos.map((prod) => {
                const precoPromo = calcularPrecoPromocional(prod);
                return (
                    <tr key={prod.id}>
                    <td>{prod.nome}</td>
                    <td>{getNomeCategoria(prod.categoriaId)}</td>
                    <td style={{ textDecoration: precoPromo ? 'line-through' : 'none', color: precoPromo ? '#999' : 'inherit' }}>
                        {prod.preco.toFixed(2)}
                    </td>
                    <td>{getNomePromocao(prod.promocaoId)}</td>
                    <td style={{ fontWeight: 'bold', color: precoPromo ? '#28a745' : 'inherit' }}>
                        {precoPromo ? precoPromo : prod.preco.toFixed(2)}
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