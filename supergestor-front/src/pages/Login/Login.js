import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import './Login.css';

export const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    const result = await login(email, password);

    if (!result.success) {
      setError(result.message);
      setIsLoading(false);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h1>SuperGestor</h1>
        
        {error && <div className="error-message" style={{color: '#dc3545', marginBottom: '1rem', padding: '10px', backgroundColor: '#f8d7da', borderRadius: '4px'}}>{error}</div>}

        <div className="form-control">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            disabled={isLoading}
            placeholder="exemplo@empresa.com"
          />
        </div>
        <div className="form-control">
          <label htmlFor="password">Senha</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={isLoading}
          />
        </div>
        <button type="submit" className="login-button" disabled={isLoading}>
          {isLoading ? 'Entrando...' : 'Entrar'}
        </button>
        <p className="login-hint">
          Admin: <strong>admin@meuapp.com</strong><br/>
          Senha: <strong>senhaSuperSecreta456</strong>
        </p>
      </form>
    </div>
  );
};