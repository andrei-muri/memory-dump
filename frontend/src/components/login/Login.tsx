import React, { useState } from 'react';
import styles from './Login.module.css';
import { useUser } from '../../contexts/UserContext';
import type { Credentials } from '../../dto/Credentials';
import { useNavigate } from 'react-router-dom';
import { useModal } from '../../contexts/ModalContext';
import { useTranslation } from 'react-i18next';

const Login: React.FC = () => {
  const [credentials, setCredentials] = useState<Credentials>({ username: "", password: "" });
  const { login } = useUser();
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { closeModal, openModal } = useModal();
  const { t } = useTranslation();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setCredentials((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const login_error = await login(credentials);
    if (login_error !== null) {
      setError(t('login.error', { error: login_error }));
    } else {
      closeModal();
      openModal('login_success');
      navigate("/");
    }
  };

  return (
    <div className={styles.loginContainer}>
      <div className={styles.formHeader}>
        <h2>{t('login.title')}</h2>
      </div>
      <form className={styles.form} onSubmit={handleSubmit}>
        <div className={styles.formGroup}>
          <label htmlFor="username">{t('login.username')}</label>
          <input
            type="text"
            id="username"
            name="username"
            placeholder={t('login.usernamePlaceholder')}
            className={styles.input}
            value={credentials.username}
            onChange={handleInputChange}
          />
        </div>
        <div className={styles.formGroup}>
          <label htmlFor="password">{t('login.password')}</label>
          <input
            type="password"
            id="password"
            name="password"
            placeholder={t('login.passwordPlaceholder')}
            className={styles.input}
            value={credentials.password}
            onChange={handleInputChange}
          />
        </div>
        {error && <p className={styles.error}>{error}</p>}
        <button type="submit" className={styles.submitButton}>
          {t('login.submit')}
        </button>
        <div className={styles.links}>
          <a href="/forgot-password" className={styles.link}>
            {t('login.forgotPassword')}
          </a>
        </div>
      </form>
    </div>
  );
};

export default Login;