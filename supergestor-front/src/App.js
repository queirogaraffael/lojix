import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { Login } from './pages/Login/Login';
import './App.css';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<div>App Vazia (Protegida)</div>} />
    </Routes>
  );
}

export default App;