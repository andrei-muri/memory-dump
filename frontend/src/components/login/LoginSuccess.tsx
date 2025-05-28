import React from 'react';
import { useUser } from '../../contexts/UserContext';
import { useNavigate } from 'react-router-dom';
import styles from './LoginSuccess.module.css';

const LoginSuccess: React.FC = () => {
    const { hasProfile } = useUser();
    const navigate = useNavigate();

    return (
        <div className={styles.container}>
            {!hasProfile ? (
                <div className={styles.setupPrompt}>
                    <h2 className={styles.message}>Let's setup your profile!</h2>
                    <button
                        className={styles.setProfileButton}
                        onClick={() => navigate('/create-profile')}
                    >
                        Set Profile
                    </button>
                </div>
            ) : (
                <div className={styles.welcome}>
                    <h2 className={styles.message}>Welcome</h2>
                </div>
            )}
        </div>
    );
};

export default LoginSuccess;