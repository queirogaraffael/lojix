import api from './api';

export const listarPromocoes = (page = 0, size = 20) => {
  return api.get(`/promocoes?page=${page}&size=${size}`);
};

export const criarPromocao = (promocao) => {
  return api.post('/promocoes', promocao);
};

export const desativarPromocao = (id) => {
  return api.patch(`/promocoes/${id}/desativar`);
};

export const associarProduto = (promocaoId, produtoId) => {
  return api.patch(`/promocoes/${promocaoId}/associar/${produtoId}`);
};

export const removerAssociacaoProduto = (produtoId) => {
  return api.patch(`/promocoes/remover/${produtoId}`);
};
