import React, { useState, useRef } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { atualizarFoto } from '../../services/api';
import Avatar from '../../components/Avatar/Avatar';
import './Perfil.css';

export const Perfil = () => {
  const { user, updateUserContext } = useAuth();
  const [loading, setLoading] = useState(false);
  const fileInputRef = useRef(null);

  const handleFotoChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      setLoading(true);
      const response = await atualizarFoto(file);
      const newFotoUrl = response.data.fotoUrl;
      
      // Update local context
      updateUserContext({ ...user, fotoUrl: newFotoUrl });
      alert('Foto atualizada com sucesso!');
    } catch (error) {
      console.error(error);
      alert('Erro ao atualizar foto.');
    } finally {
      setLoading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  if (!user) return <p>Carregando...</p>;

  return (
    <div className="perfil-container">
      <div className="perfil-card">
        <h2>Meu Perfil</h2>
        
        <div className="perfil-avatar-section">
          <Avatar name={user.name || user.username} fotoUrl={user.fotoUrl} size="large" />
          
          <div className="perfil-avatar-actions">
            <button 
              className="btn-mudar-foto" 
              onClick={() => fileInputRef.current?.click()}
              disabled={loading}
            >
              {loading ? 'Salvando...' : 'Mudar Foto'}
            </button>
            <input 
              type="file" 
              ref={fileInputRef}
              style={{ display: 'none' }}
              accept="image/png, image/jpeg, image/webp"
              onChange={handleFotoChange}
            />
          </div>
        </div>

        <div className="perfil-info">
          <div className="form-group">
            <label>Nome / Username</label>
            <input type="text" value={user.name || user.username} readOnly disabled />
          </div>
          <div className="form-group">
            <label>Papéis (Roles)</label>
            <input type="text" value={user.roles?.join(', ')} readOnly disabled />
          </div>
        </div>
      </div>
    </div>
  );
};
