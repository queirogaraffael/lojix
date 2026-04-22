import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Modal } from '../../components/Modal/Modal';
import { CategoriaForm } from './CategoriaForm';
import './Categorias.css';

export const Categorias = () => {
  const [categorias, setCategorias] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [categoriaAtual, setCategoriaAtual] = useState(null);

  const fetchCategorias = async () => {
    try {
      setLoading(true);
      // Buscando tamanho maior para listar todas por enquanto
      const response = await api.get('/categorias?page=0&size=100');
      setCategorias(response.data.content);
    } catch (error) {
      console.error(error);
      alert("Erro ao carregar categorias.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCategorias();
  }, []);

  const handleOpenModal = (categoria = null) => {
    setCategoriaAtual(categoria);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setCategoriaAtual(null);
    setIsModalOpen(false);
  };

  const handleSave = async (dadosCategoria) => {
    try {
      if (categoriaAtual) {
        await api.put(`/categorias/${categoriaAtual.id}`, dadosCategoria);
        alert('Categoria atualizada com sucesso!');
      } else {
        await api.post('/categorias', dadosCategoria);
        alert('Categoria criada com sucesso!');
      }
      fetchCategorias();
      handleCloseModal();
    } catch (error) {
      console.error(error);
      let msg = "Erro ao salvar categoria.";
      if (error.response && error.response.data) {
          // Tratamento simples de erro
           if (error.response.data.message) {
               msg = error.response.data.message;
           }
      }
      alert(msg);
    }
  };

  return (
    <div className="categorias-container">
      <div className="header-container">
        <h2>Gestão de Categorias</h2>
        <button className="btn-novo" onClick={() => handleOpenModal(null)}>
          Nova Categoria
        </button>
      </div>

      {loading ? <p>Carregando...</p> : (
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