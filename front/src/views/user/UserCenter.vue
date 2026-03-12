<template>
  <div class="user-center-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <router-link to="/" class="logo">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <span>校园二手市场</span>
        </router-link>
      </div>
    </nav>

    <div class="content-wrapper">
      <aside class="sidebar glass-card">
        <div class="user-profile">
          <el-avatar :src="userInfo?.avatar" :size="80">{{ userInfo?.nickname?.[0] }}</el-avatar>
          <div class="user-name">{{ userInfo?.nickname }}</div>
          <div class="user-credit">信誉分：{{ userInfo?.creditScore }}</div>
        </div>

        <div class="menu-list">
          <router-link to="/user/profile" class="menu-item">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span>个人资料</span>
          </router-link>
          <router-link to="/user/my-goods" class="menu-item">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
            <span>我的商品</span>
          </router-link>
          <router-link to="/user/orders" class="menu-item">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            <span>我的订单</span>
          </router-link>
          <router-link to="/user/favorites" class="menu-item">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
            <span>我的收藏</span>
          </router-link>
          <router-link to="/user/verification" class="menu-item">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
            </svg>
            <span>实名认证</span>
          </router-link>
        </div>
      </aside>

      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

onMounted(() => {
  if (!userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})
</script>

<style lang="scss" scoped>
.user-center-page {
  min-height: 100vh;
  padding-bottom: 40px;
}

.navbar {
  position: sticky;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 24px;
  margin: 0 0 24px 0;

  .nav-container {
    max-width: 1400px;
    margin: 0 auto;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 18px;
    font-weight: 600;
    color: #2563EB;
    text-decoration: none;

    .icon {
      width: 24px;
      height: 24px;
    }
  }
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 16px;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

.sidebar {
  padding: 24px;
  height: fit-content;
  position: sticky;
  top: 90px;

  .user-profile {
    text-align: center;
    padding-bottom: 24px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    margin-bottom: 24px;

    .user-name {
      font-size: 18px;
      font-weight: 600;
      color: #1E293B;
      margin-top: 12px;
    }

    .user-credit {
      font-size: 14px;
      color: #64748B;
      margin-top: 4px;
    }
  }

  .menu-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .menu-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: 8px;
    color: #64748B;
    text-decoration: none;
    transition: all 0.2s;
    cursor: pointer;
    position: relative;

    .icon {
      width: 20px;
      height: 20px;
    }

    .chat-badge {
      margin-left: auto;
    }

    &:hover {
      background: rgba(37, 99, 235, 0.1);
      color: #2563EB;
    }

    &.router-link-active {
      background: #2563EB;
      color: white;
    }
  }
}

.main-content {
  min-height: 500px;
}

@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: static;
  }
}
</style>
