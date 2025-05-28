import React, { useState, useEffect } from 'react';
import { useUser } from '../../contexts/UserContext';
import { FriendshipService } from '../../service/FriendshipService';
import { ProfileService } from '../../service/ProfileService';
import styles from './Friendships.module.css';
import Header from '../../components/header/Header';

interface RequestResponse {
    requestId: string;
    username: string;
}

const Friendships: React.FC = () => {
    const { user, friends, setFriends, fetchFriends } = useUser();
    const [requests, setRequests] = useState<RequestResponse[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchFriends();
    }, []);

    useEffect(() => {
        const fetchRequests = async () => {
            try {
                if (user?.username) {
                    const requestsData = await FriendshipService.getRequests(user.username);
                    setRequests(requestsData.requests);
                }
            } catch (err) {
                setError('Failed to fetch friendship requests.');
            }
        };
        fetchRequests();
    }, [user?.username]);

    useEffect(() => {
        requests.forEach(req => {
            console.log(`Req id: ${req.requestId}, Name: ${req.username}`);
        })
    }, [requests]);

    const handleAccept = async (requestId: string) => {
        try {
            await FriendshipService.accept(requestId);
            const acceptedRequest = requests.find(req => req.requestId === requestId);
            if (acceptedRequest) {
                setFriends([...friends, { userId: requestId, username: acceptedRequest.username }]);
            }
            setRequests(requests.filter(req => req.requestId !== requestId));
        } catch (err) {
            setError('Failed to accept request.');
        }
    };

    return (
        <div className={styles.friendshipsWrapper}>
            <Header />
            <div className={styles.friendshipsContainer}>
                <h2 className={styles.title}>Friendships</h2>
                {error && <p className={styles.error}>{error}</p>}
                <div className={styles.section}>
                    <h3>Friends</h3>
                    {friends.length === 0 ? (
                        <p>No friends yet.</p>
                    ) : (
                        friends.map((friend) => (
                            <div key={friend.userId} className={styles.friendCard}>
                                <img
                                    src={ProfileService.getProfilePictureUrl(friend.username)}
                                    alt={`${friend.username}'s profile`}
                                    className={styles.profilePicture}
                                />
                                <span className={styles.username}>{friend.username}</span>
                            </div>
                        ))
                    )}
                </div>
                <div className={styles.section}>
                    <h3>Pending Requests</h3>
                    {requests.length === 0 ? (
                        <p>No pending requests.</p>
                    ) : (
                        requests.map((request) => (
                            <div key={request.requestId} className={styles.requestCard}>
                                <span className={styles.username}>{request.username}</span>
                                <button
                                    onClick={() => handleAccept(request.requestId)}
                                    className={styles.acceptButton}
                                >
                                    Accept
                                </button>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};

export default Friendships;