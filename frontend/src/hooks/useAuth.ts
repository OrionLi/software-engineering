import { ref } from 'vue';
import { userApi } from '@/api/user';
import type { LoginParams } from '@/types/api';

export const useAuth = () => {
  const isAuthenticated = ref(!!localStorage.getItem('sessionId'));

  const login = async (params: LoginParams) => {
    const { data } = await userApi.login(params);
    localStorage.setItem('sessionId', data.data.sessionId!);
    isAuthenticated.value = true;
    return data;
  };

  const logout = async () => {
    try {
      await userApi.logout();
    } finally {
      localStorage.removeItem('sessionId');
      isAuthenticated.value = false;
    }
  };

  const checkAuth = () => {
    return isAuthenticated.value;
  };

  return {
    isAuthenticated,
    login,
    logout,
    checkAuth
  };
}; 