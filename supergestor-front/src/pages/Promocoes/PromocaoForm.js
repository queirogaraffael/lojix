import React, { useState } from 'react';

export const PromocaoForm = ({ onSave, onCancel }) => {
  const [nome, setNome] = useState('');
  const [taxa, setTaxa] = useState(0);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      nome: nome,
      taxaDeDesconto: parseFloat(taxa),
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
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          required
        />
      </div>

      <div className="form-control">
        <label htmlFor="taxa">Taxa de Desconto (Ex: 0.1 para 10%)</label>
        <input
          type="number"
          id="taxa"
          name="taxa"
          value={taxa}
          onChange={(e) => setTaxa(e.target.value)}
          step="0.01"
          min="0"
          max="1"
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