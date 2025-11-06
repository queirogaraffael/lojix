import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import './Layout.css';

export const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="layout-container">
      <nav className="navbar">
        <div className="navbar-brand" onClick={() => navigate('/')}>
          SuperGestor
        </div>
        <div className="navbar-links">
          <NavLink to="/">Dashboard</NavLink>
          <NavLink to="/produtos">Produtos</NavLink>
        </div>
        <div className="navbar-user">
          <span>Olá, {user?.username}</span>
          <button onClick={handleLogout} className="logout-button">
            Sair
          </button>
        </div>
      </nav>
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};