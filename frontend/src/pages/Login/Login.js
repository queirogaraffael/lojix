import React, { useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import styles from "./Login.module.css";

export const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    const result = await login(username, password);

    if (!result.success) {
      setError(result.message);
      setIsLoading(false);
    }
  };

  return (
    <div className={styles['login-container']}>
      <form className={styles['login-form']} onSubmit={handleSubmit}>
        <h1>Lojix</h1>

        {error && (
          <div
            className={styles['error-message']}
            style={{
              color: "#dc3545",
              marginBottom: "1rem",
              padding: "10px",
              backgroundColor: "#f8d7da",
              borderRadius: "4px",
            }}
          >
            {error}
          </div>
        )}

        <div className={styles['form-control']}>
          <label htmlFor="username">E-mail, CPF ou Usuário</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            disabled={isLoading}
            placeholder="E-mail, CPF ou Usuário"
          />
        </div>
        <div className={styles['form-control']}>
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
        <button type="submit" className={styles['login-button']} disabled={isLoading}>
          {isLoading ? "Entrando..." : "Entrar"}
        </button>
        <p className={styles['login-hint']}>
          Usuário Padrão: <strong>Admin</strong><br/>
          Senha Padrão: <strong>Admin123</strong>
        </p>
      </form>
    </div>
  );
};
