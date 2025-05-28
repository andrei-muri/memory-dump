import React from 'react';
import { Link } from 'react-router-dom';
import ThemeSwitcher from '../../components/theme/ThemeSwitcher';
import styles from './ResetPassword.module.css';

const ResetPassword: React.FC = () => {
  return (
    <div className={styles.resetPasswordWrapper}>
      <div className={styles.resetPasswordContainer}>
        <h1 className={styles.title}>Memory Dump</h1>
        <div className={styles.themeSwitcher}>
          <ThemeSwitcher />
        </div>
        <div className={styles.formContainer}>
          <div className={styles.formHeader}>
            <h2>Reset Password</h2>
          </div>
          <form className={styles.form}>
            <div className={styles.formGroup}>
              <label htmlFor="new-password">New Password</label>
              <input
                type="password"
                id="new-password"
                placeholder="Enter new password"
                className={styles.input}
              />
            </div>
            <div className={styles.formGroup}>
              <label htmlFor="confirm-password">Confirm Password</label>
              <input
                type="password"
                id="confirm-password"
                placeholder="Confirm new password"
                className={styles.input}
              />
            </div>
            <button type="submit" className={styles.submitButton}>
              Reset Password
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

export default ResetPassword;