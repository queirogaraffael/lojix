import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './Unauthorized.module.css';

export const Unauthorized = () => {
  const navigate = useNavigate();

  return (
    <div className={styles['unauthorized-container']}>
      <div className={styles['unauthorized-card']}>
        <h1 className={styles['unauthorized-title']}>403</h1>
        <h2 className={styles['unauthorized-subtitle']}>Acesso Negado</h2>
        <p className={styles['unauthorized-text']}>
          Você não tem as permissões necessárias para acessar esta página.
        </p>
        <button className={styles['unauthorized-btn']} onClick={() => navigate('/')}>
          Voltar para o Dashboard
        </button>
      </div>
    </div>
  );
};
