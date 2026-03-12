<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <nav class="navbar glass-card">
      <div class="nav-container">
        <div class="logo">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <span>校园二手市场</span>
        </div>
        <div class="nav-links">
          <!-- 管理员导航 -->
          <template v-if="isLoggedIn && userInfo?.role === 'admin'">
            <router-link to="/admin/users" class="nav-link">用户管理</router-link>
            <router-link to="/admin/goods" class="nav-link">商品审核</router-link>
            <router-link to="/admin/verifications" class="nav-link">认证审核</router-link>
            <router-link to="/admin/reports" class="nav-link">举报处理</router-link>
            <router-link to="/admin/statistics" class="nav-link">数据统计</router-link>
          </template>
          
          <!-- 普通用户导航 -->
          <template v-else>
            <router-link to="/goods" class="nav-link">商品广场</router-link>
            
            <!-- 我的聊天 -->
            <router-link v-if="isLoggedIn" to="/chat" class="nav-link icon-link" title="我的聊天">
              <el-badge :value="unreadChatCount" :hidden="unreadChatCount === 0" type="danger">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
              </el-badge>
            </router-link>
            
            <!-- 消息通知 -->
            <router-link v-if="isLoggedIn" to="/user/messages" class="nav-link icon-link" title="消息通知">
              <el-badge :value="unreadNotificationCount" :hidden="unreadNotificationCount === 0" type="danger">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
              </el-badge>
            </router-link>
            
            <!-- 我的钱包 -->
            <router-link v-if="isLoggedIn" to="/user/wallet" class="nav-link icon-link" title="我的钱包">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
            </router-link>
          </template>
          
          <router-link v-if="!isLoggedIn" to="/login" class="nav-link">登录</router-link>
          <router-link v-if="!isLoggedIn" to="/register" class="nav-link">注册</router-link>
          
          <!-- 用户头像下拉框 -->
          <el-dropdown v-else @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :src="userInfo?.avatar" :size="32">{{ userInfo?.nickname?.[0] }}</el-avatar>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <!-- 管理员菜单 -->
                <template v-if="userInfo?.role === 'admin'">
                  <el-dropdown-item command="admin">管理后台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </template>
                <!-- 普通用户菜单 -->
                <template v-else>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="my-goods">我的商品</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="favorites">我的收藏</el-dropdown-item>
                  <el-dropdown-item command="verification">实名认证</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </template>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">让闲置物品流动起来</h1>
        <p class="hero-subtitle">校园二手交易，安全便捷，绿色环保</p>
        <div class="hero-actions">
          <router-link to="/goods" class="btn-cta">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            浏览商品
          </router-link>
          <router-link v-if="isLoggedIn" to="/goods/publish" class="btn-primary">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            发布商品
          </router-link>
        </div>
      </div>
    </section>

    <!-- 特色功能 -->
    <section class="features-section">
      <div class="features-container">
        <div class="feature-card glass-card">
          <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
          <h3>实名认证</h3>
          <p>校园卡认证，交易更安全</p>
        </div>
        <div class="feature-card glass-card">
          <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8h2a2 2 0 012 2v6a2 2 0 01-2 2h-2v4l-4-4H9a1.994 1.994 0 01-1.414-.586m0 0L11 14h4a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2v4l.586-.586z" />
          </svg>
          <h3>即时沟通</h3>
          <p>站内留言，快速联系</p>
        </div>
        <div class="feature-card glass-card">
          <svg class="feature-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <h3>信誉保障</h3>
          <p>信誉评分系统，诚信交易</p>
        </div>
      </div>
    </section>

    <!-- 热门分类 -->
    <section class="categories-section">
      <h2 class="section-title">热门分类</h2>
      <div class="categories-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card glass-card" @click="goToCategory(cat.id)">
          <div class="category-icon" v-html="cat.icon"></div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getUnreadChatCount } from '@/api/chat'
import { getUnreadNotificationCount } from '@/api/notification'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo)
const unreadChatCount = ref(0)
const unreadNotificationCount = ref(0)

let intervalId = null

const fetchUnreadCount = async () => {
  if (!isLoggedIn.value) return
  try {
    const [chatRes, notificationRes] = await Promise.all([
      getUnreadChatCount(),
      getUnreadNotificationCount()
    ])
    unreadChatCount.value = chatRes.data
    unreadNotificationCount.value = notificationRes.data
  } catch (error) {
    console.error('获取未读消息数失败', error)
  }
}

onMounted(() => {
  // 如果是管理员，直接跳转到管理后台
  if (isLoggedIn.value && userInfo.value?.role === 'admin') {
    router.push('/admin/users')
    return
  }
  
  fetchUnreadCount()
  // 每30秒刷新一次未读数
  intervalId = setInterval(fetchUnreadCount, 30000)
})

onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})

const categories = [
  { id: 1, name: '教材书籍', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" /></svg>' },
  { id: 2, name: '电子数码', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z" /></svg>' },
  { id: 3, name: '生活用品', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" /></svg>' },
  { id: 4, name: '交通工具', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" /></svg>' }
]

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
  } else if (command === 'admin') {
    router.push('/admin/users')
  } else if (command === 'verification') {
    router.push('/user/verification')
  } else {
    router.push(`/user/${command}`)
  }
}

const goToCategory = (categoryId) => {
  router.push({ path: '/goods', query: { category: categoryId } })
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
}

.navbar {
  position: absolute;
  top: 16px;
  left: 16px;
  right: 16px;
  z-index: 1000;
  padding: 16px 24px;

  .nav-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    max-width: 1200px;
    margin: 0 auto;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    color: #2563EB;

    .icon {
      width: 28px;
      height: 28px;
    }
  }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 24px;
  }

  .nav-link {
    color: #1E293B;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.2s;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 8px;

    .icon {
      width: 20px;
      height: 20px;
    }

    &:hover {
      color: #2563EB;
    }
    
    &.icon-link {
      padding: 8px;
      border-radius: 8px;
      transition: all 0.2s;
      
      &:hover {
        background: rgba(37, 99, 235, 0.1);
      }
    }
  }

  .user-dropdown {
    cursor: pointer;
  }
}

.hero-section {
  padding: 160px 24px 80px;
  text-align: center;

  .hero-title {
    font-size: 56px;
    font-weight: 700;
    color: white;
    margin-bottom: 16px;
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.2);
  }

  .hero-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: 40px;
  }

  .hero-actions {
    display: flex;
    gap: 16px;
    justify-content: center;
    align-items: center;

    a {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 14px 32px;
      border-radius: 12px;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.2s;
      cursor: pointer;

      .icon {
        width: 20px;
        height: 20px;
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }
    }
  }
}

.features-section {
  padding: 80px 24px;
  background: rgba(255, 255, 255, 0.05);

  .features-container {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
  }

  .feature-card {
    padding: 32px;
    text-align: center;
    cursor: pointer;

    .feature-icon {
      width: 48px;
      height: 48px;
      color: #2563EB;
      margin: 0 auto 16px;
    }

    h3 {
      font-size: 20px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #1E293B;
    }

    p {
      color: #64748B;
      font-size: 14px;
    }
  }
}

.categories-section {
  padding: 80px 24px;
  max-width: 1200px;
  margin: 0 auto;

  .section-title {
    font-size: 32px;
    font-weight: 700;
    color: white;
    text-align: center;
    margin-bottom: 40px;
  }

  .categories-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
  }

  .category-card {
    padding: 32px;
    text-align: center;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-4px);
    }

    .category-icon {
      width: 48px;
      height: 48px;
      margin: 0 auto 16px;
      color: #2563EB;

      svg {
        width: 100%;
        height: 100%;
      }
    }

    .category-name {
      font-size: 16px;
      font-weight: 600;
      color: #1E293B;
    }
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 36px !important;
  }

  .hero-actions {
    flex-direction: column;
  }
}
</style>
