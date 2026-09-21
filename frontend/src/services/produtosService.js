import api from './api';

export const listarProdutos = (page = 0, size = 20) => {
  return api.get(`/produtos?page=${page}&size=${size}`);
};

export const criarProduto = (categoriaId, produto) => {
  return api.post(`/produtos/categoria/${categoriaId}`, produto);
};

export const atualizarProduto = (id, produto) => {
  return api.put(`/produtos/${id}`, produto);
};

export const desativarProduto = (id) => {
  return api.patch(`/produtos/${id}/desativar`);
};
