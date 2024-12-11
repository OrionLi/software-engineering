<template>
  <div class="app-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.app-container {
  min-height: 100vh;
  padding: 20px;
}

/* 全局标题样式 */
h2 {
  color: #303133;
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
  letter-spacing: 1px;
}

/* 全局卡片样式 */
.el-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s, box-shadow 0.3s;
  border: none;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.el-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.1);
}

/* 全局输入框样式 */
.el-input__wrapper {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transition: all 0.3s;
}

.el-input__wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08) !important;
}

.el-input__wrapper.is-focus {
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.15) !important;
}

/* 全局按钮样式 */
.el-button {
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #3a8ee6 100%);
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #409eff 100%);
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
