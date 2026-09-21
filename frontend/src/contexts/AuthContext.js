import React, { createContext, useContext, useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import * as authService from "../services/authService";

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const carregarDadosArmazenados = async () => {
      const storedUser = localStorage.getItem("user");
      const storedToken = localStorage.getItem("token");

      if (storedUser && storedToken) {
        setUser(JSON.parse(storedUser));
      }
      setLoading(false);
    };

    carregarDadosArmazenados();
  }, []);

  const login = async (username, password) => {
    try {
      const response = await authService.login(username, password);
      const { token } = response.data;

      localStorage.setItem("token", token);

      const userResponse = await authService.getMe();
      const userData = userResponse.data;

      setUser(userData);
      localStorage.setItem("user", JSON.stringify(userData));

      navigate("/");
      return { success: true };
    } catch (error) {
      console.error("Erro no login:", error);
      localStorage.removeItem("token");
      localStorage.removeItem("user");

      return {
        success: false,
        message:
          error.response?.data?.message ||
          "Falha ao realizar login. Verifique suas credenciais.",
      };
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    navigate("/login");
  };

  const checkAuth = () => {
    return !!localStorage.getItem("token");
  };

  const updateUserContext = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem("user", JSON.stringify(updatedUser));
  };

  const value = { user, login, logout, checkAuth, loading, updateUserContext };

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  return useContext(AuthContext);
};
