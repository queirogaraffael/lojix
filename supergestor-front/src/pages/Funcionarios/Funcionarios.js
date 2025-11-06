import React, { useState } from 'react';
import { mockFuncionarios } from '../../mocks/db';
import './Funcionarios.css';

export const Funcionarios = () => {
  const [funcionarios, setFuncionarios] = useState(mockFuncionarios);

  return (
    <div className="funcionarios-container">
      <div className="header-container">
        <h2>Gestão de Funcionários</h2>
        <button className="btn-novo">Novo Funcionário</button>
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
                <button className="btn-editar">Editar</button>
                <button className="btn-remover">Remover</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};