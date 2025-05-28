export const BASE_URL = "http://localhost:8080/api";
const AUTH_ENDPOINT = `${BASE_URL}/auth`;
export const LOGIN_ENDPOINT = `${AUTH_ENDPOINT}/login`;
export const REGISTER_ENDPOINT = `${AUTH_ENDPOINT}/register`;
export const REFRESH_ENDPOINT = `${AUTH_ENDPOINT}/refresh`;
export const LOGOUT_ENDPOINT = `${AUTH_ENDPOINT}/logout`;
export const CHANGE_PASS_ENDPOINT = `${AUTH_ENDPOINT}/changepass`;
export const MAIL_ENDPOINT = `${AUTH_ENDPOINT}/mail`;
export const RESET_ENDPOINT = `${AUTH_ENDPOINT}/reset`;

const FRIENDSHIP_ENDPOINT = `${BASE_URL}/friendship`;
export const REQUEST_FRIENDSHIP_ENDPOINT = `${FRIENDSHIP_ENDPOINT}/request`;
export const ACCEPT_FRIENDSHIP_ENDPOINT = `${FRIENDSHIP_ENDPOINT}/accept`;
export const GET_ALL_FRIENDSHIPS_ENDPOINT = `${FRIENDSHIP_ENDPOINT}/get`;
export const GET_FRIENDSHIPS_REQUESTS_ENDPOINT = `${FRIENDSHIP_ENDPOINT}/get/requests`;

const PROFILE_ENDPOINT = `${BASE_URL}/profile`;
export const PROFILE_CREATE_ENDPOINT = `${PROFILE_ENDPOINT}/create`;
export const PROFILE_PICTURE_ENDPOINT = `${PROFILE_ENDPOINT}/picture`;
export const PROFILE_GET_ENDPOINT = `${PROFILE_ENDPOINT}/get`;
export const PROFILE_EXISTS_ENDPOINT = `${PROFILE_ENDPOINT}/exists`;

const TAG_ENDPOINT = `${BASE_URL}/tag`;
export const TAG_GET_ENDPOINT = `${TAG_ENDPOINT}/get`;

