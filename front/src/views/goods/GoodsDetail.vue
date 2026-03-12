<template>
  <div class="goods-detail-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <router-link to="/" class="back-btn">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          返回
        </router-link>
      </div>
    </nav>

    <div v-loading="loading" class="content-wrapper">
      <div class="goods-main glass-card">
        <div class="image-gallery">
          <img :src="goods.mainImage || '/placeholder.jpg'" :alt="goods.title" class="main-image" />
        </div>

        <div class="goods-info-section">
          <h1 class="goods-title">{{ goods.title }}</h1>
          <div class="price-section">
            <span class="current-price">¥{{ goods.currentPrice }}</span>
            <span class="original-price">原价 ¥{{ goods.originalPrice }}</span>
          </div>

          <div class="meta-info">
            <div class="meta-item">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              <span>{{ goods.location }}</span>
            </div>
            <div class="meta-item">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
              </svg>
              <span>{{ goods.views }}次浏览</span>
            </div>
            <div class="meta-item">
              <span class="condition-badge">{{ goods.condition }}</span>
            </div>
          </div>

          <div class="action-buttons">
            <!-- 如果是自己的商品，显示提示 -->
            <div v-if="isMyGoods" class="my-goods-tip glass-card">
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span>这是您发布的商品</span>
            </div>
            
            <!-- 如果不是自己的商品，显示操作按钮 -->
            <template v-else>
              <el-button type="primary" size="large" @click="handleContact">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                </svg>
                联系卖家
              </el-button>
              <el-button size="large" @click="handleWant">我想要</el-button>
            </template>
            
            <el-button 
              :type="isFavorited ? 'danger' : 'default'" 
              size="large" 
              @click="handleFavorite"
            >
              <svg class="icon" viewBox="0 0 24 24" :fill="isFavorited ? 'currentColor' : 'none'" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
              </svg>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="goods-description glass-card">
        <h3 class="section-title">商品描述</h3>
        <p class="description-text">{{ goods.description }}</p>
      </div>

      <div class="seller-info glass-card">
        <h3 class="section-title">卖家信息</h3>
        <div class="seller-card">
          <el-avatar :src="seller.avatar" :size="60">{{ seller.nickname?.[0] }}</el-avatar>
          <div class="seller-details">
            <div class="seller-name">{{ seller.nickname }}</div>
            <div class="seller-credit">信誉分：{{ seller.creditScore }}</div>
          </div>
        </div>
      </div>

      <div class="messages-section glass-card">
        <h3 class="section-title">留言板</h3>
        <div class="message-list">
          <div v-for="msg in messages" :key="msg.id" class="message-item">
            <el-avatar :src="msg.userAvatar" :size="40">{{ msg.userName?.[0] }}</el-avatar>
            <div class="message-content">
              <div class="message-header">
                <span class="user-name">{{ msg.userName }}</span>
                <span class="message-time">{{ msg.createTime }}</span>
              </div>
              <p class="message-text">{{ msg.content }}</p>
            </div>
          </div>
        </div>

        <div v-if="isLoggedIn" class="message-input">
          <el-input
            v-model="messageContent"
            type="textarea"
            :rows="3"
            placeholder="留言咨询..."
            maxlength="200"
            show-word-limit
          />
          <el-button type="primary" @click="handleSendMessage">发送</el-button>
        </div>
        <div v-else class="login-tip">
          <router-link to="/login">登录后可留言</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getGoodsDetail } from '@/api/goods'
import { getGoodsMessages, sendMessage } from '@/api/message'
import { createOrder } from '@/api/order'
import { checkFavorite, addFavorite, removeFavorite } from '@/api/favorite'
import { createOrGetSession } from '@/api/chat'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const goods = ref({})
const seller = ref({})
const messages = ref([])
const messageContent = ref('')
const isFavorited = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn)
const isMyGoods = computed(() => {
  return isLoggedIn.value && goods.value.userId === userStore.userInfo?.id
})

const fetchGoodsDetail = async () => {
  loading.value = true
  try {
    const res = await getGoodsDetail(route.params.id)
    goods.value = res.data
    
    // 从商品数据中获取卖家信息
    seller.value = {
      nickname: res.data.userName,
      avatar: res.data.userAvatar,
      creditScore: res.data.userCreditScore || 100
    }
    
    // 获取留言
    await fetchMessages()
    
    // 检查是否已收藏
    if (isLoggedIn.value) {
      const favoriteRes = await checkFavorite(route.params.id)
      isFavorited.value = favoriteRes.data
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败')
  } finally {
    loading.value = false
  }
}

const fetchMessages = async () => {
  try {
    const res = await getGoodsMessages(route.params.id)
    messages.value = res.data
  } catch (error) {
    console.error('获取留言失败', error)
  }
}

const handleFavorite = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    if (isFavorited.value) {
      await removeFavorite(goods.value.id)
      ElMessage.success('已取消收藏')
      isFavorited.value = false
    } else {
      await addFavorite(goods.value.id)
      ElMessage.success('收藏成功')
      isFavorited.value = true
    }
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleContact = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (goods.value.userId === userStore.user?.id) {
    ElMessage.info('这是您自己的商品')
    return
  }
  
  try {
    // 创建或获取聊天会话
    const res = await createOrGetSession(goods.value.userId, goods.value.id)
    const sessionId = res.data
    
    // 跳转到聊天页面
    router.push(`/chat/${sessionId}`)
  } catch (error) {
    ElMessage.error('打开聊天失败')
  }
}

const handleWant = async () => {
  if (!isLoggedIn.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await createOrder({ goodsId: goods.value.id })
    ElMessage.success('购买请求已发送，请等待卖家确认')
    router.push('/user/orders')
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleSendMessage = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  
  try {
    await sendMessage({
      goodsId: goods.value.id,
      content: messageContent.value
    })
    ElMessage.success('留言成功')
    messageContent.value = ''
    await fetchMessages()
  } catch (error) {
    ElMessage.error('留言失败')
  }
}

onMounted(() => {
  fetchGoodsDetail()
})
</script>

<style lang="scss" scoped>
.goods-detail-page {
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

  .back-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #1E293B;
    text-decoration: none;
    font-weight: 500;
    cursor: pointer;

    .icon {
      width: 20px;
      height: 20px;
    }

    &:hover {
      color: #2563EB;
    }
  }
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.goods-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 32px;

  .image-gallery {
    .main-image {
      width: 100%;
      height: 500px;
      object-fit: cover;
      border-radius: 12px;
    }
  }

  .goods-info-section {
    .goods-title {
      font-size: 28px;
      font-weight: 700;
      color: #1E293B;
      margin-bottom: 16px;
    }

    .price-section {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 24px;

      .current-price {
        font-size: 36px;
        font-weight: 700;
        color: #F97316;
      }

      .original-price {
        font-size: 18px;
        color: #94A3B8;
        text-decoration: line-through;
      }
    }

    .meta-info {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      margin-bottom: 32px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #64748B;
        font-size: 14px;

        .icon {
          width: 18px;
          height: 18px;
        }

        .condition-badge {
          background: #2563EB;
          color: white;
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 13px;
        }
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;

      .my-goods-tip {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 14px 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        border-radius: 12px;
        font-size: 15px;
        font-weight: 500;

        .icon {
          width: 20px;
          height: 20px;
        }
      }

      button {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;

        .icon {
          width: 18px;
          height: 18px;
        }
      }
    }
  }
}

.goods-description,
.seller-info,
.messages-section {
  padding: 32px;

  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 20px;
  }
}

.description-text {
  color: #475569;
  line-height: 1.8;
  white-space: pre-wrap;
}

.seller-card {
  display: flex;
  align-items: center;
  gap: 16px;

  .seller-details {
    .seller-name {
      font-size: 18px;
      font-weight: 600;
      color: #1E293B;
      margin-bottom: 4px;
    }

    .seller-credit {
      color: #64748B;
      font-size: 14px;
    }
  }
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;

  .message-item {
    display: flex;
    gap: 12px;

    .message-content {
      flex: 1;

      .message-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .user-name {
          font-weight: 600;
          color: #1E293B;
        }

        .message-time {
          color: #94A3B8;
          font-size: 12px;
        }
      }

      .message-text {
        color: #475569;
        line-height: 1.6;
      }
    }
  }
}

.message-input {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.login-tip {
  text-align: center;
  padding: 20px;
  color: #64748B;

  a {
    color: #2563EB;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

@media (max-width: 768px) {
  .goods-main {
    grid-template-columns: 1fr;
  }
}
</style>
