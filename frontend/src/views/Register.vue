<template>
  <div class="register-container">
    <el-card class="register-card animate__animated animate__fadeIn">
      <template #header>
        <h2>用户注册</h2>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="register-form"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username"
            placeholder="请输入4-32位用户名"
          >
            <template #prefix>
              <div class="input-icon-container">
                <el-icon><User /></el-icon>
              </div>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="form.password"
            type="password"
            placeholder="至少8位，包含大小写字母和数字"
            show-password
          >
            <template #prefix>
              <div class="input-icon-container">
                <el-icon><Lock /></el-icon>
              </div>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input 
            v-model="form.email"
            placeholder="请输入邮箱"
          >
            <template #prefix>
              <div class="input-icon-container">
                <el-icon><Message /></el-icon>
              </div>
            </template>
            <template #append>
              <el-button 
                :disabled="countdown > 0"
                @click="handleSendCode"
                :loading="sending"
                class="code-btn"
                :type="countdown > 0 ? 'info' : 'primary'"
              >
                {{ countdown > 0 ? `${countdown}s后重试` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="验证码" prop="verificationCode">
          <el-input 
            v-model="form.verificationCode"
            placeholder="请输入6位验证码"
            maxlength="6"
          >
            <template #prefix>
              <div class="input-icon-container">
                <el-icon><Key /></el-icon>
              </div>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex" class="radio-group">
            <el-radio label="M" class="radio-btn">
              <el-icon><Male /></el-icon> 男
            </el-radio>
            <el-radio label="F" class="radio-btn">
              <el-icon><Female /></el-icon> 女
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item>
          <div class="button-group">
            <el-button 
              type="primary" 
              @click="handleSubmit"
              :loading="loading"
              class="submit-btn"
            >
              注册
            </el-button>
            <el-button 
              @click="router.push('/login')"
              class="login-btn"
            >
              去登录
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock, Message, Key, Male, Female } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { userApi } from '@/api/user';
import type { RegisterParams } from '@/types/api';

const router = useRouter();
const formRef = ref<FormInstance>();
const countdown = ref(0);
const loading = ref(false);
const sending = ref(false);

const form = reactive<RegisterParams>({
  username: '',
  password: '',
  email: '',
  verificationCode: '',
  sex: ''
});

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 32, message: '用户名长度为4-32位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
};

// 添加防抖处理
let timer: number | null = null;

// 发送验证码
const handleSendCode = async () => {
  // 添加邮箱格式验证
  if (!form.email) {
    ElMessage.warning('请先输入邮箱');
    return;
  }
  
  // 验证邮箱格式
  const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  if (!emailPattern.test(form.email)) {
    ElMessage.warning('请输入正确的邮箱格式');
    return;
  }

  try {
    sending.value = true;
    await userApi.sendVerificationCode(form.email);
    ElMessage.success('验证码已发送，请注意查收');
    countdown.value = 5;
    
    // 清除之前的定时器
    if (timer) {
      clearInterval(timer);
    }
    
    // 启动新的定时器
    timer = window.setInterval(() => {
      if (countdown.value <= 0) {
        clearInterval(timer!);
        timer = null;
        return;
      }
      countdown.value--;
    }, 1000);

  } catch (error: any) {
    console.error('Send Code Error:', error);
    ElMessage.error(
      error.response?.data?.message || 
      error.response?.data?.error || 
      error.message || 
      '发送失败，请检查邮箱是否正确'
    );
  } finally {
    sending.value = false;
  }
};

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});

// 提交注册
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    await userApi.register(form);
    ElMessage.success('注册成功');
    router.push('/login');
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '注册失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 40px);
  background: url('@/assets/bg-pattern.svg') center/cover;
}

.register-card {
  width: 100%;
  max-width: 520px;
  overflow: hidden;
}

h2 {
  margin: 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.input-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  color: #909399;
}

.code-btn {
  min-width: 120px;
  transition: all 0.3s;
}

.code-btn:not(:disabled):hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.code-btn:disabled {
  cursor: not-allowed;
  opacity: 0.8;
}

.radio-group {
  display: flex;
  gap: 30px;
}

.radio-btn {
  display: flex;
  align-items: center;
  gap: 4px;
}

.radio-btn :deep(.el-icon) {
  margin-right: 4px;
  font-size: 18px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.submit-btn, .login-btn {
  width: 140px;
  height: 44px;
  font-size: 16px;
}

/* 输入框动画 */
.el-input {
  transition: all 0.3s;
}

.el-input:focus-within {
  transform: translateY(-1px);
}

/* 按钮悬浮效果 */
.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(144, 147, 153, 0.2);
}
</style> 