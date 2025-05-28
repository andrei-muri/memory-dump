import axios from "axios";
import { TAG_GET_ENDPOINT } from "../endpoints/endpoints";
import type { Tag } from "../model/Tag";

export class TagService {
    static async fetchTags(): Promise<Tag[]> {
      const response = await axios.get<Tag[]>(TAG_GET_ENDPOINT, {
        withCredentials: true,
      });
      return response.data;
    }
  }