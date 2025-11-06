import React, { useState, useEffect } from 'react';
import { mockPromocoes } from '../../mocks/db';
import './ProdutoForm.css';

export const ProdutoForm = ({ produto, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: '',
    tipo: '',
    precoAtual: 0,
    descricao: '',
    dataValidade: '',
    promocaoId: null,
  });

  useEffect(() => {
    if (produto) {
      setFormData({
        ...produto,
        precoAtual: produto.precoAtual.toString(),
        promocaoId: produto.promocaoId || 'null',
      });
    } else {
      setFormData({
        nome: '',
        tipo: 'Bebidas',
        precoAtual: '0',
        descricao: '',
        dataValidade: '',
        promocaoId: 'null',
      });
    }
  }, [produto]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const promocaoFinal = formData.promocaoId === 'null' ? null : formData.promocaoId;

    onSave({
      ...formData,
      precoAtual: parseFloat(formData.precoAtual),
      promocaoId: promocaoFinal,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="produto-form">
      <div className="form-control">
        <label htmlFor="nome">Nome</label>
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
          <label htmlFor="tipo">Tipo</label>
          <select
            id="tipo"
            name="tipo"
            value={formData.tipo}
            onChange={handleChange}
          >
            <option value="Bebidas">Bebidas</option>
            <option value="Limpeza">Limpeza</option>
            <option value="Padaria">Padaria</option>
            <option value="Outros">Outros</option>
          </select>
        </div>
        <div className="form-control">
          <label htmlFor="precoAtual">Preço Atual (R$)</label>
          <input
            type="number"
            id="precoAtual"
            name="precoAtual"
            value={formData.precoAtual}
            onChange={handleChange}
            step="0.01"
            min="0"
            required
          />
        </div>
      </div>

      <div className="form-control">
        <label htmlFor="descricao">Descrição</label>
        <textarea
          id="descricao"
          name="descricao"
          value={formData.descricao}
          onChange={handleChange}
          rows="3"
        ></textarea>
      </div>

      <div className="form-group">
        <div className="form-control">
          <label htmlFor="dataValidade">Data de Validade</label>
          <input
            type="date"
            id="dataValidade"
            name="dataValidade"
            value={formData.dataValidade}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="promocaoId">Promoção</label>
          <select
            id="promocaoId"
            name="promocaoId"
            value={formData.promocaoId || 'null'}
            onChange={handleChange}
          >
            <option value="null">Sem Promoção</option>
            {mockPromocoes.map((promo) => (
              <option key={promo.id} value={promo.id}>
                {promo.nome}
              </option>
            ))}
          </select>
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