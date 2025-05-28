import axios, { isAxiosError } from "axios";
import { LOGIN_ENDPOINT, LOGOUT_ENDPOINT, REGISTER_ENDPOINT } from "../endpoints/endpoints";
import type { Credentials } from "../dto/Credentials";
import type { RegisterRequest } from "../dto/RegisterRequest";


interface LoginResponse {
    token: string;
    id: string;
    username: string;
    email: string;
}




export class AuthService {
    static async login(credentials: Credentials): Promise<LoginResponse> {
        const response = await axios.post(LOGIN_ENDPOINT, credentials);
        const data: LoginResponse = response.data;
        if (response.status === 200) {
            const token: string = data.token;
            sessionStorage.setItem("token", token);
        }
        return data;
    }

    static async logout(): Promise<void> {
        await axios.post(LOGOUT_ENDPOINT)
    }

    static async signup(credentials: RegisterRequest): Promise<string | null> {
        try {
            await axios.post<RegisterRequest>(REGISTER_ENDPOINT, credentials);
        } catch (error) {
            if (isAxiosError(error)) {
                return error.response?.data?.message || "Error in signup";
            } else {
                return "There is an error in the signup";
            }
        }

        return null;
    }
}