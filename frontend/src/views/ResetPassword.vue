<template>
  <div class="reset-password-container">
    <el-card class="reset-password-card animate__animated animate__fadeIn">
      <template #header>
        <h2>重置密码</h2>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="reset-password-form"
        size="large"
      >
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

        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="form.newPassword"
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

        <el-form-item>
          <div class="button-group">
            <el-button 
              type="primary" 
              @click="handleSubmit"
              :loading="loading"
              class="submit-btn"
            >
              重置密码
            </el-button>
            <el-button 
              @click="router.push('/login')"
              class="login-btn"
            >
              返回登录
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
import { Lock, Message, Key } from '@element-plus/icons-vue';
import type { FormInstance } from 'element-plus';
import { userApi } from '@/api/user';

const router = useRouter();
const formRef = ref<FormInstance>();
const countdown = ref(0);
const loading = ref(false);
const sending = ref(false);

const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: ''
});

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8位', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ]
};

// 添加防抖处理
let timer: number | null = null;

// 发送验证码
const handleSendCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先输入邮箱');
    return;
  }
  
  const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  if (!emailPattern.test(form.email)) {
    ElMessage.warning('请输入正确的邮箱格式');
    return;
  }

  try {
    sending.value = true;
    await userApi.sendVerificationCode(form.email);
    ElMessage.success('验证码已发送，请注意查收');
    countdown.value = 60;
    
    if (timer) {
      clearInterval(timer);
    }
    
    timer = window.setInterval(() => {
      if (countdown.value <= 0) {
        clearInterval(timer!);
        timer = null;
        return;
      }
      countdown.value--;
    }, 1000);
  } catch (error: any) {
    ElMessage.error(error.message || '验证码发送失败，请稍后重试');
  } finally {
    sending.value = false;
  }
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    loading.value = true;
    await userApi.resetPassword(form);
    ElMessage.success('密码重置成功，请重新登录');
    router.push('/login');
  } catch (error: any) {
    ElMessage.error(error.message || '重置失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 组件卸载时清理定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
});
</script>

<style scoped>
.reset-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 40px);
  background: url('@/assets/bg-pattern.svg') center/cover;
  padding: 20px;
}

.reset-password-card {
  width: 100%;
  max-width: 520px;
  overflow: hidden;
}

h2 {
  margin: 20px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  text-align: center;
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