import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { listarProdutos, criarProduto, atualizarProduto, desativarProduto } from '../../services/produtosService';
import { listarCategorias } from '../../services/categoriasService';
import { listarPromocoes, associarProduto, removerAssociacaoProduto } from '../../services/promocoesService';
import { Modal } from '../../components/Modal/Modal';
import { ProdutoForm } from './ProdutoForm';
import './Produtos.css';

export const Produtos = () => {
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [produtoAtual, setProdutoAtual] = useState(null);
  
  const queryClient = useQueryClient();

  const produtosQuery = useQuery({
    queryKey: ['produtos', paginaAtual],
    queryFn: () => listarProdutos(paginaAtual, 15)
  });

  const categoriasQuery = useQuery({
    queryKey: ['categorias'],
    queryFn: () => listarCategorias(0, 500),
    staleTime: Infinity, // Cache eterno na sessão
  });

  const promocoesQuery = useQuery({
    queryKey: ['promocoes'],
    queryFn: () => listarPromocoes(0, 500),
    staleTime: Infinity,
  });

  const loading = produtosQuery.isLoading || categoriasQuery.isLoading || promocoesQuery.isLoading;
  const produtos = produtosQuery.data?.data?.content || [];
  const categorias = categoriasQuery.data?.data?.content || [];
  const promocoes = promocoesQuery.data?.data?.content || [];

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

  const mutationSalvar = useMutation({
    mutationFn: async (dados) => {
      const { categoriaId, promocaoId, ...produtoDto } = dados;
      if (produtoAtual) {
        await atualizarProduto(produtoAtual.id, produtoDto);
        if (promocaoId !== produtoAtual.promocaoId) {
            if (promocaoId) await associarProduto(promocaoId, produtoAtual.id);
            else await removerAssociacaoProduto(produtoAtual.id);
        }
      } else {
        const response = await criarProduto(categoriaId, produtoDto);
        if (promocaoId) await associarProduto(promocaoId, response.data.id);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['produtos']);
      alert('Produto salvo com sucesso!');
      handleCloseModal();
    },
    onError: (error) => {
      console.error(error);
      const msg = error.response?.data?.message || "Erro ao salvar.";
      alert(`Erro: ${msg}`);
    }
  });

  const mutationExcluir = useMutation({
    mutationFn: (id) => desativarProduto(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['produtos']);
      alert('Produto desativado!');
    },
    onError: (error) => {
      console.error(error);
      alert('Erro ao excluir.');
    }
  });

  const handleSave = (dados) => mutationSalvar.mutate(dados);
  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja desativar este produto?')) {
      mutationExcluir.mutate(id);
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
        <>
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
        <div className="paginacao-controles" style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
          <button 
            onClick={() => setPaginaAtual(0)} 
            disabled={paginaAtual === 0 || produtosQuery.isFetching}>
            {'<< Primeira'}
          </button>
          <button 
            onClick={() => setPaginaAtual(old => Math.max(old - 1, 0))} 
            disabled={paginaAtual === 0 || produtosQuery.isFetching}>
            {'< Anterior'}
          </button>
          <span style={{ padding: '5px 10px' }}>Página {paginaAtual + 1}</span>
          <button 
            onClick={() => setPaginaAtual(old => old + 1)} 
            disabled={produtosQuery.data?.data?.last || produtosQuery.isFetching}>
            {'Próxima >'}
          </button>
        </div>
      </>
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