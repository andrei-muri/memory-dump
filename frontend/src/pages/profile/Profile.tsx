import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useUser } from '../../contexts/UserContext';
import { FriendshipService } from '../../service/FriendshipService';
import { ProfileService } from '../../service/ProfileService';
import Header from '../../components/header/Header';
import Modal from '../../components/modal/Modal';
import styles from './Profile.module.css';

interface PostMetadata {
  id: string;
  author: string;
  title: string;
  created: string;
  score: number;
  tags: string[];
}

interface Profile {
  fullName: string;
  description: string;
  preferredTags: string[];
  postsMetadata: PostMetadata[];
}

const Profile: React.FC = () => {
  const { username } = useParams<{ username: string }>();
  const { user, friends, setFriends } = useUser();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [friendshipStatus, setFriendshipStatus] = useState<string>('NOT_FRIENDS');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProfileAndStatus = async () => {
      try {
        if (username) {
          const data = await ProfileService.getProfile(username);
          setProfile(data);
          if (user?.username && username !== user.username) {
            const status = await FriendshipService.getFriendshipStatus(user.username, username);
            setFriendshipStatus(status);
          }
        }
      } catch (err) {
        setError('Failed to fetch profile.');
      }
    };
    fetchProfileAndStatus();
  }, [username, user?.username]);

  const handleFriendshipRequest = async () => {
    if (friendshipStatus === 'NOT_FRIENDS') {
      try {
        await FriendshipService.request({ from: user!.username, to: username! });
        setFriendshipStatus('PENDING');
      } catch (err) {
        setError('Failed to send friendship request.');
      }
    }
  };

  const handleUnfriend = async () => {
    if (friendshipStatus === 'FRIENDS') {
      try {
        await FriendshipService.unfriend({ from: user!.username, to: username! });
        setFriendshipStatus('NOT_FRIENDS');
        setFriends(friends.filter(friend => friend.username !== username));
      } catch (err) {
        setError('Failed to unfriend.');
      }
    }
  };

  if (!profile) return <div className={styles.loading}>Loading...</div>;

  return (
    <div className={styles.profileWrapper}>
      <Header />
      <div className={styles.profileContainer}>
        <div className={styles.mainContent}>
          <div className={styles.profileHeader}>
            <img
              src={ProfileService.getProfilePictureUrl(username!)}
              alt={`${profile.fullName}'s profile`}
              className={styles.profilePicture}
            />
            <div className={styles.profileDetails}>
              <h1 className={styles.fullName}>{profile.fullName}</h1>
              {error && <p className={styles.error}>{error}</p>}
              {user?.username && username !== user.username && (
                <div className={styles.friendshipAction}>
                  {friendshipStatus === 'NOT_FRIENDS' && (
                    <button onClick={handleFriendshipRequest} className={styles.friendshipButton}>
                      Request Friendship
                    </button>
                  )}
                  {friendshipStatus === 'PENDING' && <span className={styles.status}>Pending Request</span>}
                  {friendshipStatus === 'FRIENDS' && (
                    <>
                      <span className={styles.status}>Friends</span>
                      <button onClick={handleUnfriend} className={styles.unfriendButton}>
                        Unfriend
                      </button>
                    </>
                  )}
                </div>
              )}
              <p className={styles.description}>{profile.description || 'No description available.'}</p>
              <div className={styles.tags}>
                <h3>Preferred Tags:</h3>
                {profile.preferredTags.length > 0 ? (
                  <div className={styles.tagList}>
                    {profile.preferredTags.map((tag) => (
                      <span key={tag} className={styles.tag}>{tag}</span>
                    ))}
                  </div>
                ) : (
                  <p>No preferred tags.</p>
                )}
              </div>
            </div>
          </div>
        </div>
        <div className={styles.sidebar}>
          <h2 className={styles.sectionTitle}>Recent Posts</h2>
          {profile.postsMetadata.length > 0 ? (
            <ul className={styles.postsList}>
              {profile.postsMetadata.map((post) => (
                <li key={post.id} className={styles.postItem}>
                  <a onClick={() => navigate(`/post/${post.id}`)} className={styles.postTitle}>
                    {post.title}
                  </a>
                  <span className={styles.meta}>
                    <span className={styles.score}>{post.score} votes</span>
                    <span className={styles.created}>{new Date(post.created).toLocaleString()}</span>
                  </span>
                </li>
              ))}
            </ul>
          ) : (
            <p className={styles.noPosts}>No posts available.</p>
          )}
        </div>
      </div>
      <Modal />
    </div>
  );
};

export default Profile;