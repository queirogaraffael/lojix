import React, { useState } from 'react';
import styles from './ClienteForm.module.css';

export const ClienteForm = ({ onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    cpf: '',
    dataNascimento: ''
  });

  const formatarCPFInput = (valor) => {
    const numeros = valor.replace(/\D/g, '').slice(0, 11);
    if (numeros.length <= 3) return numeros;
    if (numeros.length <= 6) return `${numeros.slice(0, 3)}.${numeros.slice(3)}`;
    if (numeros.length <= 9) return `${numeros.slice(0, 3)}.${numeros.slice(3, 6)}.${numeros.slice(6)}`;
    return `${numeros.slice(0, 3)}.${numeros.slice(3, 6)}.${numeros.slice(6, 9)}-${numeros.slice(9, 11)}`;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'cpf') {
      setFormData((prev) => ({ ...prev, cpf: formatarCPFInput(value) }));
      return;
    }
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const cpfLimpo = formData.cpf.replace(/\D/g, '');
    
    const username = `cli_${cpfLimpo || Date.now()}`;
    const password = `Lojix@${Math.random().toString(36).slice(-6)}`;

    onSave({
      name: formData.name.trim(),
      email: formData.email.trim(),
      cpf: cpfLimpo,
      username,
      password,
      dataNascimento: formData.dataNascimento
    });
  };

  return (
    <form onSubmit={handleSubmit} className={styles['cliente-form']}>
      <div className={styles['form-control']}>
        <label htmlFor="name">Nome Completo</label>
        <input
          type="text"
          id="name"
          name="name"
          placeholder="Ex: Maria da Silva"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>

      <div className={styles['form-group']}>
        <div className={styles['form-control']}>
          <label htmlFor="cpf">CPF</label>
          <input
            type="text"
            id="cpf"
            name="cpf"
            placeholder="000.000.000-00"
            value={formData.cpf}
            onChange={handleChange}
            maxLength="14"
            required
          />
        </div>

        <div className={styles['form-control']}>
          <label htmlFor="dataNascimento">Data de Nascimento</label>
          <input
            type="date"
            id="dataNascimento"
            name="dataNascimento"
            value={formData.dataNascimento}
            onChange={handleChange}
            required
            max={new Date(new Date().setFullYear(new Date().getFullYear() - 18)).toISOString().split("T")[0]}
          />
        </div>
      </div>

      <div className={styles['form-control']}>
        <label htmlFor="email">E-mail (para avisos e promoções)</label>
        <input
          type="email"
          id="email"
          name="email"
          placeholder="cliente@exemplo.com"
          value={formData.email}
          onChange={handleChange}
          required
        />
      </div>

      <div className={styles['form-actions']}>
        <button type="button" className={styles['btn-cancel']} onClick={onCancel}>
          Cancelar
        </button>
        <button type="submit" className={styles['btn-save']}>
          Salvar Cliente
        </button>
      </div>
    </form>
  );
};
