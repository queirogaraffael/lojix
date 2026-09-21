import api from './api';

export const listarClientes = (page = 0, size = 20) => {
  return api.get(`/clientes?page=${page}&size=${size}`);
};

export const criarCliente = (cliente) => {
  return api.post('/clientes', cliente);
};
