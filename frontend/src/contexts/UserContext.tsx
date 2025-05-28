import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import type { Credentials } from '../dto/Credentials';
import { AuthService } from '../service/AuthService';
import { isAxiosError } from 'axios';
import { FriendshipService } from '../service/FriendshipService';
import { useNavigate } from 'react-router';
import type { Friendship } from '../model/Friendship';
import { ProfileService } from '../service/ProfileService';
import { Client } from '@stomp/stompjs';

interface User {
    id: string;
    username: string;
    email: string;
}

interface MessageDTO {
    id: string;
    senderUsername: string;
    recipientUsername: string;
    content: string;
    timestamp: string;
    delivered: boolean;
}

interface UserContextType {
    user: User | null;
    friends: Friendship[];
    messages: { [key: string]: MessageDTO[] };
    login: (credentials: Credentials) => Promise<string | null>;
    logout: () => void;
    isLoggedIn: boolean;
    setFriends: (friends: Friendship[]) => void;
    hasProfile: boolean;
    setHasProfile: (value: boolean) => void;
    fetchFriends: () => void;
    addMessage: (message: MessageDTO) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [friends, setFriends] = useState<Friendship[]>([]);
    const [hasProfile, setHasProfile] = useState<boolean>(false);
    const [messages, setMessages] = useState<{ [key: string]: MessageDTO[] }>({});
    const navigate = useNavigate();
    const stompClient = useRef<Client | null>(null);

    useEffect(() => {
        if (friends.length > 0) {
            console.log("Friends updated:", friends);
            friends.forEach((friend) => console.log(friend.username));
        }
    }, [friends, hasProfile]);

    useEffect(() => {
        if (user) {
            initializeWebSocket();
        }
        return () => {
            if (stompClient.current) {
                stompClient.current.deactivate();
            }
        };
    }, [user]);

    const initializeWebSocket = () => {
        const token = sessionStorage.getItem('token');
        const client = new Client({
            brokerURL: `ws://localhost:8080/ws-chat?access_token=${token}`,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("Connected");
                client.subscribe('/user/queue/messages', (message) => {
                    const msg: MessageDTO = JSON.parse(message.body);
                    addMessage(msg);
                });
                client.subscribe('/app/messages', (message) => {
                    const allMessages: MessageDTO[] = JSON.parse(message.body);
                    allMessages.forEach((msg) => addMessage(msg));
                });
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
            },
        });
        client.activate();
        stompClient.current = client;
    };

    const login = async (credentials: Credentials) => {
        try {
            const { id, username, email } = await AuthService.login(credentials);
            setUser({ id, username, email });
            const friendships = await FriendshipService.getAllFriendships(username);
            setFriends(friendships);
            const hasProfileResponse = await ProfileService.profileExists(username);
            setHasProfile(hasProfileResponse);
            if (hasProfileResponse) {
                console.log("Has profile");
            } else {
                console.log("Doesn't have profile");
            }
            console.log("Login and retrieval of friends done successfully!");
        } catch (error) {
            setUser(null);
            setFriends([]);
            if (isAxiosError(error)) {
                return error.response?.data?.message;
            } else {
                return "Error in login";
            }
        }
        return null;
    };

    const fetchFriends = async () => {
        try {
            const friendships = await FriendshipService.getAllFriendships(user ? user.username : "");
            setFriends(friendships);
        } catch (error) {
            if (isAxiosError(error)) {
                return error.response?.data?.message;
            } else {
                return "Error in friends";
            }
        }
    };

    const isLoggedIn = !!user;

    const logout = async () => {
        try {
            await AuthService.logout();
            setUser(null);
            setFriends([]);
            setMessages({});
            if (stompClient.current) {
                stompClient.current.deactivate();
            }
            sessionStorage.removeItem("token");
            console.log("Logout successfully");
            navigate("/");
        } catch (error) {
            return;
        }
    };

    const addMessage = (message: MessageDTO) => {
        setMessages((prev) => {
            const friendUsername = message.senderUsername === user?.username ? message.recipientUsername : message.senderUsername;
            const friendMessages = prev[friendUsername] || [];
            return {
                ...prev,
                [friendUsername]: [...friendMessages, message].sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime()),
            };
        });
    };

    return (
        <UserContext.Provider value={{ user, friends, messages, login, logout, isLoggedIn, setFriends, hasProfile, setHasProfile, fetchFriends, addMessage }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) throw new Error('useUser must be used within a UserProvider');
    return context;
};