import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../../contexts/AuthContext';
import { listarProdutos } from '../../services/produtosService';
import { listarFuncionarios } from '../../services/funcionariosService';
import { listarPromocoes } from '../../services/promocoesService';
import { listarClientes } from '../../services/clientesService';
import './Dashboard.css';

export const Dashboard = () => {
  const { user } = useAuth();
  
  const role = user?.role || '';
  const podeVerEstoque = role === 'ADMIN' || role === 'ESTOQUISTA';
  const podeVerFuncionarios = role === 'ADMIN';
  const podeVerClientes = role === 'ADMIN' || role === 'ATENDENTE';

  const queryProdutos = useQuery({
    queryKey: ['count_produtos'],
    queryFn: () => listarProdutos(0, 1),
    enabled: podeVerEstoque
  });

  const queryPromocoes = useQuery({
    queryKey: ['count_promocoes'],
    queryFn: () => listarPromocoes(0, 1),
    enabled: podeVerEstoque
  });

  const queryFuncionarios = useQuery({
    queryKey: ['count_funcionarios'],
    queryFn: () => listarFuncionarios(0, 1),
    enabled: podeVerFuncionarios
  });

  const queryClientes = useQuery({
    queryKey: ['count_clientes'],
    queryFn: () => listarClientes(0, 1),
    enabled: podeVerClientes
  });

  const loading = (podeVerEstoque && (queryProdutos.isLoading || queryPromocoes.isLoading)) ||
                  (podeVerFuncionarios && queryFuncionarios.isLoading) ||
                  (podeVerClientes && queryClientes.isLoading);

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
        {podeVerEstoque && (
          <>
            <div className="card">
              <h3>Total de Produtos</h3>
              <p className="card-value">
                {queryProdutos.isError ? 'Erro' : (queryProdutos.data?.data?.totalElements || 0)}
              </p>
            </div>
            <div className="card">
              <h3>Total de Promoções</h3>
              <p className="card-value">
                {queryPromocoes.isError ? 'Erro' : (queryPromocoes.data?.data?.totalElements || 0)}
              </p>
            </div>
          </>
        )}
        
        {podeVerFuncionarios && (
          <div className="card">
            <h3>Total de Funcionários</h3>
            <p className="card-value">
              {queryFuncionarios.isError ? 'Erro' : (queryFuncionarios.data?.data?.totalElements || 0)}
            </p>
          </div>
        )}

        {podeVerClientes && (
          <div className="card">
            <h3>Total de Clientes</h3>
            <p className="card-value">
              {queryClientes.isError ? 'Erro' : (queryClientes.data?.data?.totalElements || 0)}
            </p>
          </div>
        )}
      </div>
    </div>
  );
};