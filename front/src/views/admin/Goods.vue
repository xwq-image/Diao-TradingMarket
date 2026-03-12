<template>
  <div class="admin-goods glass-card">
    <h2 class="page-title">商品审核</h2>

    <div v-loading="loading" class="goods-list">
      <div v-for="item in goodsList" :key="item.id" class="goods-item">
        <img :src="item.mainImage || '/placeholder.jpg'" :alt="item.title" class="goods-image" />
        <div class="goods-info">
          <h4 class="goods-title">{{ item.title }}</h4>
          <div class="goods-meta">
            <span class="price">¥{{ item.currentPrice }}</span>
            <span class="category">{{ getCategoryName(item.categoryId) }}</span>
          </div>
          <p class="goods-desc">{{ item.description }}</p>
        </div>
        <div class="goods-actions">
          <el-button type="success" size="small" @click="handleApprove(item.id)">
            通过
          </el-button>
          <el-button type="danger" size="small" @click="handleReject(item.id)">
            拒绝
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && goodsList.length === 0" description="暂无待审核商品" />

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchGoods"
        class="pagination"
      />
    </div>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="4"
        placeholder="请输入拒绝原因"
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPendingGoods, approveGoods, rejectGoods } from '@/api/admin'

const loading = ref(false)
const goodsList = ref([])
const total = ref(0)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentGoodsId = ref(null)

const pagination = ref({
  page: 1,
  size: 10
})

const categoryMap = {
  1: '教材书籍',
  2: '电子数码',
  3: '生活用品',
  4: '交通工具',
  5: '其他'
}

const getCategoryName = (id) => categoryMap[id] || '未知'

const fetchGoods = async () => {
  loading.value = true
  try {
    const res = await getPendingGoods({
      page: pagination.value.page,
      size: pagination.value.size
    })
    goodsList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    await approveGoods(id)
    ElMessage.success('审核通过')
    fetchGoods()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = (id) => {
  currentGoodsId.value = id
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  try {
    await rejectGoods(currentGoodsId.value, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    fetchGoods()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchGoods()
})
</script>

<style lang="scss" scoped>
.admin-goods {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
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

      .goods-meta {
        display: flex;
        gap: 12px;
        margin-bottom: 8px;

        .price {
          font-size: 18px;
          font-weight: 700;
          color: #F97316;
        }

        .category {
          padding: 2px 8px;
          background: #E0F2FE;
          color: #0369A1;
          border-radius: 4px;
          font-size: 12px;
        }
      }

      .goods-desc {
        color: #64748B;
        font-size: 14px;
        line-height: 1.6;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }

    .goods-actions {
      display: flex;
      flex-direction: column;
      gap: 8px;
      justify-content: center;
    }
  }

  .pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
