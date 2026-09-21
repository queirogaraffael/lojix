import api from './api';

export const listarFuncionarios = (page = 0, size = 20) => {
  return api.get(`/funcionarios?page=${page}&size=${size}`);
};

export const criarFuncionario = (funcionario) => {
  return api.post('/funcionarios', funcionario);
};

export const atualizarFuncionario = (id, funcionario) => {
  return api.put(`/funcionarios/${id}`, funcionario);
};

export const desligarFuncionario = (id) => {
  return api.patch(`/funcionarios/${id}/desligar`);
};
