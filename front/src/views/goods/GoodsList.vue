<template>
  <div class="goods-list-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <router-link to="/" class="logo">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          <span>校园二手市场</span>
        </router-link>
        <div class="search-bar">
          <el-input v-model="searchKeyword" placeholder="搜索商品" size="large" @keyup.enter="handleSearch">
            <template #prefix>
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </template>
          </el-input>
        </div>
      </div>
    </nav>

    <div class="content-wrapper">
      <aside class="sidebar glass-card">
        <h3 class="sidebar-title">商品分类</h3>
        <div class="category-list">
          <div 
            v-for="cat in categories" 
            :key="cat.value"
            :class="['category-item', { active: filters.category === cat.value }]"
            @click="selectCategory(cat.value)"
          >
            {{ cat.label }}
          </div>
        </div>

        <h3 class="sidebar-title">价格区间</h3>
        <div class="price-filter">
          <el-input v-model="filters.minPrice" placeholder="最低价" size="small" />
          <span>-</span>
          <el-input v-model="filters.maxPrice" placeholder="最高价" size="small" />
        </div>

        <h3 class="sidebar-title">成色</h3>
        <el-radio-group v-model="filters.condition" @change="fetchGoods">
          <el-radio label="">全部</el-radio>
          <el-radio label="全新">全新</el-radio>
          <el-radio label="99新">99新</el-radio>
          <el-radio label="95新">95新</el-radio>
          <el-radio label="9成新">9成新</el-radio>
        </el-radio-group>

        <el-button type="primary" @click="fetchGoods" class="filter-btn">应用筛选</el-button>
      </aside>

      <main class="main-content">
        <div class="toolbar">
          <div class="sort-options">
            <span 
              v-for="sort in sortOptions" 
              :key="sort.value"
              :class="['sort-item', { active: filters.sortBy === sort.value }]"
              @click="selectSort(sort.value)"
            >
              {{ sort.label }}
            </span>
          </div>
        </div>

        <div v-loading="loading" class="goods-grid">
          <div 
            v-for="item in goodsList" 
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
                <span class="views">{{ item.views }}次浏览</span>
              </div>
            </div>
          </div>
        </div>

        <el-pagination
          v-if="total > 0"
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchGoods"
          class="pagination"
        />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getGoodsList } from '@/api/goods'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const searchKeyword = ref('')
const goodsList = ref([])
const total = ref(0)

const filters = reactive({
  category: '',
  minPrice: '',
  maxPrice: '',
  condition: '',
  sortBy: 'latest'
})

const pagination = reactive({
  page: 1,
  size: 12
})

const categories = [
  { label: '全部', value: '' },
  { label: '教材书籍', value: '1' },
  { label: '电子数码', value: '2' },
  { label: '生活用品', value: '3' },
  { label: '交通工具', value: '4' },
  { label: '其他', value: '5' }
]

const sortOptions = [
  { label: '最新发布', value: 'latest' },
  { label: '价格升序', value: 'price_asc' },
  { label: '价格降序', value: 'price_desc' },
  { label: '最多浏览', value: 'views' }
]

const fetchGoods = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value,
      category: filters.category,
      minPrice: filters.minPrice,
      maxPrice: filters.maxPrice,
      condition: filters.condition,
      sortBy: filters.sortBy,
      page: pagination.page,
      size: pagination.size
    }
    const res = await getGoodsList(params)
    goodsList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (value) => {
  filters.category = value
  pagination.page = 1
  fetchGoods()
}

const selectSort = (value) => {
  filters.sortBy = value
  pagination.page = 1
  fetchGoods()
}

const handleSearch = () => {
  pagination.page = 1
  fetchGoods()
}

const goToDetail = (id) => {
  router.push(`/goods/${id}`)
}

onMounted(() => {
  if (route.query.category) {
    filters.category = route.query.category
  }
  fetchGoods()
})
</script>

<style lang="scss" scoped>
.goods-list-page {
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
    display: flex;
    align-items: center;
    gap: 24px;
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
    white-space: nowrap;

    .icon {
      width: 24px;
      height: 24px;
    }
  }

  .search-bar {
    flex: 1;
    max-width: 600px;

    .icon {
      width: 18px;
      height: 18px;
      color: #64748B;
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

  .sidebar-title {
    font-size: 16px;
    font-weight: 600;
    color: #1E293B;
    margin: 24px 0 12px;

    &:first-child {
      margin-top: 0;
    }
  }

  .category-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .category-item {
    padding: 10px 16px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    color: #64748B;

    &:hover {
      background: rgba(37, 99, 235, 0.1);
      color: #2563EB;
    }

    &.active {
      background: #2563EB;
      color: white;
    }
  }

  .price-filter {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .filter-btn {
    width: 100%;
    margin-top: 24px;
  }
}

.main-content {
  .toolbar {
    margin-bottom: 24px;

    .sort-options {
      display: flex;
      gap: 24px;
    }

    .sort-item {
      color: #64748B;
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: #2563EB;
      }

      &.active {
        color: #2563EB;
        font-weight: 600;
      }
    }
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
        font-size: 12px;
        color: #64748B;
      }
    }
  }

  .pagination {
    margin-top: 40px;
    display: flex;
    justify-content: center;
  }
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
