import React, { useState, useEffect } from 'react';

export const ClienteForm = ({ cliente, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    nome: '',
    cpf: '',
    idade: '',
    dataCadastro: '',
  });

  useEffect(() => {
    if (cliente) {
      setFormData({
        nome: cliente.nome,
        cpf: cliente.cpf,
        idade: cliente.idade.toString(),
        dataCadastro: cliente.dataCadastro,
      });
    } else {
      setFormData({
        nome: '',
        cpf: '',
        idade: '',
        dataCadastro: '',
      });
    }
  }, [cliente]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave({
      ...formData,
      idade: parseInt(formData.idade, 10),
    });
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
          <label htmlFor="cpf">CPF / Identidade</label>
          <input
            type="text"
            id="cpf"
            name="cpf"
            value={formData.cpf}
            onChange={handleChange}
            required
          />
        </div>
        <div className="form-control">
          <label htmlFor="idade">Idade</label>
          <input
            type="number"
            id="idade"
            name="idade"
            value={formData.idade}
            onChange={handleChange}
            min="0"
            required
          />
        </div>
      </div>

      <div className="form-control">
        <label htmlFor="dataCadastro">Data de Cadastro</label>
        <input
          type="date"
          id="dataCadastro"
          name="dataCadastro"
          value={formData.dataCadastro}
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