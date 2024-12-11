import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';

// 定义路由元信息类型
declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean;
    requiresGuest?: boolean;
    title?: string;
  }
}

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { 
      requiresAuth: true,
      title: '首页'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { 
      requiresGuest: true,
      title: '登录'
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { 
      requiresGuest: true,
      title: '注册'
    }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/ResetPassword.vue'),
    meta: { 
      requiresGuest: true,
      title: '重置密码'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('sessionId');
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 软件工程` : '软件工程';

  // 已登录用户访问游客页面
  if (to.meta.requiresGuest && isAuthenticated) {
    next({ name: 'Home' });
    return;
  }

  // 未登录用户访问需要认证的页面
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ 
      name: 'Login',
      query: { redirect: to.fullPath }
    });
    return;
  }

  next();
});

export default router; 