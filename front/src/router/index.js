import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/goods',
    name: 'GoodsList',
    component: () => import('@/views/goods/GoodsList.vue')
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: () => import('@/views/goods/GoodsDetail.vue')
  },
  {
    path: '/goods/publish',
    name: 'GoodsPublish',
    component: () => import('@/views/goods/GoodsPublish.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: () => import('@/views/user/UserCenter.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'my-goods',
        name: 'MyGoods',
        component: () => import('@/views/user/MyGoods.vue')
      },
      {
        path: 'orders',
        name: 'MyOrders',
        component: () => import('@/views/user/MyOrders.vue')
      },
      {
        path: 'favorites',
        name: 'MyFavorites',
        component: () => import('@/views/user/MyFavorites.vue')
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/user/Messages.vue')
      },
      {
        path: 'verification',
        name: 'Verification',
        component: () => import('@/views/user/Verification.vue')
      },
      {
        path: 'wallet',
        name: 'Wallet',
        component: () => import('@/views/user/Wallet.vue')
      },
      {
        path: 'report',
        name: 'ReportUser',
        component: () => import('@/views/user/ReportUser.vue')
      }
    ]
  },
  {
    path: '/chat',
    name: 'ChatList',
    component: () => import('@/views/chat/ChatList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/chat/:id',
    name: 'ChatWindow',
    component: () => import('@/views/chat/ChatWindow.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'goods',
        name: 'AdminGoods',
        component: () => import('@/views/admin/Goods.vue')
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue')
      },
      {
        path: 'verifications',
        name: 'AdminVerifications',
        component: () => import('@/views/admin/Verifications.vue')
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('@/views/admin/Reports.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
