import React, { useState, useEffect } from 'react';
import { listarPromocoes, criarPromocao, desativarPromocao } from '../../services/promocoesService';
import { Modal } from '../../components/Modal/Modal';
import { PromocaoForm } from './PromocaoForm';
import './Promocoes.css';

export const Promocoes = () => {
  const [promocoes, setPromocoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchPromocoes = async () => {
    try {
      setLoading(true);
      const response = await listarPromocoes(0, 20);
      setPromocoes(response.data.content);
    } catch (error) {
      console.error(error);
      alert("Erro ao carregar promoções.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPromocoes();
  }, []);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleSave = async (dadosPromocao) => {
    try {
      await criarPromocao(dadosPromocao);
      alert('Promoção criada com sucesso!');
      fetchPromocoes();
      handleCloseModal();
    } catch (error) {
      console.error(error);
      let msg = "Erro ao salvar promoção.";
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'object' && !data.message) {
            msg = Object.entries(data).map(([k, v]) => `${k}: ${v}`).join('\n');
        } else if (data.message) {
            msg = data.message;
        }
      }
      alert(msg);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja desativar esta promoção?')) {
      try {
        await desativarPromocao(id);
        alert('Promoção desativada com sucesso!');
        fetchPromocoes();
      } catch (error) {
        console.error(error);
        alert("Erro ao desativar promoção.");
      }
    }
  };

  const formatarData = (data) => {
    if (!data) return '-';
    return new Date(data + 'T12:00:00').toLocaleDateString('pt-BR');
  };

  return (
    <div className="promocoes-container">
      <div className="header-container">
        <h2>Gestão de Promoções</h2>
        <button className="btn-novo" onClick={handleOpenModal}>
          Nova Promoção
        </button>
      </div>

      {loading ? <p>Carregando...</p> : (
        <table className="promocoes-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Taxa de Desconto</th>
              <th>Início</th>
              <th>Fim</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {promocoes.map((promo) => (
              <tr key={promo.id}>
                <td>{promo.nome}</td>
                <td>{(promo.taxaDeDesconto).toFixed(0)}%</td>
                <td>{formatarData(promo.inicio)}</td>
                <td>{formatarData(promo.fim)}</td>
                <td className="acoes">
                  <button
                    className="btn-remover"
                    onClick={() => handleDelete(promo.id)}
                  >
                    Desativar
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
        title="Nova Promoção"
      >
        <PromocaoForm
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};