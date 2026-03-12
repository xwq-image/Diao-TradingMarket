<template>
  <div class="profile-page glass-card">
    <h2 class="page-title">个人资料</h2>
    
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="头像">
        <el-upload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :auto-upload="false"
          :on-change="handleAvatarChange"
        >
          <el-avatar v-if="form.avatar" :src="form.avatar" :size="100" />
          <div v-else class="avatar-placeholder">
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </div>
        </el-upload>
      </el-form-item>

      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称" />
      </el-form-item>

      <el-form-item label="学号">
        <el-input v-model="form.studentId" disabled />
      </el-form-item>

      <el-form-item label="手机号">
        <el-input v-model="form.phone" disabled />
      </el-form-item>

      <el-form-item label="校区" prop="campus">
        <el-input v-model="form.campus" placeholder="请输入校区" />
      </el-form-item>

      <el-form-item label="宿舍楼" prop="dormitory">
        <el-input v-model="form.dormitory" placeholder="请输入宿舍楼" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">保存</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateUserInfo } from '@/api/user'

const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = ref({
  avatar: '',
  nickname: '',
  studentId: '',
  phone: '',
  campus: '',
  dormitory: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度2-20字符', trigger: 'blur' }
  ]
}

const handleAvatarChange = (file) => {
  console.log('头像上传', file)
}

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await updateUserInfo(form.value)
    await userStore.fetchUserInfo()
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    form.value = { ...userInfo }
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 32px;
  }

  .avatar-uploader {
    .avatar-placeholder {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      border: 2px dashed #D1D5DB;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      transition: all 0.2s;

      .icon {
        width: 32px;
        height: 32px;
        color: #9CA3AF;
      }

      &:hover {
        border-color: #2563EB;

        .icon {
          color: #2563EB;
        }
      }
    }
  }
}
</style>
