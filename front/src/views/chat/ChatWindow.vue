<template>
  <div class="chat-window-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <el-button @click="router.back()" text>
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          返回
        </el-button>
        <div v-if="sessionInfo" class="chat-header">
          <el-avatar :src="sessionInfo.otherUserAvatar" :size="32">
            {{ sessionInfo.otherUserName?.[0] }}
          </el-avatar>
          <span class="user-name">{{ sessionInfo.otherUserName }}</span>
        </div>
      </div>
    </nav>

    <div class="chat-container">
      <!-- 商品信息卡片 -->
      <div v-if="sessionInfo?.goodsId" class="goods-card glass-card" @click="goToGoods">
        <img :src="sessionInfo.goodsImage" :alt="sessionInfo.goodsTitle" class="goods-image" />
        <div class="goods-info">
          <div class="goods-title">{{ sessionInfo.goodsTitle }}</div>
          <div class="goods-action">查看商品 →</div>
        </div>
      </div>

      <!-- 消息列表 -->
      <div ref="messagesContainer" class="messages-container" v-loading="loading">
        <div v-for="msg in messages" :key="msg.id" :class="['message-item', { mine: msg.isMine }]">
          <el-avatar v-if="!msg.isMine" :src="msg.senderAvatar" :size="36">
            {{ msg.senderName?.[0] }}
          </el-avatar>
          <div class="message-bubble">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
          <el-avatar v-if="msg.isMine" :src="msg.senderAvatar" :size="36">
            {{ msg.senderName?.[0] }}
          </el-avatar>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-container glass-card">
        <el-input
          v-model="messageInput"
          type="textarea"
          :rows="3"
          placeholder="输入消息..."
          maxlength="500"
          show-word-limit
          @keydown.enter.ctrl="handleSend"
        />
        <el-button type="primary" @click="handleSend" :disabled="!messageInput.trim()">
          发送 (Ctrl+Enter)
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSessionMessages, sendChatMessage, markChatMessagesAsRead, getMyChatSessions } from '@/api/chat'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const messages = ref([])
const messageInput = ref('')
const messagesContainer = ref(null)
const sessionInfo = ref(null)

const sessionId = route.params.id

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  
  return date.toLocaleString('zh-CN', { month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const fetchMessages = async () => {
  loading.value = true
  try {
    const res = await getSessionMessages(sessionId)
    messages.value = res.data
    scrollToBottom()
    
    // 标记为已读
    await markChatMessagesAsRead(sessionId)
  } catch (error) {
    ElMessage.error('获取消息失败')
  } finally {
    loading.value = false
  }
}

const fetchSessionInfo = async () => {
  try {
    const res = await getMyChatSessions()
    const session = res.data.find(s => s.id == sessionId)
    if (session) {
      sessionInfo.value = session
    }
  } catch (error) {
    console.error('获取会话信息失败', error)
  }
}

const handleSend = async () => {
  if (!messageInput.value.trim()) return
  
  try {
    await sendChatMessage({
      sessionId: Number(sessionId),
      content: messageInput.value.trim()
    })
    
    messageInput.value = ''
    await fetchMessages()
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

const goToGoods = () => {
  if (sessionInfo.value?.goodsId) {
    router.push(`/goods/${sessionInfo.value.goodsId}`)
  }
}

onMounted(() => {
  fetchSessionInfo()
  fetchMessages()
  
  // 定时刷新消息（可选）
  const interval = setInterval(fetchMessages, 5000)
  
  // 组件卸载时清除定时器
  return () => clearInterval(interval)
})
</script>

<style lang="scss" scoped>
.chat-window-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 16px 24px;

  .nav-container {
    display: flex;
    align-items: center;
    gap: 16px;
    max-width: 1200px;
    margin: 0 auto;

    .icon {
      width: 20px;
      height: 20px;
    }

    .chat-header {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-name {
        font-size: 16px;
        font-weight: 600;
        color: #1E293B;
      }
    }
  }
}

.chat-container {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.goods-card {
  display: flex;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
  }

  .goods-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 8px;
  }

  .goods-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .goods-title {
      font-size: 14px;
      font-weight: 600;
      color: #1E293B;
      margin-bottom: 4px;
    }

    .goods-action {
      font-size: 12px;
      color: #2563EB;
    }
  }
}

.messages-container {
  flex: 1;
  min-height: 400px;
  max-height: calc(100vh - 400px);
  overflow-y: auto;
  padding: 16px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  .message-item {
    display: flex;
    gap: 12px;
    align-items: flex-start;

    &.mine {
      flex-direction: row-reverse;

      .message-bubble {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;

        .message-time {
          color: rgba(255, 255, 255, 0.8);
        }
      }
    }

    .message-bubble {
      max-width: 60%;
      padding: 12px 16px;
      border-radius: 12px;
      background: white;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

      .message-content {
        font-size: 14px;
        line-height: 1.6;
        word-wrap: break-word;
        white-space: pre-wrap;
      }

      .message-time {
        font-size: 11px;
        color: #94A3B8;
        margin-top: 4px;
      }
    }
  }
}

.input-container {
  padding: 16px;
  display: flex;
  gap: 12px;
  align-items: flex-end;

  :deep(.el-textarea) {
    flex: 1;
  }
}
</style>
