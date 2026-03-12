<template>
  <div class="verification-page glass-card">
    <h2 class="page-title">实名认证</h2>

    <!-- 认证状态显示 -->
    <div v-if="verification" class="status-card glass-card">
      <div v-if="verification.status === 'PENDING'" class="status-pending">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div class="status-text">
          <h3>认证审核中</h3>
          <p>您的认证申请正在审核中，请耐心等待</p>
        </div>
      </div>

      <div v-else-if="verification.status === 'APPROVED'" class="status-approved">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div class="status-text">
          <h3>认证已通过</h3>
          <p>恭喜您，已完成实名认证</p>
        </div>
      </div>

      <div v-else-if="verification.status === 'REJECTED'" class="status-rejected">
        <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div class="status-text">
          <h3>认证已驳回</h3>
          <p>{{ verification.rejectReason || '认证材料不符合要求' }}</p>
          <el-button type="primary" @click="resetForm">重新申请</el-button>
        </div>
      </div>

      <div class="verification-info">
        <div class="info-item">
          <span class="label">真实姓名：</span>
          <span class="value">{{ verification.realName }}</span>
        </div>
        <div class="info-item">
          <span class="label">学号：</span>
          <span class="value">{{ verification.studentId }}</span>
        </div>
        <div class="info-item">
          <span class="label">申请时间：</span>
          <span class="value">{{ verification.createTime }}</span>
        </div>
      </div>
    </div>

    <!-- 认证申请表单 -->
    <el-form
      v-else
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="verification-form"
    >
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入真实姓名" />
      </el-form-item>

      <el-form-item label="学号" prop="studentId">
        <el-input v-model="form.studentId" placeholder="请输入学号" />
      </el-form-item>

      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="form.idCard" placeholder="请输入身份证号（选填）" />
      </el-form-item>

      <el-form-item label="学生证照片" prop="studentCardImage">
        <el-upload
          class="upload-box"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleStudentCardSuccess"
          accept="image/*"
        >
          <img v-if="form.studentCardImage" :src="form.studentCardImage" class="preview-image" />
          <div v-else class="upload-placeholder">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>上传学生证照片</span>
          </div>
        </el-upload>
      </el-form-item>

      <el-form-item label="身份证正面" prop="idCardFront">
        <el-upload
          class="upload-box"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleIdCardFrontSuccess"
          accept="image/*"
        >
          <img v-if="form.idCardFront" :src="form.idCardFront" class="preview-image" />
          <div v-else class="upload-placeholder">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>上传身份证正面（选填）</span>
          </div>
        </el-upload>
      </el-form-item>

      <el-form-item label="身份证背面" prop="idCardBack">
        <el-upload
          class="upload-box"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleIdCardBackSuccess"
          accept="image/*"
        >
          <img v-if="form.idCardBack" :src="form.idCardBack" class="preview-image" />
          <div v-else class="upload-placeholder">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
            <span>上传身份证背面（选填）</span>
          </div>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
          提交认证申请
        </el-button>
      </el-form-item>
    </el-form>

    <div class="tips glass-card">
      <h4>认证说明</h4>
      <ul>
        <li>实名认证后才能发布商品</li>
        <li>请确保上传的照片清晰可见</li>
        <li>认证信息仅用于平台审核，不会泄露给其他用户</li>
        <li>认证通过后，您的信誉度将得到提升</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { applyVerification, getMyVerification } from '@/api/verification'

const formRef = ref(null)
const verification = ref(null)
const submitting = ref(false)

const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/file/upload'
const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('token')
}

const form = ref({
  realName: '',
  studentId: '',
  idCard: '',
  studentCardImage: '',
  idCardFront: '',
  idCardBack: ''
})

const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  studentCardImage: [
    { required: true, message: '请上传学生证照片', trigger: 'change' }
  ]
}

const fetchVerification = async () => {
  try {
    const res = await getMyVerification()
    verification.value = res.data
  } catch (error) {
    console.error('获取认证信息失败', error)
  }
}

const handleStudentCardSuccess = (response) => {
  if (response.code === 200) {
    form.value.studentCardImage = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleIdCardFrontSuccess = (response) => {
  if (response.code === 200) {
    form.value.idCardFront = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleIdCardBackSuccess = (response) => {
  if (response.code === 200) {
    form.value.idCardBack = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  
  submitting.value = true
  try {
    await applyVerification(form.value)
    ElMessage.success('认证申请已提交，请等待审核')
    await fetchVerification()
  } catch (error) {
    ElMessage.error(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  verification.value = null
  form.value = {
    realName: '',
    studentId: '',
    idCard: '',
    studentCardImage: '',
    idCardFront: '',
    idCardBack: ''
  }
}

onMounted(() => {
  fetchVerification()
})
</script>

<style lang="scss" scoped>
.verification-page {
  padding: 32px;
  max-width: 800px;
  margin: 0 auto;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }
}

.status-card {
  padding: 24px;
  margin-bottom: 24px;

  .status-pending,
  .status-approved,
  .status-rejected {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 20px;
    border-radius: 12px;
    margin-bottom: 20px;

    .icon {
      width: 48px;
      height: 48px;
      flex-shrink: 0;
    }

    .status-text {
      flex: 1;

      h3 {
        font-size: 18px;
        font-weight: 600;
        margin-bottom: 4px;
      }

      p {
        font-size: 14px;
        color: #64748B;
        margin-bottom: 12px;
      }
    }
  }

  .status-pending {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;

    .status-text p {
      color: rgba(255, 255, 255, 0.9);
    }
  }

  .status-approved {
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    color: white;

    .status-text p {
      color: rgba(255, 255, 255, 0.9);
    }
  }

  .status-rejected {
    background: linear-gradient(135deg, #ee0979 0%, #ff6a00 100%);
    color: white;

    .status-text p {
      color: rgba(255, 255, 255, 0.9);
    }
  }

  .verification-info {
    .info-item {
      display: flex;
      padding: 12px 0;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);

      &:last-child {
        border-bottom: none;
      }

      .label {
        width: 100px;
        color: #64748B;
        font-size: 14px;
      }

      .value {
        flex: 1;
        color: #1E293B;
        font-size: 14px;
      }
    }
  }
}

.verification-form {
  margin-bottom: 24px;

  .upload-box {
    width: 200px;
    height: 150px;
    border: 2px dashed #D1D5DB;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      border-color: #2563EB;
    }

    .preview-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .upload-placeholder {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;
      color: #9CA3AF;

      .icon {
        width: 32px;
        height: 32px;
      }

      span {
        font-size: 14px;
      }
    }
  }
}

.tips {
  padding: 20px;
  background: rgba(37, 99, 235, 0.05);

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 12px;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      padding: 8px 0;
      padding-left: 20px;
      position: relative;
      color: #64748B;
      font-size: 14px;

      &::before {
        content: '•';
        position: absolute;
        left: 0;
        color: #2563EB;
        font-weight: bold;
      }
    }
  }
}
</style>
