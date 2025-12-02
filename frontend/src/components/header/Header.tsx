import React from 'react';
import { useModal } from '../../contexts/ModalContext';
import ThemeSwitcher from '../theme/ThemeSwitcher';
import styles from './Header.module.css';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../contexts/UserContext';
import { useTranslation } from 'react-i18next';

const Header: React.FC = () => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const { logout, isLoggedIn, hasProfile } = useUser();
  const { t, i18n } = useTranslation();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'en' ? 'ro' : 'en';
    i18n.changeLanguage(newLang);
  };

  return (
    <header className={styles.header}>
      <div className={styles.logo} onClick={() => navigate(`/`)}>Memory Dump</div>
      <div className={styles.searchBar}>
        <input type="text" placeholder={t('header.searchPlaceholder')} className={styles.searchInput} />
      </div>
      <div className={styles.themeSwitcher}>
        <ThemeSwitcher />
      </div>
      <div className={styles.authButtons}>
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/chat")}>{t('header.chat')}</button>}
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/friendships")}>{t('header.friends')}</button>}
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => navigate("/create-post")}>{t('header.create')}</button>}
        {isLoggedIn && !hasProfile &&
          <button className={styles.setProfileButton} onClick={() => navigate("/create-profile")}>{t('header.setProfile')}</button>}
        {isLoggedIn &&
          <button className={styles.loginButton} onClick={() => logout()}>{t('header.logout')}</button>}
        {!isLoggedIn &&
          <button className={styles.loginButton} onClick={() => openModal('login')}>{t('header.login')}</button>}
        {!isLoggedIn &&
          <button className={styles.signupButton} onClick={() => openModal('signup')}>{t('header.signup')}</button>}
        <button className={styles.languageButton} onClick={toggleLanguage}>
          {i18n.language === 'en' ? 'RO' : 'EN'}
        </button>
      </div>
    </header>
  );
};

export default Header;