import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../../contexts/UserContext';
import { useTags } from '../../contexts/TagContext';
import { PostService } from '../../service/PostService';
import styles from './CreatePost.module.css';
import Header from '../../components/header/Header';
import { usePosts } from '../../contexts/PostContext';

const CreatePost: React.FC = () => {
  const { user } = useUser();
  const { tags } = useTags();
  const navigate = useNavigate();
  const {fetchPosts} = usePosts();
  const [formData, setFormData] = useState({
    author: user?.username || 'default',
    title: '',
    content: '',
    tags: [] as string[],
  });
  const [error, setError] = useState<string | null>(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleTagChange = (tagName: string) => {
    setFormData((prev) => {
      const newTags = prev.tags.includes(tagName)
        ? prev.tags.filter((t) => t !== tagName)
        : [...prev.tags, tagName];
      return { ...prev, tags: newTags };
    });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    fetchPosts();
    try {
      await PostService.createPost(formData);
      navigate('/');
    } catch (error) {
      setError('Failed to create post. Please try again.');
    }
  };

  return (
    <div className={styles.createPostWrapper}>
      <Header />
      <div className={styles.createPostContainer}>
        <div className={styles.formHeader}>
          <h2>Create Post</h2>
        </div>
        <form className={styles.form} onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label htmlFor="title">Title</label>
            <input
              type="text"
              id="title"
              name="title"
              placeholder="Enter post title"
              className={styles.input}
              value={formData.title}
              onChange={handleInputChange}
              required
            />
          </div>
          <div className={styles.formGroup}>
            <label htmlFor="content">Content</label>
            <textarea
              id="content"
              name="content"
              placeholder="Enter post content"
              className={styles.textarea}
              value={formData.content}
              onChange={handleInputChange}
              required
            />
          </div>
          <div className={styles.formGroup}>
            <label>Tags</label>
            <div className={styles.tagList}>
              {tags.map((tag) => (
                <label key={tag.id} className={styles.tagLabel}>
                  <input
                    type="checkbox"
                    checked={formData.tags.includes(tag.name)}
                    onChange={() => handleTagChange(tag.name)}
                  />
                  <span className={styles.tagName}>{tag.name}</span>
                </label>
              ))}
            </div>
          </div>
          {error && <p className={styles.error}>{error}</p>}
          <button type="submit" className={styles.submitButton}>
            Create Post
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreatePost;