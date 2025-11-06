import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

export const Dashboard = () => {
  const { user } = useAuth();
  return (
    <div>
      <h2>Bem-vindo ao Painel, {user.username}!</h2>
      <p>Este é o sistema de gestão do supermercado.</p>
    </div>
  );
};