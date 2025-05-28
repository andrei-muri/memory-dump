import React, { createContext, useContext, useState, useEffect } from 'react';
import { PostService } from '../service/PostService';

interface PostMetadata {
  id: string;
  author: string;
  title: string;
  created: string;
  score: number;
  tags: string[];
}

interface PostsContextType {
  posts: PostMetadata[];
  fetchPosts: (id?: string, sort?: string, tags?: string[]) => Promise<void>;
}

const PostsContext = createContext<PostsContextType | undefined>(undefined);

export const PostsProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [posts, setPosts] = useState<PostMetadata[]>([]);

  const fetchPosts = async (id?: string, sort?: string, tags?: string[]) => {
    console.log("fetch posts");
    const data = await PostService.getAllPosts(id, sort, tags);
    setPosts(data);
  };

  useEffect(() => {
    fetchPosts();
  }, []);

  return (
    <PostsContext.Provider value={{ posts, fetchPosts }}>
      {children}
    </PostsContext.Provider>
  );
};

export const usePosts = () => {
  const context = useContext(PostsContext);
  if (!context) throw new Error('usePosts must be used within a PostsProvider');
  return context;
};