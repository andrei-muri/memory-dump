import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Header from '../../components/header/Header';
import Modal from '../../components/modal/Modal';
import { PostService } from '../../service/PostService';
import { ProfileService } from '../../service/ProfileService';
import VoteButton from '../../components/vote/VoteButton';
import styles from './Post.module.css';
import { useUser } from '../../contexts/UserContext';

interface ProgrammingLanguagePercentage {
  language: string;
  percentage: string;
}

interface Post {
  id: string;
  author: string;
  title: string;
  content: string;
  created: string;
  tags: string[];
  programmingLanguagePercentages: ProgrammingLanguagePercentage[];
  score: number;
}

const Post: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [post, setPost] = useState<Post | null>(null);
  const navigate = useNavigate();
  const { user } = useUser();

  useEffect(() => {
    const fetchPost = async () => {
      if (id) {
        try {
          const data = await PostService.getPostById(id);
          setPost(data);
        } catch (error) {
          console.error('Failed to fetch post:', error);
        }
      }
    };
    fetchPost();
  }, [id]);

  if (!post) return <div className={styles.loading}>Loading...</div>;

  const handleScoreUpdate = (newScore: number) => {
    setPost((prev) => prev ? { ...prev, score: newScore } : null);
  };

  // const handleVote = async (vote: "UPVOTE" | "DOWNVOTE") => {
  //   const newScore = await PostService.vote(vote, user ? user?.username : "", post.id);
  //   setPost((prev) => prev ? { ...prev, score: newScore } : null);
  // }

  return (
    <div className={styles.postWrapper}>
      <Header />
      <div className={styles.postContainer}>
        <div className={styles.postContent}>
          <div className={styles.postHeader}>
            <img
              src={ProfileService.getProfilePictureUrl(post.author)}
              alt={`${post.author}'s profile`}
              className={styles.profilePicture}
              onClick={() => navigate(`/profile/${post.author}`)}
            />
            <div className={styles.headerDetails}>
              <h1 className={styles.title}>{post.title}</h1>
              <div className={styles.meta}>
                <span className={styles.author} onClick={() => navigate(`/profile/${post.author}`)}>{post.author}</span>
                <span className={styles.created}>{new Date(post.created).toLocaleString()}</span>
              </div>
              <div className={styles.tags}>
                {post.tags.map((tag) => (
                  <span key={tag} className={styles.tag}>{tag}</span>
                ))}
              </div>
            </div>
                <VoteButton postId={post.id} initialScore={post.score} onScoreUpdate={handleScoreUpdate} username={user? user.username : ""} author={post.author}/>
          </div>
          <div className={styles.languageBar}>
            {post.programmingLanguagePercentages.map((lang, index) => (
              <div
                key={lang.language}
                className={styles.languageSegment}
                style={{
                  width: lang.percentage,
                  backgroundColor: getLanguageColor(lang.language, index),
                }}
              />
            ))}
          </div>
          <div className={styles.languageList}>
            {post.programmingLanguagePercentages.map((lang, index) => (
              <div key={lang.language} className={styles.languageItem}>
                <span
                  className={styles.languageColor}
                  style={{ backgroundColor: getLanguageColor(lang.language, index) }}
                />
                <span>{lang.language} {lang.percentage}</span>
              </div>
            ))}
          </div>
          <div className={styles.content}>
            <div dangerouslySetInnerHTML={{ __html: post.content }} />
          </div>
        </div>
      </div>
      <Modal />
    </div>
  );
};

// Utility function to assign colors to languages
const getLanguageColor = (language: string, index: number): string => {
  const colors = [
    '#f1e05a', // JavaScript
    '#701516', // Java
    '#5e5086', // Haskell
    '#ff6200', // Prolog
    '#89e051', // Spring Boot (custom)
  ];
  const languageColors: { [key: string]: string } = {
    javascript: '#f1e05a',
    java: '#701516',
    haskell: '#5e5086',
    prolog: '#ff6200',
    'spring-boot': '#89e051',
  };
  return languageColors[language.toLowerCase()] || colors[index % colors.length] || '#cccccc';
};

export default Post;