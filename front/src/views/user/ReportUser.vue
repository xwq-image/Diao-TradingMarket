<template>
  <div class="report-page glass-card">
    <h2 class="page-title">举报用户</h2>

    <!-- 举报表单 -->
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="report-form"
    >
      <el-form-item label="被举报人" prop="reportedId">
        <el-input
          v-model="form.reportedId"
          placeholder="请输入被举报人ID"
          type="number"
        />
        <div class="form-tip">提示：可以在用户个人主页查看用户ID</div>
      </el-form-item>

      <el-form-item label="举报类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio label="USER">举报用户</el-radio>
          <el-radio label="GOODS">举报商品</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="form.type === 'GOODS'" label="商品ID" prop="targetId">
        <el-input
          v-model="form.targetId"
          placeholder="请输入商品ID"
          type="number"
        />
      </el-form-item>

      <el-form-item label="举报原因" prop="reason">
        <el-select v-model="form.reason" placeholder="请选择举报原因">
          <el-option label="虚假信息" value="虚假信息" />
          <el-option label="欺诈行为" value="欺诈行为" />
          <el-option label="恶意骚扰" value="恶意骚扰" />
          <el-option label="违规交易" value="违规交易" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>

      <el-form-item label="详细描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述举报原因..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="证据材料">
        <el-upload
          v-model:file-list="fileList"
          :action="uploadUrl"
          :headers="uploadHeaders"
          list-type="picture-card"
          :on-success="handleUploadSuccess"
          :on-remove="handleRemove"
          accept="image/*"
        >
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
        </el-upload>
        <div class="form-tip">请上传相关证据截图，最多5张</div>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
          提交举报
        </el-button>
        <el-button size="large" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 我的举报记录 -->
    <div class="my-reports-section">
      <h3 class="section-title">我的举报记录</h3>
      <el-table v-loading="loading" :data="myReports" stripe>
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
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { submitReport, getMyReports } from '@/api/report'

const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const myReports = ref([])
const fileList = ref([])

const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/file/upload'
const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

const form = ref({
  reportedId: '',
  type: 'USER',
  targetId: '',
  reason: '',
  description: '',
  evidenceImages: []
})

const rules = {
  reportedId: [
    { required: true, message: '请输入被举报人ID', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择举报类型', trigger: 'change' }
  ],
  reason: [
    { required: true, message: '请选择举报原因', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' }
  ]
}

const fetchMyReports = async () => {
  loading.value = true
  try {
    const res = await getMyReports()
    myReports.value = res.data
  } catch (error) {
    console.error('获取举报记录失败', error)
  } finally {
    loading.value = false
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    form.value.evidenceImages.push(response.data)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleRemove = (file) => {
  const index = form.value.evidenceImages.indexOf(file.response?.data)
  if (index > -1) {
    form.value.evidenceImages.splice(index, 1)
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  submitting.value = true
  try {
    await submitReport(form.value)
    ElMessage.success('举报已提交，等待管理员审核')
    handleReset()
    await fetchMyReports()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
  form.value.evidenceImages = []
  fileList.value = []
}

onMounted(() => {
  fetchMyReports()
})
</script>

<style lang="scss" scoped>
.report-page {
  padding: 32px;
  max-width: 1000px;
  margin: 0 auto;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }
}

.report-form {
  margin-bottom: 48px;

  .form-tip {
    font-size: 12px;
    color: #64748B;
    margin-top: 4px;
  }

  .icon {
    width: 24px;
    height: 24px;
  }
}

.my-reports-section {
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 16px;
  }
}
</style>
