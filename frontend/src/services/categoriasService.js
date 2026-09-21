import api from './api';

export const listarCategorias = (page = 0, size = 50) => {
  return api.get(`/categorias?page=${page}&size=${size}`);
};

export const criarCategoria = (categoria) => {
  return api.post('/categorias', categoria);
};

export const atualizarCategoria = (id, categoria) => {
  return api.put(`/categorias/${id}`, categoria);
};
