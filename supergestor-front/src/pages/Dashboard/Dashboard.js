import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { mockProdutos } from '../../mocks/db';
import { mockFuncionarios } from '../../mocks/db';
import { mockPromocoes } from '../../mocks/db';
import { mockClientes } from '../../mocks/db';
import './Dashboard.css';

export const Dashboard = () => {
  const { user } = useAuth();

  const totalProdutos = mockProdutos.length;
  const totalFuncionarios = mockFuncionarios.length;
  const totalPromocoes = mockPromocoes.length;
  const totalClientes = mockClientes.length;

  return (
    <div className="dashboard-container">
      <h2>Bem-vindo ao Painel, {user.username}!</h2>
      <p>Este é o resumo atual do seu sistema:</p>

      <div className="dashboard-cards">
        <div className="card">
          <h3>Total de Produtos</h3>
          <p className="card-value">{totalProdutos}</p>
        </div>
        <div className="card">
          <h3>Total de Funcionários</h3>
          <p className="card-value">{totalFuncionarios}</p>
        </div>
        <div className="card">
          <h3>Total de Promoções</h3>
          <p className="card-value">{totalPromocoes}</p>
        </div>
        <div className="card">
          <h3>Total de Clientes</h3>
          <p className="card-value">{totalClientes}</p>
        </div>
      </div>
    </div>
  );
};