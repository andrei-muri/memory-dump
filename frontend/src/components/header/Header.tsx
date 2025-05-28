import React from 'react';
import { useModal } from '../../contexts/ModalContext';
import ThemeSwitcher from '../theme/ThemeSwitcher';
import styles from './Header.module.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../contexts/UserContext';

const Header: React.FC = () => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const { logout, isLoggedIn, hasProfile } = useUser();

  return (
    <header className={styles.header}>
      <div className={styles.logo} onClick={() => navigate(`/`)}>Memory Dump</div>
      <div className={styles.searchBar}>
        <input type="text" placeholder="Search..." className={styles.searchInput} />
      </div>
      <div className={styles.themeSwitcher}>
        <ThemeSwitcher />
      </div>
      <div className={styles.authButtons}>
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/chat")}>Chat</button>}
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/friendships")}>Friends</button>}
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/create-post")}>Create</button>}
        { isLoggedIn && !hasProfile &&
          <button className={styles.setProfileButton} onClick={() => navigate("/create-profile")}>Set profile</button>}
        { isLoggedIn &&
          <button className={styles.loginButton} onClick={() => logout()}>Logout</button>}
        { !isLoggedIn &&
          <button className={styles.loginButton} onClick={() => openModal('login')}>Login</button>}
        { !isLoggedIn &&
          <button className={styles.signupButton} onClick={() => openModal('signup')}>Signup</button>}
      </div>
    </header>
  );
};

export default Header;