import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { listarFuncionarios, criarFuncionario, atualizarFuncionario, desligarFuncionario } from '../../services/funcionariosService';
import { Modal } from '../../components/Modal/Modal';
import Avatar from '../../components/Avatar/Avatar';
import { FuncionarioForm } from './FuncionarioForm';
import './Funcionarios.css';

export const Funcionarios = () => {
  const [paginaAtual, setPaginaAtual] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [funcionarioAtual, setFuncionarioAtual] = useState(null);
  const queryClient = useQueryClient();

  const funcionariosQuery = useQuery({
    queryKey: ['funcionarios', paginaAtual],
    queryFn: () => listarFuncionarios(paginaAtual, 15)
  });

  const funcionarios = funcionariosQuery.data?.data?.content || [];
  const loading = funcionariosQuery.isLoading;

  const handleOpenModal = (funcionario = null) => {
    setFuncionarioAtual(funcionario);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setFuncionarioAtual(null);
    setIsModalOpen(false);
  };

  const mutationSalvar = useMutation({
    mutationFn: async (dadosFuncionario) => {
      if (funcionarioAtual) {
        const payload = {
          cargo: dadosFuncionario.cargo,
          salario: dadosFuncionario.salario,
          usuarioUpdateDTO: {}
        };
        await atualizarFuncionario(funcionarioAtual.id, payload);
      } else {
        const payload = {
          cargo: dadosFuncionario.cargo,
          salario: dadosFuncionario.salario,
          role: dadosFuncionario.role,
          usuarioRequestDTO: {
            name: dadosFuncionario.name,
            email: dadosFuncionario.email,
            username: dadosFuncionario.username,
            cpf: dadosFuncionario.cpf,
            password: dadosFuncionario.password,
            dataNascimento: dadosFuncionario.dataNascimento.split('-').reverse().join('-')
          }
        };
        await criarFuncionario(payload);
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['funcionarios']);
      alert('Funcionário salvo com sucesso!');
      handleCloseModal();
    },
    onError: (error) => {
      console.error(error);
      let msg = "Erro desconhecido ao salvar.";
      if (error.response && error.response.data) {
        const data = error.response.data;
        if (typeof data === 'object' && !data.message && !data.error) {
          const mensagens = Object.entries(data)
            .map(([campo, mensagem]) => `- ${campo}: ${mensagem}`)
            .join('\n');
          msg = `Erros de validação:\n${mensagens}`;
        } else if (data.message) {
          msg = data.message;
        } else if (typeof data === 'string') {
          msg = data;
        } else if (data.error) {
          msg = data.error;
        }
      }
      alert(msg);
    }
  });

  const mutationExcluir = useMutation({
    mutationFn: (id) => desligarFuncionario(id),
    onSuccess: () => {
      queryClient.invalidateQueries(['funcionarios']);
      alert('Funcionário desligado com sucesso!');
    },
    onError: (error) => {
      console.error(error);
      alert("Erro ao tentar desligar o funcionário.");
    }
  });

  const handleSave = (dados) => mutationSalvar.mutate(dados);
  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja desligar este funcionário?')) {
      mutationExcluir.mutate(id);
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

      {loading ? <p>Carregando...</p> : (
        <>
        <table className="funcionarios-tabela">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Cargo</th>
              <th>Salário</th>
              <th>Email</th>
              <th>CPF</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {funcionarios.map((func) => (
              <tr key={func.id}>
                <td>{func.usuarioResponseDTO.name}</td>
                <td>{func.cargo}</td>
                <td>R$ {func.salario?.toFixed(2)}</td>
                <td>{func.usuarioResponseDTO.email}</td>
                <td>{func.usuarioResponseDTO.cpf}</td>
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
                    Desligar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="paginacao-controles" style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
          <button 
            onClick={() => setPaginaAtual(0)} 
            disabled={paginaAtual === 0 || funcionariosQuery.isFetching}>
            {'<< Primeira'}
          </button>
          <button 
            onClick={() => setPaginaAtual(old => Math.max(old - 1, 0))} 
            disabled={paginaAtual === 0 || funcionariosQuery.isFetching}>
            {'< Anterior'}
          </button>
          <span style={{ padding: '5px 10px' }}>Página {paginaAtual + 1}</span>
          <button 
            onClick={() => setPaginaAtual(old => old + 1)} 
            disabled={funcionariosQuery.data?.data?.last || funcionariosQuery.isFetching}>
            {'Próxima >'}
          </button>
        </div>
      </>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            {funcionarioAtual && (
              <Avatar 
                name={funcionarioAtual.usuarioResponseDTO.name} 
                fotoUrl={funcionarioAtual.usuarioResponseDTO.fotoUrl} 
                size="medium" 
              />
            )}
            <span>{funcionarioAtual ? 'Editar Funcionário' : 'Novo Funcionário'}</span>
          </div>
        }
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