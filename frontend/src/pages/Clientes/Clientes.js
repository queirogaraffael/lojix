import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { listarClientes, criarCliente } from '../../services/clientesService';
import { Modal } from '../../components/Modal/Modal';
import { ClienteForm } from './ClienteForm';
import './Clientes.css';

export const Clientes = () => {
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const queryClient = useQueryClient();

  const clientesQuery = useQuery({
    queryKey: ['clientes', paginaAtual],
    queryFn: () => listarClientes(paginaAtual, 15)
  });

  const clientes = clientesQuery.data?.data?.content || [];
  const loading = clientesQuery.isLoading;

  const handleOpenModal = () => {
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const mutationSalvar = useMutation({
    mutationFn: (payload) => criarCliente(payload),
    onSuccess: () => {
      queryClient.invalidateQueries(['clientes']);
      alert('Cliente cadastrado com sucesso!');
      handleCloseModal();
    },
    onError: (error) => {
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
  });

  const handleSave = (dadosCliente) => {
    const payload = {
      usuarioRequestDTO: {
          name: dadosCliente.name,
          email: dadosCliente.email,
          username: dadosCliente.username,
          cpf: dadosCliente.cpf,
          password: dadosCliente.password,
          dataNascimento: dadosCliente.dataNascimento.split('-').reverse().join('-')
      }
    };
    mutationSalvar.mutate(payload);
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
        <>
        <table className="clientes-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Email</th>
              <th>CPF</th>
              <th>Membro Desde</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {clientes.map((cliente) => (
              <tr key={cliente.id}>
                <td>{cliente.usuario.name}</td>
                <td>{cliente.usuario.email}</td>
                <td>{cliente.usuario.cpf}</td>
                <td>{formatarData(cliente.membroDesde)}</td>
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
        <div className="paginacao-controles" style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
          <button 
            onClick={() => setPaginaAtual(0)} 
            disabled={paginaAtual === 0 || clientesQuery.isFetching}>
            {'<< Primeira'}
          </button>
          <button 
            onClick={() => setPaginaAtual(old => Math.max(old - 1, 0))} 
            disabled={paginaAtual === 0 || clientesQuery.isFetching}>
            {'< Anterior'}
          </button>
          <span style={{ padding: '5px 10px' }}>Página {paginaAtual + 1}</span>
          <button 
            onClick={() => setPaginaAtual(old => old + 1)} 
            disabled={clientesQuery.data?.data?.last || clientesQuery.isFetching}>
            {'Próxima >'}
          </button>
        </div>
      </>
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