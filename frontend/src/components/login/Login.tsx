import React, { useState } from 'react';
import styles from './Login.module.css';
import { useUser } from '../../contexts/UserContext';
import type { Credentials } from '../../dto/Credentials';
import { useNavigate } from 'react-router-dom';
import { useModal } from '../../contexts/ModalContext';

const Login: React.FC = () => {
  const [credentials, setCredentials] = useState<Credentials>({ username: "", password: "" });
  const { login } = useUser();
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { closeModal, openModal } = useModal();

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
      setError(login_error);
    } else {
      closeModal();
      openModal('login_success');
      navigate("/");
    }
  };

  return (
    <div className={styles.loginContainer}>
      <div className={styles.formHeader}>
        <h2>Login</h2>
      </div>
      <form className={styles.form} onSubmit={handleSubmit}>
        <div className={styles.formGroup}>
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            name="username"
            placeholder="Enter username"
            className={styles.input}
            value={credentials.username}
            onChange={handleInputChange}
          />
        </div>
        <div className={styles.formGroup}>
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            name="password"
            placeholder="Enter password"
            className={styles.input}
            value={credentials.password}
            onChange={handleInputChange}
          />
        </div>
        {error && <p className={styles.error}>{error}</p>}
        <button type="submit" className={styles.submitButton}>
          Login
        </button>
        <div className={styles.links}>
          <a href="/forgot-password" className={styles.link}>
            Forgot Password?
          </a>
        </div>
      </form>
    </div>
  );
};

export default Login;