import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Modal } from '../../components/Modal/Modal';
import { FuncionarioForm } from './FuncionarioForm';
import './Funcionarios.css';

export const Funcionarios = () => {
  const [funcionarios, setFuncionarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [funcionarioAtual, setFuncionarioAtual] = useState(null);

  const fetchFuncionarios = async () => {
    try {
      setLoading(true);
      const response = await api.get('/funcionarios?page=0&size=20');
      setFuncionarios(response.data.content);
    } catch (error) {
      console.error(error);
      alert("Erro ao carregar lista de funcionários.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFuncionarios();
  }, []);

  const handleOpenModal = (funcionario = null) => {
    setFuncionarioAtual(funcionario);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setFuncionarioAtual(null);
    setIsModalOpen(false);
  };

  const handleSave = async (dadosFuncionario) => {
    try {
      if (funcionarioAtual) {
        const payload = {
          cargo: dadosFuncionario.cargo,
          salario: dadosFuncionario.salario,
          usuarioUpdateDTO: {}
        };
        await api.put(`/funcionarios/${funcionarioAtual.id}`, payload);
        alert('Funcionário atualizado com sucesso!');
      } else {
        const payload = {
          cargo: dadosFuncionario.cargo,
          salario: dadosFuncionario.salario,
          usuarioRequestDTO: {
            name: dadosFuncionario.name,
            email: dadosFuncionario.email,
            username: dadosFuncionario.username,
            cpf: dadosFuncionario.cpf,
            password: dadosFuncionario.password
          }
        };
        await api.post('/funcionarios', payload);
        alert('Funcionário criado com sucesso!');
      }
      fetchFuncionarios();
      handleCloseModal();
    } catch (error) {
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
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja desligar este funcionário?')) {
      try {
        await api.patch(`/funcionarios/${id}/desligar`);
        alert('Funcionário desligado com sucesso!');
        fetchFuncionarios();
      } catch (error) {
        console.error(error);
        alert("Erro ao tentar desligar o funcionário.");
      }
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
      )}

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