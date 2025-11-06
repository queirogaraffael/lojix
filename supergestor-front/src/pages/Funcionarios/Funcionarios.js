import React, { useState } from 'react';
import { mockFuncionarios } from '../../mocks/db';
import { Modal } from '../../components/Modal/Modal';
import { FuncionarioForm } from './FuncionarioForm';
import './Funcionarios.css';

export const Funcionarios = () => {
  const [funcionarios, setFuncionarios] = useState(mockFuncionarios);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [funcionarioAtual, setFuncionarioAtual] = useState(null);

  const handleOpenModal = (funcionario = null) => {
    setFuncionarioAtual(funcionario);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setFuncionarioAtual(null);
    setIsModalOpen(false);
  };

  const handleSave = (funcionario) => {
    if (funcionario.id) {
      // Editar
      setFuncionarios(
        funcionarios.map((f) =>
          f.id === funcionario.id
            ? {
                ...funcionario,
                password: f.password, // Mantém a senha mockada antiga
              }
            : f
        )
      );
    } else {
      // Criar
      const novoFuncionario = {
        ...funcionario,
        id: `f${new Date().getTime()}`,
      };
      setFuncionarios([...funcionarios, novoFuncionario]);
    }
    handleCloseModal();
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja remover este funcionário?')) {
      setFuncionarios(funcionarios.filter((f) => f.id !== id));
    }
  };

  return (
    <div className="funcionarios-container">
      <div className="header-container">
        <h2>Gestão de Funcionários</h2>
        <button className="btn-novo" onClick={() => handleOpenModal(null)}>
          Novo Funcionário
        </button>
      </div>

      <table className="funcionarios-tabela">
        <thead>
          <tr>
            <th>Foto</th>
            <th>Nome</th>
            <th>Email</th>
            <th>CPF</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {funcionarios.map((func) => (
            <tr key={func.id}>
              <td>
                <img
                  src={func.foto}
                  alt={func.nome}
                  className="funcionario-foto"
                />
              </td>
              <td>{func.nome}</td>
              <td>{func.email}</td>
              <td>{func.cpf}</td>
              <td className="acoes">
                <button
                  className="btn-editar"
                  onClick={() => handleOpenModal(func)}
                >
                  Editar
                </button>
                <button
                  className="btn-remover"
                  onClick={() => handleDelete(func.id)}
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
        title={funcionarioAtual ? 'Editar Funcionário' : 'Novo Funcionário'}
      >
        <FuncionarioForm
          funcionario={funcionarioAtual}
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};