<template>
  <div class="admin-statistics glass-card">
    <h2 class="page-title">数据统计</h2>

    <div v-loading="loading" class="statistics-content">
      <!-- 概览卡片 -->
      <div class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon user">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">总用户数</div>
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-sub">近7天新增: {{ stats.newUsersLast7Days || 0 }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon goods">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">总商品数</div>
            <div class="stat-value">{{ stats.totalGoods || 0 }}</div>
            <div class="stat-sub">已上架: {{ stats.publishedGoods || 0 }} | 待审核: {{ stats.pendingGoods || 0 }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon order">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">总订单数</div>
            <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            <div class="stat-sub">已完成: {{ stats.completedOrders || 0 }}</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon rate">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-label">交易成功率</div>
            <div class="stat-value">{{ (stats.successRate || 0).toFixed(1) }}%</div>
          </div>
        </div>
      </div>

      <!-- 商品分类统计 -->
      <div class="category-stats">
        <h3 class="section-title">商品分类统计</h3>
        <div class="category-list">
          <div v-for="(count, category) in stats.categoryStats" :key="category" class="category-item">
            <span class="category-name">{{ category }}</span>
            <div class="category-bar">
              <div class="category-progress" :style="{ width: getCategoryPercent(count) + '%' }"></div>
            </div>
            <span class="category-count">{{ count }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStatistics } from '@/api/admin'

const loading = ref(false)
const stats = ref({})

const maxCategoryCount = computed(() => {
  if (!stats.value.categoryStats) return 1
  return Math.max(...Object.values(stats.value.categoryStats), 1)
})

const getCategoryPercent = (count) => {
  return (count / maxCategoryCount.value) * 100
}

const fetchStatistics = async () => {
  loading.value = true
  try {
    const res = await getStatistics()
    stats.value = res.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style lang="scss" scoped>
.admin-statistics {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 32px;
  }

  .statistics-content {
    min-height: 400px;
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 32px;
  }

  .stat-card {
    display: flex;
    gap: 16px;
    padding: 24px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 12px;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;

      svg {
        width: 28px;
        height: 28px;
        color: white;
      }

      &.user {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.goods {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.order {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.rate {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    .stat-info {
      flex: 1;

      .stat-label {
        font-size: 14px;
        color: #64748B;
        margin-bottom: 8px;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #1E293B;
        margin-bottom: 4px;
      }

      .stat-sub {
        font-size: 12px;
        color: #94A3B8;
      }
    }
  }

  .category-stats {
    background: rgba(255, 255, 255, 0.6);
    border-radius: 12px;
    padding: 24px;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #1E293B;
      margin-bottom: 20px;
    }

    .category-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .category-item {
      display: grid;
      grid-template-columns: 100px 1fr 60px;
      align-items: center;
      gap: 16px;

      .category-name {
        font-size: 14px;
        color: #475569;
      }

      .category-bar {
        height: 24px;
        background: #E2E8F0;
        border-radius: 12px;
        overflow: hidden;

        .category-progress {
          height: 100%;
          background: linear-gradient(90deg, #2563EB 0%, #3B82F6 100%);
          transition: width 0.3s ease;
        }
      }

      .category-count {
        font-size: 16px;
        font-weight: 600;
        color: #1E293B;
        text-align: right;
      }
    }
  }
}
</style>
