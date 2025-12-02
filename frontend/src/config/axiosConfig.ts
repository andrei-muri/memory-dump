import axios from 'axios';
import { REFRESH_ENDPOINT } from '../endpoints/endpoints';
import i18n from "i18next";

// Function to refresh the access token
const refreshAccessToken = async () => {
  try {
    // Call the refresh token endpoint; the server will read the HTTP-only refreshToken cookie
    const response = await axios.post(`${REFRESH_ENDPOINT}`, {}, {
      withCredentials: true, 
    });

    
    const newAccessToken = response.data.accessToken;

    
    sessionStorage.setItem('token', newAccessToken);

    return newAccessToken;
  } catch (error) {
    console.error('Failed to refresh token:', error);
    
    sessionStorage.removeItem('token');
    window.location.href = '/';
    throw error;
  }
};

axios.interceptors.request.use(
  (config) => {
    const accessToken = sessionStorage.getItem('token');
    config.headers["Accept-Language"] = i18n.language;
    if (accessToken) {
      config.headers = config.headers || {};
      config.headers.set('Authorization', `Bearer ${accessToken}`);
    }
    config.withCredentials = true;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);


axios.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // Check if the error is 401 and the request hasn't been retried yet
    const accessToken = sessionStorage.getItem("token");
    if (error.response?.status === 401 && !originalRequest._retry && accessToken) {
      originalRequest._retry = true; // Mark the request as retried

      try {
        // Refresh the access token
        const newAccessToken = await refreshAccessToken();

        // Update the Authorization header with the new token
        originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;

        // Retry the original request with the new token
        return axios(originalRequest);
      } catch (refreshError) {
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);