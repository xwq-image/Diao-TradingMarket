<template>
  <div class="my-goods-page glass-card">
    <div class="page-header">
      <h2 class="page-title">我的商品</h2>
      <router-link to="/goods/publish">
        <el-button type="primary">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          发布商品
        </el-button>
      </router-link>
    </div>

    <el-tabs v-model="activeTab" @tab-change="fetchGoods">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="审核中" name="pending" />
      <el-tab-pane label="已上架" name="published" />
      <el-tab-pane label="已下架" name="offline" />
      <el-tab-pane label="已售出" name="sold" />
    </el-tabs>

    <div v-loading="loading" class="goods-list">
      <div v-for="item in goodsList" :key="item.id" class="goods-item">
        <img :src="item.mainImage || '/placeholder.jpg'" :alt="item.title" class="goods-image" />
        <div class="goods-info">
          <h4 class="goods-title">{{ item.title }}</h4>
          <div class="goods-price">¥{{ item.currentPrice }}</div>
          <div class="goods-meta">
            <span :class="['status-badge', item.status]">{{ getStatusText(item.status) }}</span>
            <span class="views">{{ item.views }}次浏览</span>
          </div>
        </div>
        <div class="goods-actions">
          <el-button size="small" @click="handleEdit(item.id)">编辑</el-button>
          <el-button size="small" @click="handleToggleStatus(item)">
            {{ item.status === 'published' ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(item.id)">删除</el-button>
        </div>
      </div>

      <el-empty v-if="!loading && goodsList.length === 0" description="暂无商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyGoodsList, deleteGoods, updateGoodsStatus } from '@/api/goods'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('all')
const goodsList = ref([])

const statusMap = {
  draft: '草稿',
  pending: '审核中',
  published: '已上架',
  offline: '已下架',
  sold: '已售出'
}

const getStatusText = (status) => statusMap[status] || status

const fetchGoods = async () => {
  loading.value = true
  try {
    const params = {
      status: activeTab.value === 'all' ? '' : activeTab.value,
      page: 1,
      size: 100
    }
    const res = await getMyGoodsList(params)
    goodsList.value = res.data.records
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = (id) => {
  router.push(`/goods/edit/${id}`)
}

const handleToggleStatus = async (item) => {
  const newStatus = item.status === 'published' ? 'offline' : 'published'
  try {
    await updateGoodsStatus(item.id, newStatus)
    ElMessage.success('操作成功')
    fetchGoods()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteGoods(id)
    ElMessage.success('删除成功')
    fetchGoods()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchGoods()
})
</script>

<style lang="scss" scoped>
.my-goods-page {
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

    .icon {
      width: 16px;
      height: 16px;
    }
  }

  .goods-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    min-height: 300px;
  }

  .goods-item {
    display: flex;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.5);
    transition: all 0.2s;

    &:hover {
      background: rgba(255, 255, 255, 0.8);
    }

    .goods-image {
      width: 120px;
      height: 120px;
      object-fit: cover;
      border-radius: 8px;
    }

    .goods-info {
      flex: 1;

      .goods-title {
        font-size: 16px;
        font-weight: 600;
        color: #1E293B;
        margin-bottom: 8px;
      }

      .goods-price {
        font-size: 20px;
        font-weight: 700;
        color: #F97316;
        margin-bottom: 12px;
      }

      .goods-meta {
        display: flex;
        gap: 12px;
        font-size: 14px;

        .status-badge {
          padding: 4px 12px;
          border-radius: 12px;
          font-size: 12px;

          &.pending {
            background: #FEF3C7;
            color: #92400E;
          }

          &.published {
            background: #D1FAE5;
            color: #065F46;
          }

          &.offline {
            background: #E5E7EB;
            color: #374151;
          }

          &.sold {
            background: #DBEAFE;
            color: #1E40AF;
          }
        }

        .views {
          color: #64748B;
        }
      }
    }

    .goods-actions {
      display: flex;
      flex-direction: column;
      gap: 8px;
      justify-content: center;
    }
  }
}
</style>
