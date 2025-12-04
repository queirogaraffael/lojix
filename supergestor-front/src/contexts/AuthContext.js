import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const carregarDadosArmazenados = async () => {
      const storedUser = localStorage.getItem('user');
      const storedToken = localStorage.getItem('token');

      if (storedUser && storedToken) {
        setUser(JSON.parse(storedUser));
        api.defaults.headers.Authorization = `Bearer ${storedToken}`;
      }
      setLoading(false);
    };

    carregarDadosArmazenados();
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token } = response.data;

      localStorage.setItem('token', token);
      api.defaults.headers.Authorization = `Bearer ${token}`;

      const userResponse = await api.get('/auth/me');
      const userData = userResponse.data;

      setUser(userData);
      localStorage.setItem('user', JSON.stringify(userData));
      
      navigate('/');
      return { success: true };
    } catch (error) {
      console.error("Erro no login:", error);
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      delete api.defaults.headers.Authorization;
      
      return { 
        success: false, 
        message: error.response?.data?.message || 'Falha ao realizar login. Verifique suas credenciais.'
      };
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    delete api.defaults.headers.Authorization;
    navigate('/login');
  };

  const checkAuth = () => {
    return !!localStorage.getItem('token');
  };

  const value = { user, login, logout, checkAuth, loading };

  return <AuthContext.Provider value={value}>{!loading && children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};