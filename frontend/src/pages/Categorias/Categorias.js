import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { listarCategorias, criarCategoria, atualizarCategoria } from '../../services/categoriasService';
import { Modal } from '../../components/Modal/Modal';
import { CategoriaForm } from './CategoriaForm';
import './Categorias.css';

export const Categorias = () => {
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [categoriaAtual, setCategoriaAtual] = useState(null);
  const queryClient = useQueryClient();

  const categoriasQuery = useQuery({
    queryKey: ['categorias', 'paginated', paginaAtual],
    queryFn: () => listarCategorias(paginaAtual, 15)
  });

  const categorias = categoriasQuery.data?.data?.content || [];
  const loading = categoriasQuery.isLoading;

  const handleOpenModal = (categoria = null) => {
    setCategoriaAtual(categoria);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setCategoriaAtual(null);
    setIsModalOpen(false);
  };

  const mutationSalvar = useMutation({
    mutationFn: async (dadosCategoria) => {
      if (categoriaAtual) {
        await atualizarCategoria(categoriaAtual.id, dadosCategoria);
      } else {
        await criarCategoria(dadosCategoria);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['categorias']);
      alert('Categoria salva com sucesso!');
      handleCloseModal();
    },
    onError: (error) => {
      console.error(error);
      let msg = "Erro ao salvar categoria.";
      if (error.response && error.response.data) {
        if (error.response.data.message) {
          msg = error.response.data.message;
        }
      }
      alert(msg);
    }
  });

  const handleSave = (dadosCategoria) => mutationSalvar.mutate(dadosCategoria);

  return (
    <div className="categorias-container">
      <div className="header-container">
        <h2>Gestão de Categorias</h2>
        <button className="btn-novo" onClick={() => handleOpenModal(null)}>
          Nova Categoria
        </button>
      </div>

      {loading ? <p>Carregando...</p> : (
        <>
        <table className="categorias-tabela">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {categorias.map((cat) => (
              <tr key={cat.id}>
                <td>{cat.id}</td>
                <td>{cat.nome}</td>
                <td className="acoes">
                  <button
                    className="btn-editar"
                    onClick={() => handleOpenModal(cat)}
                  >
                    Editar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="paginacao-controles" style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
          <button 
            onClick={() => setPaginaAtual(0)} 
            disabled={paginaAtual === 0 || categoriasQuery.isFetching}>
            {'<< Primeira'}
          </button>
          <button 
            onClick={() => setPaginaAtual(old => Math.max(old - 1, 0))} 
            disabled={paginaAtual === 0 || categoriasQuery.isFetching}>
            {'< Anterior'}
          </button>
          <span style={{ padding: '5px 10px' }}>Página {paginaAtual + 1}</span>
          <button 
            onClick={() => setPaginaAtual(old => old + 1)} 
            disabled={categoriasQuery.data?.data?.last || categoriasQuery.isFetching}>
            {'Próxima >'}
          </button>
        </div>
      </>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={categoriaAtual ? 'Editar Categoria' : 'Nova Categoria'}
      >
        <CategoriaForm
          categoria={categoriaAtual}
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};