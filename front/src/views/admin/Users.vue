<template>
  <div class="admin-users glass-card">
    <h2 class="page-title">用户管理</h2>

    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索用户（昵称/手机号/学号）"
        clearable
        @keyup.enter="fetchUsers"
      >
        <template #append>
          <el-button @click="fetchUsers">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </el-button>
        </template>
      </el-input>
    </div>

    <el-table v-loading="loading" :data="usersList" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="studentId" label="学号" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="campus" label="校区" width="100" />
      <el-table-column prop="creditScore" label="信誉分" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="认证状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.role === 'admin'" type="info">管理员</el-tag>
          <el-tag v-else :type="getVerifyType(row)">
            {{ getVerifyText(row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" fixed="right" width="200">
        <template #default="{ row }">
          <el-button
            size="small"
            :type="row.status === 1 ? 'danger' : 'success'"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button
            v-if="row.role !== 'admin' && row.verified === 0"
            size="small"
            type="primary"
            @click="handleVerify(row.id)"
          >
            通过认证
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="fetchUsers"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus, verifyUser } from '@/api/admin'

const loading = ref(false)
const searchKeyword = ref('')
const usersList = ref([])
const total = ref(0)

const pagination = ref({
  page: 1,
  size: 10
})

const getVerifyType = (row) => {
  // 管理员不需要认证
  if (row.role === 'admin') return 'info'
  
  // 根据 verified 字段判断（对应数据库的 is_verified: 0未认证 1已认证）
  if (row.verified === 1) return 'success'
  return 'warning'
}

const getVerifyText = (row) => {
  // 管理员不需要认证
  if (row.role === 'admin') return '管理员'
  
  // 根据 verified 字段判断（对应数据库的 is_verified: 0未认证 1已认证）
  if (row.verified === 1) return '已认证'
  return '未认证'
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.value.page,
      size: pagination.value.size,
      keyword: searchKeyword.value
    })
    usersList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(`确定${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleVerify = async (id) => {
  try {
    await verifyUser(id, 'approved')  // 传递 'approved' 字符串
    ElMessage.success('认证成功')
    fetchUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.admin-users {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }

  .search-bar {
    margin-bottom: 20px;

    .icon {
      width: 18px;
      height: 18px;
    }
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>
