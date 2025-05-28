import axios from 'axios';
import { BASE_URL } from '../endpoints/endpoints';

interface MessageDTO {
    id: string;
    senderUsername: string;
    recipientUsername: string;
    content: string;
    timestamp: string;
    delivered: boolean;
}

interface SendMessageDTO {
    recipientUsername: string;
    content: string;
}

export class MessageService {
    static async sendMessage(dto: SendMessageDTO): Promise<MessageDTO> {
        const response = await axios.post(`${BASE_URL}/messages`, dto, {
            withCredentials: true,
        });
        return response.data;
    }

    static async getChatHistory(friendUsername: string): Promise<MessageDTO[]> {
        const response = await axios.get(`${BASE_URL}/messages/${friendUsername}`, {
            withCredentials: true,
        });
        return response.data;
    }
}