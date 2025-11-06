import React, { useState, useEffect } from 'react';

export const FuncionarioForm = ({ funcionario, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    cpf: '',
    foto: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
    password: '',
  });

  const isEditMode = !!funcionario;

  useEffect(() => {
    if (funcionario) {
      setFormData({
        ...funcionario,
        password: '',
      });
    } else {
      setFormData({
        nome: '',
        email: '',
        cpf: '',
        foto: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
        password: '',
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

  const handleFotoChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData((prev) => ({
          ...prev,
          foto: reader.result,
        }));
      };
      reader.readAsDataURL(file);
    }
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
        <label htmlFor="nome">Nome Completo</label>
        <input
          type="text"
          id="nome"
          name="nome"
          value={formData.nome}
          onChange={handleChange}
          required
        />
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
            required
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
            required
          />
        </div>
      </div>

      <div className="form-control">
        <label htmlFor="password">
          Senha {isEditMode ? '(Deixe em branco para não alterar)' : ''}
        </label>
        <input
          type="password"
          id="password"
          name="password"
          value={formData.password}
          onChange={handleChange}
          required={!isEditMode}
        />
      </div>

      <div className="form-control">
        <label htmlFor="foto">Foto (Upload)</label>
        <input
          type="file"
          id="foto"
          name="foto"
          accept="image/*"
          onChange={handleFotoChange}
        />
        <img
          src={formData.foto}
          alt="Preview"
          className="funcionario-foto-preview"
        />
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