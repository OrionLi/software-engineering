<template>
  <div class="login-container">
    <el-card class="login-card animate__animated animate__fadeIn">
      <template #header>
        <h2>用户登录</h2>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="login-form"
        size="large"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="form.username"
            placeholder="请输入用户名"
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
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <div class="input-icon-container">
                <el-icon><Lock /></el-icon>
              </div>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item>
          <div class="button-group">
            <el-button 
              type="primary" 
              @click="handleSubmit" 
              :loading="loading"
              class="submit-btn"
            >
              登录
            </el-button>
            <el-button 
              @click="router.push('/register')"
              class="register-btn"
            >
              去注册
            </el-button>
          </div>
          <div class="forgot-password">
            <el-link type="primary" @click="router.push('/reset-password')">忘记密码？</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { User, Lock } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { userApi } from '@/api/user';
import type { LoginParams } from '@/types/api';

const router = useRouter();
const route = useRoute();
const formRef = ref<FormInstance>();
const loading = ref(false);

const form = reactive<LoginParams>({
  username: '',
  password: ''
});

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    const { data } = await userApi.login(form);
    localStorage.setItem('sessionId', data.data.sessionId!);
    ElMessage.success('登录成功');
    
    // 获取重定向地址
    const redirect = route.query.redirect as string;
    router.replace(redirect || '/');
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '登录失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 40px);
  background: url('@/assets/bg-pattern.svg') center/cover;
}

.login-card {
  width: 100%;
  max-width: 480px;
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

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.submit-btn, .register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(144, 147, 153, 0.2);
}

.forgot-password {
  text-align: center;
  margin-top: 16px;
}
</style> 