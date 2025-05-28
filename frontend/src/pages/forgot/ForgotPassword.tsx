import React from 'react';
import { Link } from 'react-router-dom';
import ThemeSwitcher from '../../components/theme/ThemeSwitcher';
import styles from './ForgotPassword.module.css';

const ForgotPassword: React.FC = () => {
  return (
    <div className={styles.forgotPasswordWrapper}>
      <div className={styles.forgotPasswordContainer}>
        <h1 className={styles.title}>Memory Dump</h1>
        <div className={styles.themeSwitcher}>
          <ThemeSwitcher />
        </div>
        <div className={styles.formContainer}>
          <div className={styles.formHeader}>
            <h2>Forgot Password</h2>
          </div>
          <form className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                placeholder="Enter your email"
                className={styles.input}
              />
            </div>
            <button type="submit" className={styles.submitButton}>
              Send Reset Link
            </button>
            <div className={styles.links}>
              <Link to="/login" className={styles.link}>
                Back to Login
              </Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;