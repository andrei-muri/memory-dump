import type { Role } from "../model/Role";

export interface RegisterRequest {
    username: string;
    password: string;
    email: string;
    role: Role;
  }