import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  return config;
}, (error) => {
  return Promise.reject(error);
});

export const atualizarFoto = (arquivo) => {
  const formData = new FormData();
  formData.append('foto', arquivo);
  return api.patch('/usuarios/foto', formData);
};

export default api;