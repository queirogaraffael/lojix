import React, { useState, useEffect } from 'react';
import styles from './CategoriaForm.module.css';

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
    <form onSubmit={handleSubmit} className={styles['categoria-form']}>
      <div className={styles['form-control']}>
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

      <div className={styles['form-actions']}>
        <button type="button" className={styles['btn-cancel']} onClick={onCancel}>
          Cancelar
        </button>
        <button type="submit" className={styles['btn-save']}>
          Salvar
        </button>
      </div>
    </form>
  );
};
