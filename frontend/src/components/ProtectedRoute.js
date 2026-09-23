import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, checkAuth } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user) {
      const isAuthenticated = checkAuth();
      if (!isAuthenticated) {
        navigate('/login');
        return;
      }
    }

    if (user && allowedRoles && !allowedRoles.includes(user.role)) {
      navigate('/unauthorized');
    }
  }, [user, checkAuth, navigate, allowedRoles]);

  if (!user) {
    return null;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return null;
  }

  return children;
};
