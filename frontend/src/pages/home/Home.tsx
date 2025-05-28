import React, { useEffect } from 'react';
import { usePosts } from '../../contexts/PostContext';
import Header from '../../components/header/Header';
import Modal from '../../components/modal/Modal';
import styles from './Home.module.css';
import { useNavigate } from 'react-router';
import { useUser } from '../../contexts/UserContext';

import { ProfileService } from '../../service/ProfileService';

const Home: React.FC = () => {
  const { posts, fetchPosts } = usePosts();
  const navigate = useNavigate();
  const { isLoggedIn, hasProfile, user } = useUser();

  useEffect(() => {
    if (posts.length === 0) {
      console.log("Called fetch from home");
      fetchPosts();
    }
  }, []);

  return (
    <div className={styles.homeWrapper}>
      <Header />
      {isLoggedIn && hasProfile && (
        <div className={styles.profileSection} onClick={() => navigate(`/profile/${user?.username}`)}>
          <img
            src={ProfileService.getProfilePictureUrl(user?.username) || '/default-profile.png'} // Fallback to default image
            alt={`${user?.username}'s profile`}
            className={styles.profilePicture}
          />
          <span className={styles.profileName}>{user?.username}</span>
        </div>
      )}
      <div className={styles.homeContainer}>
        
        <h1 className={styles.title}>Memory Dump</h1>
        <div className={styles.postList}>
          {posts.map((post) => (
            <div key={post.id} className={styles.postItem} >
              <span className={styles.score}>{post.score} votes</span>
              <a onClick={() => navigate(`/post/${post.id}`)} className={styles.postTitle}>
                {post.title}
              </a>
              <span className={styles.meta}>
                {post.tags.map((tag) => (
                  <span key={tag} className={styles.tag}>{tag}</span>
                ))}
                <span className={styles.author}>
                  <span onClick={() => navigate(`/profile/${post.author}`)} className={styles.username}>{post.author}</span> {new Date(post.created).toLocaleString()}
                </span>
              </span>
            </div>
          ))}
        </div>
      </div>
      <Modal />
    </div>
  );
};

export default Home;