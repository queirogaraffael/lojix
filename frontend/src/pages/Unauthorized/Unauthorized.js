import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Unauthorized.css';

export const Unauthorized = () => {
  const navigate = useNavigate();

  return (
    <div className="unauthorized-container">
      <div className="unauthorized-card">
        <h1 className="unauthorized-title">403</h1>
        <h2 className="unauthorized-subtitle">Acesso Negado</h2>
        <p className="unauthorized-text">
          Você não tem as permissões necessárias para acessar esta página.
        </p>
        <button className="unauthorized-btn" onClick={() => navigate('/')}>
          Voltar para o Dashboard
        </button>
      </div>
    </div>
  );
};
