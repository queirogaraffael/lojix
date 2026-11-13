import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { listarFuncionarios, criarFuncionario, atualizarFuncionario, desligarFuncionario } from '../../services/funcionariosService';
import { Modal } from '../../components/Modal/Modal';
import Avatar from '../../components/Avatar/Avatar';
import { FuncionarioForm } from './FuncionarioForm';
import styles from './Funcionarios.module.css';

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

  const formatarSalario = (valor) => {
    if (valor === null || valor === undefined) return '-';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor);
  };

  const formatarCPF = (cpf) => {
    if (!cpf) return '-';
    const nums = cpf.replace(/\D/g, '');
    if (nums.length !== 11) return cpf;
    return nums.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  const renderRoleBadge = (role) => {
    const roleMap = {
      ADMIN: { label: 'Administrador', bg: '#cce5ff', color: '#004085' },
      ATENDENTE: { label: 'Atendente', bg: '#d4edda', color: '#155724' },
      ESTOQUISTA: { label: 'Estoquista', bg: '#fff3cd', color: '#856404' }
    };
    const config = roleMap[role] || { label: role || 'Geral', bg: '#e2e3e5', color: '#383d41' };
    return (
      <span style={{
        backgroundColor: config.bg,
        color: config.color,
        padding: '2px 8px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: 'bold',
        display: 'inline-block'
      }}>
        {config.label}
      </span>
    );
  };

  const handleSave = (dados) => mutationSalvar.mutate(dados);
  const handleDelete = (id) => {
    if (window.confirm('Tem certeza que deseja desligar este funcionário?')) {
      mutationExcluir.mutate(id);
    }
  };

  return (
    <div className={styles['funcionarios-container']}>
      <div className={styles['header-container']}>
        <h2>Gestão de Funcionários</h2>
        <button className={styles['btn-novo']} onClick={() => handleOpenModal(null)}>
          Novo Funcionário
        </button>
      </div>

      {loading ? <p>Carregando...</p> : (
        <>
        <table className={styles['funcionarios-tabela']}>
          <thead>
            <tr>
              <th>Nome</th>
              <th>Cargo</th>
              <th>Nível de Acesso</th>
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
                <td>{renderRoleBadge(func.usuarioResponseDTO.role)}</td>
                <td>{formatarSalario(func.salario)}</td>
                <td>{func.usuarioResponseDTO.email}</td>
                <td>{formatarCPF(func.usuarioResponseDTO.cpf)}</td>
                <td className={styles.acoes}>
                  <button
                    className={styles['btn-editar']}
                    onClick={() => handleOpenModal(func)}
                  >
                    Editar
                  </button>
                  <button
                    className={styles['btn-remover']}
                    onClick={() => handleDelete(func.id)}
                  >
                    Desligar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className={styles['paginacao-controles']} style={{ marginTop: '20px', display: 'flex', gap: '10px', justifyContent: 'center' }}>
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