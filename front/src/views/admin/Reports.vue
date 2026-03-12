<template>
  <div class="reports-page">
    <div class="page-header">
      <h2>举报审核</h2>
      <el-radio-group v-model="statusFilter" @change="fetchList">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PENDING">待处理</el-radio-button>
        <el-radio-button label="APPROVED">已通过</el-radio-button>
        <el-radio-button label="REJECTED">已驳回</el-radio-button>
      </el-radio-group>
    </div>

    <el-table v-loading="loading" :data="list" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="reporterName" label="举报人" width="120" />
      <el-table-column prop="reportedName" label="被举报人" width="120" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'USER'" type="info">用户</el-tag>
          <el-tag v-else type="warning">商品</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="原因" width="120" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'PENDING'" type="warning">待处理</el-tag>
          <el-tag v-else-if="row.status === 'APPROVED'" type="success">已通过</el-tag>
          <el-tag v-else type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="举报时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'PENDING'"
            type="success"
            size="small"
            @click="handleApprove(row)"
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

    <!-- 通过对话框 -->
    <el-dialog v-model="approveDialogVisible" title="通过举报" width="500px">
      <el-form :model="approveForm" label-width="100px">
        <el-form-item label="扣除信誉分">
          <el-input-number
            v-model="approveForm.creditDeduction"
            :min="1"
            :max="100"
            :step="5"
          />
        </el-form-item>
        <el-form-item label="管理员备注">
          <el-input
            v-model="approveForm.adminNote"
            type="textarea"
            :rows="3"
            placeholder="请输入备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApprove">确定</el-button>
      </template>
    </el-dialog>

    <!-- 驳回对话框 -->
    <el-dialog v-model="rejectDialogVisible" title="驳回举报" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="驳回原因">
          <el-input
            v-model="rejectForm.adminNote"
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
    <el-dialog v-model="detailDialogVisible" title="举报详情" width="700px">
      <div v-if="currentItem" class="detail-content">
        <div class="detail-item">
          <span class="label">举报人：</span>
          <span class="value">{{ currentItem.reporterName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">被举报人：</span>
          <span class="value">{{ currentItem.reportedName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报类型：</span>
          <span class="value">{{ currentItem.type === 'USER' ? '用户' : '商品' }}</span>
        </div>
        <div class="detail-item">
          <span class="label">举报原因：</span>
          <span class="value">{{ currentItem.reason }}</span>
        </div>
        <div class="detail-item">
          <span class="label">详细描述：</span>
          <span class="value">{{ currentItem.description }}</span>
        </div>
        <div v-if="currentItem.evidenceImages && currentItem.evidenceImages.length > 0" class="detail-item">
          <span class="label">证据材料：</span>
          <div class="evidence-images">
            <el-image
              v-for="(img, index) in currentItem.evidenceImages"
              :key="index"
              :src="img"
              :preview-src-list="currentItem.evidenceImages"
              fit="cover"
              class="evidence-image"
            />
          </div>
        </div>
        <div v-if="currentItem.adminNote" class="detail-item">
          <span class="label">管理员备注：</span>
          <span class="value">{{ currentItem.adminNote }}</span>
        </div>
        <div v-if="currentItem.creditDeduction" class="detail-item">
          <span class="label">扣除信誉分：</span>
          <span class="value">{{ currentItem.creditDeduction }}分</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReportList, approveReport, rejectReport } from '@/api/report'

const loading = ref(false)
const list = ref([])
const statusFilter = ref('PENDING')

const approveDialogVisible = ref(false)
const approveForm = ref({
  id: null,
  creditDeduction: 10,
  adminNote: ''
})

const rejectDialogVisible = ref(false)
const rejectForm = ref({
  id: null,
  adminNote: ''
})

const detailDialogVisible = ref(false)
const currentItem = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getReportList(statusFilter.value)
    list.value = res.data
  } catch (error) {
    ElMessage.error('获取列表失败')
  } finally {
    loading.value = false
  }
}

const handleApprove = (row) => {
  approveForm.value.id = row.id
  approveForm.value.creditDeduction = 10
  approveForm.value.adminNote = ''
  approveDialogVisible.value = true
}

const confirmApprove = async () => {
  try {
    await approveReport(
      approveForm.value.id,
      approveForm.value.creditDeduction,
      approveForm.value.adminNote
    )
    ElMessage.success('已通过举报')
    approveDialogVisible.value = false
    fetchList()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleReject = (row) => {
  rejectForm.value.id = row.id
  rejectForm.value.adminNote = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.value.adminNote) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  
  try {
    await rejectReport(rejectForm.value.id, rejectForm.value.adminNote)
    ElMessage.success('已驳回举报')
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
.reports-page {
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

  .evidence-images {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;

    .evidence-image {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      cursor: pointer;
    }
  }
}
</style>
