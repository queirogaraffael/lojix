import React, { useState } from 'react';

export const PromocaoForm = ({ onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: '',
    taxaDeDesconto: '',
    inicio: '',
    fim: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    const taxaDecimal = parseFloat(formData.taxaDeDesconto) / 100;

    onSave({
      ...formData,
      taxaDeDesconto: taxaDecimal
    });
  };

  return (
    <form onSubmit={handleSubmit} className="produto-form">
      <div className="form-control">
        <label htmlFor="nome">Nome da Promoção</label>
        <input
          type="text"
          id="nome"
          name="nome"
          value={formData.nome}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-control">
        <label htmlFor="taxaDeDesconto">Taxa de Desconto (%)</label>
        <input
          type="number"
          id="taxaDeDesconto"
          name="taxaDeDesconto"
          value={formData.taxaDeDesconto}
          onChange={handleChange}
          placeholder="Ex: 10 para 10%"
          min="0"
          max="100"
          required
        />
      </div>

      <div className="form-group">
        <div className="form-control">
          <label htmlFor="inicio">Data Início</label>
          <input
            type="date"
            id="inicio"
            name="inicio"
            value={formData.inicio}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="fim">Data Fim</label>
          <input
            type="date"
            id="fim"
            name="fim"
            value={formData.fim}
            onChange={handleChange}
            required
          />
        </div>
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