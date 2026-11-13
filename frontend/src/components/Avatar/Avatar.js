import React from 'react';
import styles from './Avatar.module.css';

const Avatar = ({ name, fotoUrl, size = 'medium' }) => {
  const getInitials = (name) => {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  };

  return (
    <div className={`${styles.avatar} ${styles[`avatar-${size}`] || ''}`}>
      {fotoUrl ? (
        <img src={fotoUrl} alt={`Avatar de ${name}`} className={styles['avatar-img']} />
      ) : (
        <div className={styles['avatar-initials']}>
          {getInitials(name)}
        </div>
      )}
    </div>
  );
};

export default Avatar;
