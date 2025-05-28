import axios from "axios";
import { BASE_URL, GET_ALL_FRIENDSHIPS_ENDPOINT } from "../endpoints/endpoints";
import type { Friendship } from "../model/Friendship";

interface FriendshipResponse {
    friendships: Friendship[]
}

interface FriendshipRequest {
    from: string;
    to: string;
}

interface UnfriendRequest {
    from: string;
    to: string;
}

interface FriendshipResponse {
    friendships: Friendship[];
}

interface RequestResponse {
    requestId: string;
    username: string;
}

interface GetRequestsResponse {
    requests: RequestResponse[];
}

export class FriendshipService {
    static async getAllFriendships(username: string): Promise<Friendship[]> {
        const response = await axios.get<FriendshipResponse>(`${GET_ALL_FRIENDSHIPS_ENDPOINT}/${username}`, {
            withCredentials: true,
        });
        return response.data.friendships;
    }

    static async request(friendshipRequest: FriendshipRequest): Promise<void> {
        await axios.post(`${BASE_URL}/friendship/request`, friendshipRequest, {
            withCredentials: true,
        });
    }

    static async accept(requestId: string): Promise<void> {
        await axios.post(`${BASE_URL}/friendship/accept/${requestId}`, null, {
            withCredentials: true,
        });
    }

    static async unfriend(unfriendRequest: UnfriendRequest): Promise<void> {
        await axios.post(`${BASE_URL}/friendship/unfriend`, unfriendRequest, {
            withCredentials: true,
        });
    }

    static async getRequests(username: string): Promise<GetRequestsResponse> {
        const response = await axios.get(`${BASE_URL}/friendship/get/requests/${username}`, {
            withCredentials: true,
        });
        return response.data;
    }

    static async getFriendshipStatus(user1: string, user2: string): Promise<string> {
        const response = await axios.get(`${BASE_URL}/friendship/status`, {
            params: { user1, user2 },
            withCredentials: true,
        });
        return response.data;
    }
}