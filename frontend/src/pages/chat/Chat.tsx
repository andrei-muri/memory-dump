import React, { useState, useEffect, useRef } from 'react';
import { useUser } from '../../contexts/UserContext';
import { MessageService } from '../../service/MessageService';
import Header from '../../components/header/Header';
import { ProfileService } from '../../service/ProfileService';
import styles from './Chat.module.css';

const Chat: React.FC = () => {
    const { user, friends, messages, addMessage } = useUser();
    const [selectedFriend, setSelectedFriend] = useState<string | null>(null);
    const [messageInput, setMessageInput] = useState('');
    const [error, setError] = useState<string | null>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const sendMessage = async () => {
        if (messageInput.trim() && selectedFriend && user?.username) {
            try {
                const dto = { recipientUsername: selectedFriend, content: messageInput };
                const sentMessage = await MessageService.sendMessage(dto);
                addMessage(sentMessage);
                setMessageInput('');
            } catch (err) {
                setError('Failed to send message.');
            }
        }
    };

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages, selectedFriend]);

    return (
        <div className={styles.chatWrapper}>
            <Header />
            <div className={styles.chatContainer}>
                <div className={styles.friendsSidebar}>
                    <h2 className={styles.sidebarTitle}>Friends</h2>
                    {friends.length === 0 ? (
                        <p className={styles.noFriends}>No friends yet.</p>
                    ) : (
                        friends.map((friend) => (
                            <div
                                key={friend.userId}
                                className={`${styles.friendItem} ${selectedFriend === friend.username ? styles.selected : ''}`}
                                onClick={() => setSelectedFriend(friend.username)}
                            >
                                <img
                                    src={ProfileService.getProfilePictureUrl(friend.username)}
                                    alt={`${friend.username}'s profile`}
                                    className={styles.profilePicture}
                                />
                                <span className={styles.friendName}>{friend.username}</span>
                                <span className={styles.statusIndicator} />
                            </div>
                        ))
                    )}
                </div>
                <div className={styles.chatArea}>
                    {selectedFriend ? (
                        <>
                            <div className={styles.chatHeader}>
                                <h2 className={styles.chatTitle}>{selectedFriend}</h2>
                            </div>
                            <div className={styles.messagesContainer}>
                                {messages[selectedFriend]?.map((msg) => (
                                    <div
                                        key={msg.id}
                                        className={`${styles.message} ${msg.senderUsername === user?.username ? styles.sent : styles.received}`}
                                    >
                                        <p className={styles.messageContent}>{msg.content}</p>
                                        <span className={styles.messageTimestamp}>
                                            {new Date(msg.timestamp).toLocaleTimeString()}
                                        </span>
                                    </div>
                                ))}
                                <div ref={messagesEndRef} />
                            </div>
                            <div className={styles.inputArea}>
                                <input
                                    type="text"
                                    value={messageInput}
                                    onChange={(e) => setMessageInput(e.target.value)}
                                    className={styles.messageInput}
                                    placeholder="Type a message..."
                                />
                                <button onClick={sendMessage} className={styles.sendButton}>
                                    Send
                                </button>
                            </div>
                        </>
                    ) : (
                        <div className={styles.noChatSelected}>
                            <p>Select a friend to start chatting</p>
                        </div>
                    )}
                    {error && <p className={styles.error}>{error}</p>}
                </div>
            </div>
        </div>
    );
};

export default Chat;