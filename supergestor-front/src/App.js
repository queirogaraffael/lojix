import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Login } from './pages/Login/Login';
import { Layout } from './components/Layout/Layout';
import { ProtectedRoute } from './ProtectedRoute';
import { Dashboard } from './pages/Dashboard/Dashboard';
import { Produtos } from './pages/Produtos/Produtos';
import { Funcionarios } from './pages/Funcionarios/Funcionarios';
import { Promocoes } from './pages/Promocoes/Promocoes';
import './App.css';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <Layout>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/produtos" element={<Produtos />} />
                <Route path="/funcionarios" element={<Funcionarios />} />
                <Route path="/promocoes" element={<Promocoes />} />
              </Routes>
            </Layout>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;