import axios, { AxiosError } from 'axios';
import { ElMessage } from 'element-plus';
import router from '@/router';
import type { ApiResponse, User, RegisterParams, LoginParams } from '@/types/api';

const api = axios.create({
  baseURL: '/api',
  timeout: 5000
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    const sessionId = localStorage.getItem('sessionId');
    if (sessionId) {
      config.headers['X-Session-Id'] = sessionId;
    }
    return config;
  },
  error => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => response,
  (error: AxiosError<ApiResponse>) => {
    if (!error.response) {
      ElMessage.error('网络错误，请检查网络连接');
      return Promise.reject(error);
    }

    switch (error.response.status) {
      case 401:
        localStorage.removeItem('sessionId');
        router.replace({
          name: 'Login',
          query: { redirect: router.currentRoute.value.fullPath }
        });
        break;
      case 403:
        ElMessage.error('没有权限执行此操作');
        break;
      case 404:
        ElMessage.error('请求的资源不存在');
        break;
      case 500:
        ElMessage.error('服务器错误，请稍后重试');
        break;
      default:
        ElMessage.error(error.response.data?.message || '操作失败');
    }

    return Promise.reject(error);
  }
);

// API 请求函数
export const userApi = {
  // 发送验证码
  async sendVerificationCode(email: string) {
    const response = await api.get<ApiResponse>('/user/verification-code', {
      params: { email }
    });
    if (response.data.code !== 200) {
      throw new Error(response.data.message);
    }
    return response;
  },

  // 用户注册
  async register(params: RegisterParams) {
    const response = await api.post<ApiResponse>('/user/register', params);
    if (response.data.code !== 200) {
      throw new Error(response.data.message);
    }
    return response;
  },

  // 用户登录
  async login(params: LoginParams) {
    const response = await api.post<ApiResponse<User>>('/user/login', params);
    if (response.data.code !== 200) {
      throw new Error(response.data.message);
    }
    return response;
  },

  // 用户登出
  async logout() {
    try {
      const response = await api.post<ApiResponse>('/user/logout');
      return response;
    } catch (error) {
      throw error;
    }
  }
}; 