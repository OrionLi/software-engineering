<template>
  <div class="home-container">
    <el-card class="welcome-card animate__animated animate__fadeInUp">
      <template #header>
        <div class="card-header">
          <div class="user-info">
            <div class="avatar">
              <el-icon><UserFilled /></el-icon>
            </div>
            <h2>欢迎回来</h2>
          </div>
          <el-button 
            type="danger" 
            @click="handleLogout"
            :loading="loading"
            class="logout-btn"
          >
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </el-button>
        </div>
      </template>
      <div class="card-content">
        <el-empty 
          description="更多功能开发中..." 
          :image-size="200"
        >
          <template #image>
            <el-icon class="empty-icon"><Tools /></el-icon>
          </template>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { UserFilled, SwitchButton, Tools } from '@element-plus/icons-vue';
import { userApi } from '@/api/user';

const router = useRouter();
const loading = ref(false);

const handleLogout = async () => {
  try {
    loading.value = true;
    await userApi.logout();
    localStorage.removeItem('sessionId');
    ElMessage.success('已退出登录');
    router.push('/login');
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '退出失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  background: url('@/assets/bg-pattern.svg') center/cover;
}

.welcome-card {
  width: 100%;
  max-width: 800px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #F56C6C 0%, #E24C4C 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(245, 108, 108, 0.3);
  animation: pulse 2s infinite;
}

.avatar .el-icon {
  font-size: 32px;
  color: white;
}

.card-header h2 {
  margin: 0;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 24px;
  font-size: 16px;
  transition: all 0.3s;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(245, 108, 108, 0.3);
}

.card-content {
  min-height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.empty-icon {
  font-size: 100px;
  color: #909399;
  animation: wrench 2.5s ease infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

@keyframes wrench {
  0% { transform: rotate(-12deg); }
  8% { transform: rotate(12deg); }
  10% { transform: rotate(24deg); }
  18% { transform: rotate(-24deg); }
  20% { transform: rotate(-24deg); }
  28% { transform: rotate(24deg); }
  30% { transform: rotate(24deg); }
  38% { transform: rotate(-24deg); }
  40% { transform: rotate(-24deg); }
  48% { transform: rotate(24deg); }
  50% { transform: rotate(24deg); }
  58% { transform: rotate(-24deg); }
  60% { transform: rotate(-24deg); }
  68% { transform: rotate(24deg); }
  75% { transform: rotate(0deg); }
}
</style> 