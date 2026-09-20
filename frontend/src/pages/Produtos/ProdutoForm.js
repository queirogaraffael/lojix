import React, { useState, useEffect } from 'react';
import './ProdutoForm.css';

export const ProdutoForm = ({ produto, categorias, promocoes, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: '',
    preco: '',
    descricao: '',
    dataValidade: '',
    categoriaId: '',
    promocaoId: '',
    quantidadeEstoque: ''
  });

  const isEditMode = !!produto;

  useEffect(() => {
    if (produto) {
      setFormData({
        nome: produto.nome,
        preco: produto.precoBase,
        descricao: produto.descricao || '',
        dataValidade: produto.dataValidade || '',
        categoriaId: produto.categoriaId,
        promocaoId: produto.promocaoId || '',
        quantidadeEstoque: produto.quantidadeEstoque || 0
      });
    } else {
      setFormData({
        nome: '',
        preco: '',
        descricao: '',
        dataValidade: '',
        categoriaId: categorias.length > 0 ? categorias[0].id : '',
        promocaoId: '',
        quantidadeEstoque: ''
      });
    }
  }, [produto, categorias]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.categoriaId && !isEditMode) {
        alert("Selecione uma categoria.");
        return;
    }

    onSave({
      ...formData,
      preco: parseFloat(formData.preco),
      quantidadeEstoque: parseInt(formData.quantidadeEstoque || 0, 10),
      promocaoId: formData.promocaoId ? parseInt(formData.promocaoId) : null
    });
  };

  return (
    <form onSubmit={handleSubmit} className="produto-form">
      <div className="form-control">
        <label htmlFor="nome">Nome do Produto</label>
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
          <label htmlFor="categoriaId">Categoria</label>
          <select
            id="categoriaId"
            name="categoriaId"
            value={formData.categoriaId}
            onChange={handleChange}
            required
            disabled={isEditMode}
            style={isEditMode ? { backgroundColor: '#e9ecef' } : {}}
          >
            <option value="" disabled>Selecione...</option>
            {categorias.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.nome}
              </option>
            ))}
          </select>
        </div>

        <div className="form-control">
          <label htmlFor="preco">Preço (R$)</label>
          <input
            type="number"
            id="preco"
            name="preco"
            value={formData.preco}
            onChange={handleChange}
            step="0.01"
            min="0.01"
            required
          />
        </div>

        <div className="form-control">
          <label htmlFor="quantidadeEstoque">Estoque</label>
          <input
            type="number"
            id="quantidadeEstoque"
            name="quantidadeEstoque"
            value={formData.quantidadeEstoque}
            onChange={handleChange}
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
          maxLength="255"
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
          <label htmlFor="promocaoId">Promoção (Opcional)</label>
          <select
            id="promocaoId"
            name="promocaoId"
            value={formData.promocaoId}
            onChange={handleChange}
          >
            <option value="">Sem Promoção</option>
            {promocoes.map((promo) => (
              <option key={promo.id} value={promo.id}>
                {promo.nome} ({(promo.taxaDeDesconto * 100).toFixed(0)}%)
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