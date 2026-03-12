<template>
  <div class="admin-layout">
    <div class="admin-sidebar glass-card">
      <h2 class="admin-title">管理后台</h2>
      <div class="menu-list">
        <router-link to="/admin/users" class="menu-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
          </svg>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/goods" class="menu-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
          </svg>
          <span>商品审核</span>
        </router-link>
        <router-link to="/admin/verifications" class="menu-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
          </svg>
          <span>认证审核</span>
        </router-link>
        <router-link to="/admin/reports" class="menu-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
          <span>举报审核</span>
        </router-link>
        <router-link to="/admin/statistics" class="menu-item">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
          </svg>
          <span>数据统计</span>
        </router-link>
      </div>
      
      <div class="sidebar-footer">
        <button class="logout-btn" @click="handleLogout">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          <span>退出登录</span>
        </button>
      </div>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
  padding: 24px;
}

.admin-sidebar {
  padding: 24px;
  height: fit-content;
  position: sticky;
  top: 24px;
  display: flex;
  flex-direction: column;

  .admin-title {
    font-size: 20px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }

  .menu-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    flex: 1;
  }

  .menu-item {
    padding: 12px 16px;
    border-radius: 8px;
    color: #64748B;
    text-decoration: none;
    transition: all 0.2s;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 12px;

    .icon {
      width: 20px;
      height: 20px;
      flex-shrink: 0;
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

  .sidebar-footer {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid rgba(0, 0, 0, 0.06);
  }

  .logout-btn {
    width: 100%;
    padding: 12px 16px;
    border: none;
    border-radius: 8px;
    background: transparent;
    color: #EF4444;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 12px;

    .icon {
      width: 20px;
      height: 20px;
      flex-shrink: 0;
    }

    &:hover {
      background: rgba(239, 68, 68, 0.1);
    }
  }
}

@media (max-width: 1024px) {
  .admin-layout {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    position: static;
  }
}
</style>
