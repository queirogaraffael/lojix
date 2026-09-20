import React, { useState, useEffect } from 'react';

export const FuncionarioForm = ({ funcionario, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    username: '',
    cpf: '',
    cargo: '',
    role: 'ATENDENTE',
    salario: '',
    password: '',
    dataNascimento: ''
  });

  const isEditMode = !!funcionario;

  useEffect(() => {
    if (funcionario) {
      setFormData({
        name: funcionario.usuarioResponseDTO.name,
        email: funcionario.usuarioResponseDTO.email,
        username: funcionario.usuarioResponseDTO.username,
        cpf: funcionario.usuarioResponseDTO.cpf,
        cargo: funcionario.cargo,
        role: funcionario.usuarioResponseDTO.role || 'ATENDENTE',
        salario: funcionario.salario,
        password: '',
        dataNascimento: funcionario.usuarioResponseDTO.dataNascimento ? funcionario.usuarioResponseDTO.dataNascimento.split('-').reverse().join('-') : ''
      });
    } else {
      setFormData({
        name: '',
        email: '',
        username: '',
        cpf: '',
        cargo: '',
        role: 'ATENDENTE',
        salario: '',
        password: '',
        dataNascimento: ''
      });
    }
  }, [funcionario]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!isEditMode && !formData.password) {
      alert('A senha é obrigatória para criar um novo funcionário.');
      return;
    }
    onSave(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="produto-form">
      <div className="form-control">
        <label htmlFor="name">Nome Completo</label>
        <input
          type="text"
          id="name"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required={!isEditMode}
          disabled={isEditMode}
        />
      </div>

      <div className="form-group">
         <div className="form-control">
          <label htmlFor="username">Usuário (Login)</label>
          <input
            type="text"
            id="username"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required={!isEditMode}
            disabled={isEditMode}
          />
        </div>
        <div className="form-control">
          <label htmlFor="cpf">CPF (somente números)</label>
          <input
            type="text"
            id="cpf"
            name="cpf"
            value={formData.cpf}
            onChange={handleChange}
            required={!isEditMode}
            disabled={isEditMode}
            maxLength="11"
          />
        </div>
      </div>

      <div className="form-group">
        <div className="form-control">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required={!isEditMode}
            disabled={isEditMode}
          />
        </div>
        <div className="form-control">
          <label htmlFor="dataNascimento">Data de Nasc.</label>
          <input
            type="date"
            id="dataNascimento"
            name="dataNascimento"
            value={formData.dataNascimento}
            onChange={handleChange}
            required={!isEditMode}
            disabled={isEditMode}
            max={new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().split("T")[0]}
          />
        </div>
      </div>

      <div className="form-group">
         <div className="form-control">
          <label htmlFor="cargo">Cargo (Ex: Gerente Geral)</label>
          <input
            type="text"
            id="cargo"
            name="cargo"
            value={formData.cargo}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="role">Nível de Acesso no Sistema</label>
          <select
            id="role"
            name="role"
            value={formData.role}
            onChange={handleChange}
            required={!isEditMode}
            disabled={isEditMode}
          >
            <option value="ATENDENTE">Atendente (Acesso a Clientes)</option>
            <option value="ESTOQUISTA">Estoquista (Acesso a Produtos e Promoções)</option>
            <option value="ADMIN">Administrador (Acesso Total)</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <div className="form-control">
          <label htmlFor="salario">Salário</label>
          <input
            type="number"
            id="salario"
            name="salario"
            value={formData.salario}
            onChange={handleChange}
            step="0.01"
            required
          />
        </div>
        
        {!isEditMode && (
          <div className="form-control">
            <label htmlFor="password">Senha</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength="6"
            />
          </div>
        )}
      </div>

      <div className="form-actions">
        <button type="button" className="btn-cancel" onClick={onCancel}>
          Cancelar
        </button>
        <button type="submit" className="btn-save">
          Salvar
        </button>
      </div>
    </form>
  );
};