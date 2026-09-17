import React from 'react';
import './Avatar.css';

const Avatar = ({ name, fotoUrl, size = 'medium' }) => {
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  };

  return (
    <div className={`avatar avatar-${size}`}>
      {fotoUrl ? (
        <img src={fotoUrl} alt={`Avatar de ${name}`} className="avatar-img" />
      ) : (
        <div className="avatar-initials">
          {getInitials(name)}
        </div>
      )}
    </div>
  );
};

export default Avatar;
