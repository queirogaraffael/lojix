import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Avatar from '../Avatar/Avatar';
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
          {(user?.role === 'ADMIN' || user?.role === 'ESTOQUISTA') && (
            <>
              <NavLink to="/produtos">Produtos</NavLink>
              <NavLink to="/categorias">Categorias</NavLink>
              <NavLink to="/promocoes">Promoções</NavLink>
            </>
          )}
          {user?.role === 'ADMIN' && (
            <NavLink to="/funcionarios">Funcionários</NavLink>
          )}
          {(user?.role === 'ADMIN' || user?.role === 'ATENDENTE') && (
            <NavLink to="/clientes">Clientes</NavLink>
          )}
        </div>
        <div className="navbar-user">
          <div className="user-profile-btn" onClick={() => navigate('/perfil')} title="Meu Perfil">
            <Avatar name={user?.name || user?.username} fotoUrl={user?.fotoUrl} size="small" />
            <span>Olá, {user?.name || user?.username}</span>
          </div>
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