import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Login } from './pages/Login/Login';
import { Layout } from './components/Layout/Layout';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Dashboard } from './pages/Dashboard/Dashboard';
import { Produtos } from './pages/Produtos/Produtos';
import { Funcionarios } from './pages/Funcionarios/Funcionarios';
import { Promocoes } from './pages/Promocoes/Promocoes';
import { Clientes } from './pages/Clientes/Clientes';
import { Categorias } from './pages/Categorias/Categorias';
import { Perfil } from './pages/Perfil/Perfil';
import { Unauthorized } from './pages/Unauthorized/Unauthorized';
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
                <Route path="/produtos" element={<ProtectedRoute allowedRoles={['ADMIN', 'ESTOQUISTA']}><Produtos /></ProtectedRoute>} />
                <Route path="/funcionarios" element={<ProtectedRoute allowedRoles={['ADMIN']}><Funcionarios /></ProtectedRoute>} />
                <Route path="/promocoes" element={<ProtectedRoute allowedRoles={['ADMIN', 'ESTOQUISTA']}><Promocoes /></ProtectedRoute>} />
                <Route path="/clientes" element={<ProtectedRoute allowedRoles={['ADMIN', 'ATENDENTE']}><Clientes /></ProtectedRoute>} />
                <Route path="/categorias" element={<ProtectedRoute allowedRoles={['ADMIN', 'ESTOQUISTA']}><Categorias /></ProtectedRoute>} />
                <Route path="/perfil" element={<Perfil />} />
                <Route path="/unauthorized" element={<Unauthorized />} />
              </Routes>
            </Layout>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;