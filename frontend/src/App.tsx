import { BrowserRouter, Route, Routes } from 'react-router-dom';
import './App.css'
import { ThemeProvider } from './contexts/ThemeContext';
import './styles/global.css'
import ForgotPassword from './pages/forgot/ForgotPassword';
import ResetPassword from './pages/reset/ResetPassword';
import Home from './pages/home/Home';
import { PostsProvider } from './contexts/PostContext';
import Post from './pages/post/Post';
import { ModalProvider } from './contexts/ModalContext';
import Profile from './pages/profile/Profile';
import { UserProvider } from './contexts/UserContext';
import { TagProvider } from './contexts/TagContext';
import CreateProfile from './pages/create-profile/CreateProfile';
import CreatePost from './pages/create-post/CreatePost';
import Friendships from './pages/friendships/Friendships';
import Chat from './pages/chat/Chat';

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <TagProvider>
        <ModalProvider>
          <ThemeProvider>
            <PostsProvider>
              <UserProvider>
                <Routes>
                  <Route path="/" element={<Home />} />
                  <Route path="/forgot" element={<ForgotPassword />} />
                  <Route path="/reset" element={<ResetPassword />} />
                  <Route path="/post/:id" element={<Post />} />
                  <Route path="/profile/:username" element={<Profile />} />
                  <Route path="/create-profile" element={<CreateProfile />} />
                  <Route path="/create-post" element={<CreatePost />} />
                  <Route path="/friendships" element={<Friendships />} />
                  <Route path="/chat" element={<Chat />} />
                </Routes>
              </UserProvider>
            </PostsProvider>
          </ThemeProvider>
        </ModalProvider>
      </TagProvider>
    </BrowserRouter>

  );
};

export default App;
