import React from 'react';
import { useModal } from '../../contexts/ModalContext';
import Login from '../login/Login';
import Signup from '../signup/Signup';
import styles from './Modal.module.css';
import Welcome from '../welcome/Welcome';
import LoginSuccess from '../login/LoginSuccess';

const Modal: React.FC = () => {
  const { isModalOpen, modalType, closeModal } = useModal();

  if (!isModalOpen) return null;

  return (
    <div className={styles.modalOverlay} onClick={closeModal}>
      <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
        <button className={styles.closeButton} onClick={closeModal}>
          ×
        </button>
        {modalType === 'login' && <Login />}
        {modalType === 'signup' && <Signup />}
        {modalType === 'welcome' && <Welcome />}
        {modalType === 'login_success' && <LoginSuccess />}
      </div>
    </div>
  );
};

export default Modal;