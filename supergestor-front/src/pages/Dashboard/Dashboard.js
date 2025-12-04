import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import api from '../../services/api';
import './Dashboard.css';

export const Dashboard = () => {
  const { user } = useAuth();
  
  const [counts, setCounts] = useState({
    produtos: 0,
    funcionarios: 0,
    promocoes: 0,
    clientes: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // Busca apenas 1 item de cada para obter o metadado "totalElements" do Spring Page de forma leve
        const [produtosRes, funcionariosRes, promocoesRes, clientesRes] = await Promise.all([
          api.get('/produtos?page=0&size=1'),
          api.get('/funcionarios?page=0&size=1'),
          api.get('/promocoes?page=0&size=1'),
          api.get('/clientes?page=0&size=1')
        ]);

        setCounts({
          produtos: produtosRes.data.totalElements || 0,
          funcionarios: funcionariosRes.data.totalElements || 0,
          promocoes: promocoesRes.data.totalElements || 0,
          clientes: clientesRes.data.totalElements || 0
        });
      } catch (error) {
        console.error('Erro ao buscar dados do dashboard:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="header-container">
            <h2>Bem-vindo ao Painel, {user?.name || user?.username}!</h2>
        </div>
        <p>Carregando dados do sistema...</p>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="header-container">
        <h2>Bem-vindo ao Painel, {user?.name || user?.username}!</h2>
      </div>
      <p>Este é o resumo atual do seu sistema:</p>

      <div className="dashboard-cards">
        <div className="card">
          <h3>Total de Produtos</h3>
          <p className="card-value">{counts.produtos}</p>
        </div>
        <div className="card">
          <h3>Total de Funcionários</h3>
          <p className="card-value">{counts.funcionarios}</p>
        </div>
        <div className="card">
          <h3>Total de Promoções</h3>
          <p className="card-value">{counts.promocoes}</p>
        </div>
        <div className="card">
          <h3>Total de Clientes</h3>
          <p className="card-value">{counts.clientes}</p>
        </div>
      </div>
    </div>
  );
};