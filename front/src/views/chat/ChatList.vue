<template>
  <div class="chat-list-page glass-card">
    <div class="page-header">
      <h2 class="page-title">我的聊天</h2>
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" type="danger">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
        </svg>
      </el-badge>
    </div>

    <div v-loading="loading" class="sessions-list">
      <div
        v-for="session in sessions"
        :key="session.id"
        :class="['session-item', { unread: session.unreadCount > 0 }]"
        @click="openChat(session.id)"
      >
        <el-avatar :src="session.otherUserAvatar" :size="50">
          {{ session.otherUserName?.[0] }}
        </el-avatar>
        
        <div class="session-content">
          <div class="session-header">
            <span class="user-name">{{ session.otherUserName }}</span>
            <span class="session-time">{{ formatTime(session.lastMessageTime) }}</span>
          </div>
          
          <div v-if="session.goodsTitle" class="goods-tag">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
            {{ session.goodsTitle }}
          </div>
          
          <div class="last-message">{{ session.lastMessage || '暂无消息' }}</div>
        </div>
        
        <el-badge v-if="session.unreadCount > 0" :value="session.unreadCount" type="danger" />
      </div>

      <el-empty v-if="!loading && sessions.length === 0" description="暂无聊天记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyChatSessions, getUnreadChatCount } from '@/api/chat'

const router = useRouter()
const loading = ref(false)
const sessions = ref([])
const unreadCount = ref(0)

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

const fetchSessions = async () => {
  loading.value = true
  try {
    const res = await getMyChatSessions()
    sessions.value = res.data
    
    // 获取未读总数
    const countRes = await getUnreadChatCount()
    unreadCount.value = countRes.data
  } catch (error) {
    ElMessage.error('获取聊天列表失败')
  } finally {
    loading.value = false
  }
}

const openChat = (sessionId) => {
  router.push(`/chat/${sessionId}`)
}

onMounted(() => {
  fetchSessions()
})
</script>

<style lang="scss" scoped>
.chat-list-page {
  padding: 32px;
  min-height: 80vh;

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

    .icon {
      width: 24px;
      height: 24px;
      color: #2563EB;
    }
  }

  .sessions-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .session-item {
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

    .session-content {
      flex: 1;
      min-width: 0;

      .session-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .user-name {
          font-size: 16px;
          font-weight: 600;
          color: #1E293B;
        }

        .session-time {
          font-size: 12px;
          color: #94A3B8;
        }
      }

      .goods-tag {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 2px 8px;
        background: rgba(37, 99, 235, 0.1);
        color: #2563EB;
        border-radius: 4px;
        font-size: 12px;
        margin-bottom: 8px;

        .icon {
          width: 12px;
          height: 12px;
        }
      }

      .last-message {
        font-size: 14px;
        color: #64748B;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>
