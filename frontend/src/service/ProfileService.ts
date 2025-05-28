import axios from 'axios';
import { PROFILE_CREATE_ENDPOINT, PROFILE_EXISTS_ENDPOINT, PROFILE_GET_ENDPOINT } from '../endpoints/endpoints';
import type { SimpleMessageResponse } from '../dto/SimpleMessageResponse';

interface PostMetadata {
  id: string;
  author: string;
  title: string;
  created: string;
  score: number;
  tags: string[];
}

interface Profile {
  fullName: string;
  description: string;
  preferredTags: string[];
  postsMetadata: PostMetadata[];
}

export class ProfileService {
  private static baseUrl = 'http://localhost:8080/api/profile';

  static async getProfile(username: string): Promise<Profile> {
    const response = await axios.get<Profile>(`${PROFILE_GET_ENDPOINT}/${username}`);
    return response.data;
  }

  static async profileExists(username: string): Promise<boolean> {
    const response = await axios.get<SimpleMessageResponse>(`${PROFILE_EXISTS_ENDPOINT}/${username}`);
    return response.data.message === "true";
  }

  static getProfilePictureUrl(username: string | undefined): string {
    if (username === undefined) return "";
    return `${ProfileService.baseUrl}/picture/${username}`;
  }

  static async createProfile(formData: FormData): Promise<SimpleMessageResponse> {
    const response = await axios.post<SimpleMessageResponse>(PROFILE_CREATE_ENDPOINT, formData, {
      withCredentials: true,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  }
}