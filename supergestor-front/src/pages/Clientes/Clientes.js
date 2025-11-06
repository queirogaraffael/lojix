import React, { useState } from 'react';
import { mockClientes } from '../../mocks/db';
import { Modal } from '../../components/Modal/Modal';
import { ClienteForm } from './ClienteForm';
import './Clientes.css';

export const Clientes = () => {
  const [clientes, setClientes] = useState(mockClientes);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [clienteAtual, setClienteAtual] = useState(null);

  const handleOpenModal = (cliente = null) => {
    setClienteAtual(cliente);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setClienteAtual(null);
    setIsModalOpen(false);
  };

  const calcularTempoCliente = (dataCadastro) => {
    const dataInicio = new Date(dataCadastro);
    const dataFim = new Date();
    
    let anos = dataFim.getFullYear() - dataInicio.getFullYear();
    let meses = dataFim.getMonth() - dataInicio.getMonth();
    
    if (meses < 0 || (meses === 0 && dataFim.getDate() < dataInicio.getDate())) {
      anos--;
      meses += 12;
    }
    
    return `${anos} anos e ${meses} meses`;
  };

  const handleSave = (cliente) => {
    if (cliente.id) {
      // Editar
      setClientes(clientes.map((c) => (c.id === cliente.id ? cliente : c)));
    } else {
      // Criar
      const novoCliente = {
        ...cliente,
        id: `c${new Date().getTime()}`,
      };
      setClientes([...clientes, novoCliente]);
    }
    handleCloseModal();
  };

  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja remover este cliente?')) {
      setClientes(clientes.filter((c) => c.id !== id));
    }
  };

  return (
    <div className="clientes-container">
      <div className="header-container">
        <h2>Gestão de Clientes</h2>
        <button className="btn-novo" onClick={() => handleOpenModal(null)}>
          Novo Cliente
        </button>
      </div>

      <table className="clientes-tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF/Identidade</th>
            <th>Idade</th>
            <th>Tempo de Cliente</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id}>
              <td>{cliente.nome}</td>
              <td>{cliente.cpf}</td>
              <td>{cliente.idade}</td>
              <td>{calcularTempoCliente(cliente.dataCadastro)}</td>
              <td className="acoes">
                <button
                  className="btn-editar"
                  onClick={() => handleOpenModal(cliente)}
                >
                  Editar
                </button>
                <button
                  className="btn-remover"
                  onClick={() => handleDelete(cliente.id)}
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
        title={clienteAtual ? 'Editar Cliente' : 'Novo Cliente'}
      >
        <ClienteForm
          cliente={clienteAtual}
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};