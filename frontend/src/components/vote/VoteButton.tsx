import React from 'react';
import { useUser } from '../../contexts/UserContext';
import { usePosts } from '../../contexts/PostContext';
import { PostService } from '../../service/PostService';
import styles from './VoteButton.module.css';

interface VoteButtonsProps {
    postId: string;
    initialScore: number;
    onScoreUpdate: (newScore: number) => void;
    username: string;
    author: string;
}

const VoteButton: React.FC<VoteButtonsProps> = ({ postId, initialScore, onScoreUpdate, username, author }) => {
    const { isLoggedIn } = useUser();
    const { fetchPosts } = usePosts();
    const [score, setScore] = React.useState(initialScore);

    const handleVote = async (voteType: 'UPVOTE' | 'DOWNVOTE') => {
        try {
            const newScore = await PostService.vote(voteType, username, postId);
            setScore(newScore);
            onScoreUpdate(newScore);
            fetchPosts(); 
        } catch (error) {
            console.error('Voting failed:', error);
        }
    };

    return (
        <div className={styles.voteContainer}>
            <button
                className={styles.voteButton}
                onClick={() => handleVote('UPVOTE')}
                disabled={!isLoggedIn || author === username}
            >
                ↑
            </button>
            <span className={styles.voteCount}>{score}</span>
            <button
                className={styles.voteButton}
                onClick={() => handleVote('DOWNVOTE')}
                disabled={!isLoggedIn || author === username}
            >
                ↓
            </button>
        </div>
    );
};

export default VoteButton;