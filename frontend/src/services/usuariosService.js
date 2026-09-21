import api from './api';

export const atualizarFoto = (arquivo) => {
  const formData = new FormData();
  formData.append('foto', arquivo);
  return api.patch('/usuarios/foto', formData);
};
