import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { message } from 'antd';
import type { ApiResponse } from '../types/api';

const request = axios.create({
  baseURL: '',
  timeout: 15000,
});

request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('adminToken');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

request.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse;
    if (res.code !== 0) {
      message.error(res.message || '请求失败');
      return Promise.reject(new Error(res.message));
    }
    return response;
  },
  (error: AxiosError<ApiResponse>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('adminToken');
      localStorage.removeItem('adminUser');
      window.location.href = '/login';
      return Promise.reject(error);
    }
    const msg = error.response?.data?.message || '网络错误';
    message.error(msg);
    return Promise.reject(error);
  }
);

export default request;
