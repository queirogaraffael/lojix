import React, { useState } from 'react';
import { mockPromocoes } from '../../mocks/db';
import { Modal } from '../../components/Modal/Modal';
import { PromocaoForm } from './PromocaoForm';
import './Promocoes.css';

export const Promocoes = () => {
  const [promocoes, setPromocoes] = useState(mockPromocoes);

  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleSave = (promocao) => {
    const novaPromocao = {
      ...promocao,
      id: `p${new Date().getTime()}`,
    };
    setPromocoes([...promocoes, novaPromocao]);
    handleCloseModal();
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja remover esta promoção?')) {
      setPromocoes(promocoes.filter((p) => p.id !== id));
    }
  };

  return (
    <div className="promocoes-container">
      <div className="header-container">
        <h2>Gestão de Promoções</h2>
        <button className="btn-novo" onClick={handleOpenModal}>
          Nova Promoção
        </button>
      </div>

      <table className="promocoes-tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>Taxa de Desconto</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {promocoes.map((promo) => (
            <tr key={promo.id}>
              <td>{promo.nome}</td>
              <td>{(promo.taxaDeDesconto * 100).toFixed(0)}%</td>
              <td className="acoes">
                <button
                  className="btn-remover"
                  onClick={() => handleDelete(promo.id)}
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
        title={'Nova Promoção'}
      >
        <PromocaoForm
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};