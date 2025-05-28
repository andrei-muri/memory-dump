import axios from 'axios';

interface PostMetadata {
  id: string;
  author: string;
  title: string;
  created: string;
  score: number;
  tags: string[];
}

interface ProgrammingLanguagePercentage {
  language: string;
  percentage: string;
}

interface GetPostByIdResponse {
  id: string;
  author: string;
  title: string;
  content: string;
  created: string;
  tags: string[];
  programmingLanguagePercentages: ProgrammingLanguagePercentage[];
  score: number;
}

interface PostCreateDTO {
  author: string;
  title: string;
  content: string;
  tags: string[];
}


export class PostService {
  private static baseUrl = 'http://localhost:8080/api/post';

  static async getAllPosts(id?: string, sort?: string, tags?: string[]): Promise<PostMetadata[]> {
    const params: any = {};
    if (id) params.id = id;
    if (sort) params.sort = sort;
    if (tags) params.tags = tags;

    const response = await axios.get<PostMetadata[]>(`${PostService.baseUrl}/get/all`, { params });
    return response.data;
  }

  static async vote(voteType: "DOWNVOTE" | "UPVOTE", username: string, postId: string): Promise<number> {
    const response = await axios.post(`${PostService.baseUrl}/vote`, {voteType, username, postId});
    return response.data;
  }

  static async getPostById(id: string): Promise<GetPostByIdResponse> {
    const response = await axios.get<GetPostByIdResponse>(`${PostService.baseUrl}/get/${id}`);
    return response.data;
  }

  static async createPost(data: PostCreateDTO): Promise<void> {
    await axios.post(`${this.baseUrl}/create`, data, {
      withCredentials: true,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }
}