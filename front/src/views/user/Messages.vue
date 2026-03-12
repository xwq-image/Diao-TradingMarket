<template>
  <div class="messages-page glass-card">
    <div class="page-header">
      <h2 class="page-title">消息通知</h2>
      <el-button v-if="unreadCount > 0" type="primary" size="small" @click="handleMarkAllRead">
        全部标记为已读
      </el-button>
    </div>

    <div v-loading="loading" class="messages-list">
      <div v-for="notification in notificationsList" :key="notification.id" 
           :class="['notification-item', { unread: !notification.isRead }]"
           @click="handleNotificationClick(notification)">
        <div class="notification-icon" :class="notification.type">
          <svg v-if="notification.type === 'order'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <svg v-else-if="notification.type === 'message'" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
          </svg>
          <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
        </div>
        <div class="notification-content">
          <div class="notification-header">
            <span class="notification-title">{{ notification.title }}</span>
            <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
          </div>
          <p class="notification-text">{{ notification.content }}</p>
        </div>
        <div v-if="!notification.isRead" class="unread-dot"></div>
      </div>

      <el-empty v-if="!loading && notificationsList.length === 0" description="暂无消息" />

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchNotifications"
        class="pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyNotifications, markNotificationAsRead, markAllNotificationsAsRead, getUnreadNotificationCount } from '@/api/notification'

const router = useRouter()
const loading = ref(false)
const notificationsList = ref([])
const total = ref(0)
const unreadCount = ref(0)

const pagination = ref({
  page: 1,
  size: 10
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return date.toLocaleDateString('zh-CN')
}

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await getMyNotifications({
      page: pagination.value.page,
      size: pagination.value.size
    })
    notificationsList.value = res.data.records
    total.value = res.data.total
    
    // 获取未读数量
    const countRes = await getUnreadNotificationCount()
    unreadCount.value = countRes.data
  } catch (error) {
    ElMessage.error('获取消息失败')
  } finally {
    loading.value = false
  }
}

const handleNotificationClick = async (notification) => {
  if (!notification.isRead) {
    try {
      await markNotificationAsRead(notification.id)
      notification.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (error) {
      console.error('标记已读失败', error)
    }
  }

  // 根据通知类型跳转
  if (notification.type === 'order' && notification.relatedId) {
    router.push('/user/orders')
  } else if (notification.type === 'message' && notification.relatedId) {
    router.push(`/goods/${notification.relatedId}`)
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllNotificationsAsRead()
    ElMessage.success('已全部标记为已读')
    fetchNotifications()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchNotifications()
})
</script>

<style lang="scss" scoped>
.messages-page {
  padding: 32px;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #1E293B;
    }
  }

  .messages-list {
    min-height: 300px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .notification-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.2s;
    position: relative;

    &:hover {
      background: rgba(255, 255, 255, 0.8);
      transform: translateX(4px);
    }

    &.unread {
      background: rgba(37, 99, 235, 0.05);
      border-left: 3px solid #2563EB;
    }

    .notification-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;

      svg {
        width: 20px;
        height: 20px;
        color: white;
      }

      &.order {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.message {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }

      &.system {
        background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
      }
    }

    .notification-content {
      flex: 1;

      .notification-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .notification-title {
          font-size: 16px;
          font-weight: 600;
          color: #1E293B;
        }

        .notification-time {
          font-size: 12px;
          color: #94A3B8;
        }
      }

      .notification-text {
        font-size: 14px;
        color: #64748B;
        line-height: 1.6;
      }
    }

    .unread-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #2563EB;
      position: absolute;
      top: 20px;
      right: 20px;
    }
  }

  .pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
