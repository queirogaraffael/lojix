import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const ProtectedRoute = ({ children }) => {
  const { user, checkAuth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      const isAuthenticated = checkAuth();
      if (!isAuthenticated) {
        navigate('/login');
      }
    }
  }, [user, checkAuth, navigate]);

  if (!user) {
    return null;
  }

  return children;
};