<template>
  <div class="my-favorites-page glass-card">
    <h2 class="page-title">我的收藏</h2>

    <div v-loading="loading" class="goods-grid">
      <div 
        v-for="item in favoritesList" 
        :key="item.id"
        class="goods-card glass-card"
        @click="goToDetail(item.id)"
      >
        <div class="goods-image">
          <img :src="item.mainImage || '/placeholder.jpg'" :alt="item.title" />
          <span v-if="item.condition" class="condition-tag">{{ item.condition }}</span>
        </div>
        <div class="goods-info">
          <h4 class="goods-title">{{ item.title }}</h4>
          <div class="goods-meta">
            <span class="price">¥{{ item.currentPrice }}</span>
            <span class="original-price">¥{{ item.originalPrice }}</span>
          </div>
          <div class="goods-footer">
            <span class="location">{{ item.location }}</span>
            <el-button 
              type="danger" 
              size="small" 
              @click.stop="handleRemoveFavorite(item.id)"
            >
              取消收藏
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && favoritesList.length === 0" description="暂无收藏" />
    </div>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="total"
      layout="prev, pager, next"
      @current-change="fetchFavorites"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyFavorites, removeFavorite } from '@/api/favorite'

const router = useRouter()
const loading = ref(false)
const favoritesList = ref([])
const total = ref(0)

const pagination = ref({
  page: 1,
  size: 12
})

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getMyFavorites({
      page: pagination.value.page,
      size: pagination.value.size
    })
    favoritesList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleRemoveFavorite = async (goodsId) => {
  try {
    await removeFavorite(goodsId)
    ElMessage.success('已取消收藏')
    fetchFavorites()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const goToDetail = (id) => {
  router.push(`/goods/${id}`)
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.my-favorites-page {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }

  .goods-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 20px;
    min-height: 400px;
  }

  .goods-card {
    cursor: pointer;
    overflow: hidden;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-4px);
    }

    .goods-image {
      position: relative;
      width: 100%;
      height: 200px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .condition-tag {
        position: absolute;
        top: 8px;
        right: 8px;
        background: rgba(37, 99, 235, 0.9);
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
      }
    }

    .goods-info {
      padding: 16px;

      .goods-title {
        font-size: 16px;
        font-weight: 600;
        color: #1E293B;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .goods-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;

        .price {
          font-size: 20px;
          font-weight: 700;
          color: #F97316;
        }

        .original-price {
          font-size: 14px;
          color: #94A3B8;
          text-decoration: line-through;
        }
      }

      .goods-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 12px;

        .location {
          color: #64748B;
        }
      }
    }
  }

  .pagination {
    margin-top: 24px;
    display: flex;
    justify-content: center;
  }
}
</style>
