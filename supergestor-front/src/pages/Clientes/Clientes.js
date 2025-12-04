import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Modal } from '../../components/Modal/Modal';
import { ClienteForm } from './ClienteForm';
import './Clientes.css';

export const Clientes = () => {
  const [clientes, setClientes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchClientes = async () => {
    try {
      setLoading(true);
      const response = await api.get('/clientes?page=0&size=20');
      setClientes(response.data.content);
    } catch (error) {
      console.error(error);
      alert("Erro ao carregar clientes.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClientes();
  }, []);

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleSave = async (dadosCliente) => {
    try {
      const payload = {
        tempoFidelidade: dadosCliente.tempoFidelidade,
        usuarioRequestDTO: {
            name: dadosCliente.name,
            email: dadosCliente.email,
            username: dadosCliente.username,
            cpf: dadosCliente.cpf,
            password: dadosCliente.password
        }
      };

      await api.post('/clientes', payload);
      alert('Cliente cadastrado com sucesso!');
      fetchClientes();
      handleCloseModal();
    } catch (error) {
      console.error(error);
      let msg = "Erro ao salvar cliente.";
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

  const handleNotImplemented = () => {
    alert("Esta funcionalidade (Editar/Excluir) não foi implementada no Backend (ClienteController) ainda.");
  };

  const formatarData = (data) => {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
  };

  return (
    <div className="clientes-container">
      <div className="header-container">
        <h2>Gestão de Clientes</h2>
        <button className="btn-novo" onClick={handleOpenModal}>
          Novo Cliente
        </button>
      </div>

      {loading ? <p>Carregando...</p> : (
        <table className="clientes-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Email</th>
              <th>CPF</th>
              <th>Fidelidade Desde</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {clientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.usuario.name}</td>
                <td>{cliente.usuario.email}</td>
                <td>{cliente.usuario.cpf}</td>
                <td>{formatarData(cliente.tempoFidelidade)}</td>
                <td className="acoes">
                  <button
                    className="btn-editar"
                    onClick={handleNotImplemented}
                    style={{ opacity: 0.5, cursor: 'not-allowed' }}
                  >
                    Editar
                  </button>
                  <button
                    className="btn-remover"
                    onClick={handleNotImplemented}
                    style={{ opacity: 0.5, cursor: 'not-allowed' }}
                  >
                    Excluir
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
        title="Novo Cliente"
      >
        <ClienteForm
          onSave={handleSave}
          onCancel={handleCloseModal}
        />
      </Modal>
    </div>
  );
};