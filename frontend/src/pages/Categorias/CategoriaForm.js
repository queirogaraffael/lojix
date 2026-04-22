import React, { useState, useEffect } from 'react';

export const CategoriaForm = ({ categoria, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: ''
  });

  useEffect(() => {
    if (categoria) {
      setFormData({
        nome: categoria.nome
      });
    } else {
      setFormData({
        nome: ''
      });
    }
  }, [categoria]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="produto-form">
      <div className="form-control">
        <label htmlFor="nome">Nome da Categoria</label>
        <input
          type="text"
          id="nome"
          name="nome"
          value={formData.nome}
          onChange={handleChange}
          required
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