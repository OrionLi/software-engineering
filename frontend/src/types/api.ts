// API响应类型
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// 用户类型
export interface User {
  id: number;
  username: string;
  email: string;
  sex: 'M' | 'F';
  registerDate: string;
  lastModifyDate: string;
  sessionId?: string;
}

// 注册参数类型
export interface RegisterParams {
  username: string;
  password: string;
  email: string;
  sex: string;
  verificationCode: string;
}

// 登录参数类型
export interface LoginParams {
  username: string;
  password: string;
}

export interface ResetPasswordParams {
  email: string;
  verificationCode: string;
  newPassword: string;
} 