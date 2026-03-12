<template>
  <div class="verifications-page">
    <div class="page-header">
      <h2>实名认证审核</h2>
      <el-radio-group v-model="statusFilter" @change="fetchList">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待审核</el-radio-button>
        <el-radio-button label="APPROVED">已通过</el-radio-button>
        <el-radio-button label="REJECTED">已驳回</el-radio-button>
      </el-radio-group>
    </div>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userName" label="用户" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="studentId" label="学号" width="150" />
      <el-table-column label="学生证" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.studentCardImage"
            :src="row.studentCardImage"
            :preview-src-list="[row.studentCardImage]"
            fit="cover"
            style="width: 60px; height: 40px; border-radius: 4px; cursor: pointer;"
          />
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'PENDING'" type="warning">待审核</el-tag>
          <el-tag v-else-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
          <el-tag v-else-if="row.status === 'REJECTED'" type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            type="success"
            size="small"
            @click="handleApprove(row.id)"
          >
            通过
          </el-button>
          <el-button
            v-if="row.status === 'PENDING'"
            type="danger"
            size="small"
            @click="handleReject(row)"
          >
            驳回
          </el-button>
          <el-button size="small" @click="handleView(row)">查看详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回认证" width="500px">
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入驳回原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="认证详情" width="600px">
      <div v-if="currentItem" class="detail-content">
        <div class="detail-item">
          <span class="label">用户：</span>
          <span class="value">{{ currentItem.userName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">真实姓名：</span>
          <span class="value">{{ currentItem.realName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">学号：</span>
          <span class="value">{{ currentItem.studentId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">身份证号：</span>
          <span class="value">{{ currentItem.idCard || '未填写' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">学生证照片：</span>
          <el-image
            v-if="currentItem.studentCardImage"
            :src="currentItem.studentCardImage"
            :preview-src-list="[currentItem.studentCardImage]"
            fit="contain"
            style="width: 200px; height: 150px; border-radius: 8px; cursor: pointer;"
          />
        </div>
        <div v-if="currentItem.idCardFront" class="detail-item">
          <span class="label">身份证正面：</span>
          <el-image
            :src="currentItem.idCardFront"
            :preview-src-list="[currentItem.idCardFront]"
            fit="contain"
            style="width: 200px; height: 150px; border-radius: 8px; cursor: pointer;"
          />
        </div>
        <div v-if="currentItem.idCardBack" class="detail-item">
          <span class="label">身份证背面：</span>
          <el-image
            :src="currentItem.idCardBack"
            :preview-src-list="[currentItem.idCardBack]"
            fit="contain"
            style="width: 200px; height: 150px; border-radius: 8px; cursor: pointer;"
          />
        </div>
        <div v-if="currentItem.rejectReason" class="detail-item">
          <span class="label">驳回原因：</span>
          <span class="value">{{ currentItem.rejectReason }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVerificationList, approveVerification, rejectVerification } from '@/api/verification'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('PENDING')

const rejectDialogVisible = ref(false)
const rejectForm = ref({
  id: null,
  reason: ''
})

const detailDialogVisible = ref(false)
const currentItem = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getVerificationList(statusFilter.value)
    list.value = res.data
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = async (id) => {
  try {
    await ElMessageBox.confirm('确认通过该认证申请？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await approveVerification(id)
    ElMessage.success('已通过认证')
    fetchList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleReject = (row) => {
  rejectForm.value.id = row.id
  rejectForm.value.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.reason) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  
  try {
    await rejectVerification(rejectForm.value.id, rejectForm.value.reason)
    ElMessage.success('已驳回认证')
    rejectDialogVisible.value = false
    fetchList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleView = (row) => {
  currentItem.value = row
  detailDialogVisible.value = true
}

onMounted(() => {
  fetchList()
})
</script>

<style lang="scss" scoped>
.verifications-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    h2 {
      font-size: 20px;
      font-weight: 600;
      color: #1E293B;
    }
  }
}

.detail-content {
  .detail-item {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #F1F5F9;

    &:last-child {
      border-bottom: none;
    }

    .label {
      width: 120px;
      color: #64748B;
      font-size: 14px;
      flex-shrink: 0;
    }

    .value {
      flex: 1;
      color: #1E293B;
      font-size: 14px;
    }
  }
}
</style>
