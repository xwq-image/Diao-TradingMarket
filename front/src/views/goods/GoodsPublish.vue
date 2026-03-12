<template>
  <div class="publish-page">
    <nav class="navbar glass-card">
      <div class="nav-container">
        <router-link to="/" class="back-btn">
          <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
          </svg>
          返回
        </router-link>
        <h2>发布商品</h2>
      </div>
    </nav>

    <div class="content-wrapper">
      <div class="publish-form glass-card">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="商品标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入商品标题（2-50字）" maxlength="50" show-word-limit />
          </el-form-item>

          <el-form-item label="商品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类">
              <el-option label="教材书籍" :value="1" />
              <el-option label="电子数码" :value="2" />
              <el-option label="生活用品" :value="3" />
              <el-option label="交通工具" :value="4" />
              <el-option label="其他" :value="5" />
            </el-select>
          </el-form-item>

          <el-form-item label="原价" prop="originalPrice">
            <el-input-number v-model="form.originalPrice" :min="0.01" :precision="2" :step="1" />
          </el-form-item>

          <el-form-item label="现价" prop="currentPrice">
            <el-input-number v-model="form.currentPrice" :min="0.01" :precision="2" :step="1" />
          </el-form-item>

          <el-form-item label="成色" prop="condition">
            <el-radio-group v-model="form.condition">
              <el-radio label="全新">全新</el-radio>
              <el-radio label="99新">99新</el-radio>
              <el-radio label="95新">95新</el-radio>
              <el-radio label="9成新">9成新</el-radio>
              <el-radio label="8成新及以下">8成新及以下</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="交易地点" prop="location">
            <el-input v-model="form.location" placeholder="如：南校区/宿舍楼/快递" />
          </el-form-item>

          <el-form-item label="商品图片" prop="images">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="5"
              :on-change="handleImageChange"
            >
              <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
            </el-upload>
            <div class="tip">最多上传5张图片，第一张为主图</div>
          </el-form-item>

          <el-form-item label="商品描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="请详细描述商品信息（最多500字）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
              发布商品
            </el-button>
            <el-button size="large" @click="handleCancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishGoods } from '@/api/goods'
import { uploadMultipleFiles } from '@/api/file'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const uploadedImages = ref([])

const form = ref({
  title: '',
  categoryId: null,
  originalPrice: null,
  currentPrice: null,
  condition: '',
  location: '',
  images: '',
  mainImage: '',
  description: ''
})

const rules = {
  title: [
    { required: true, message: '请输入商品标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度2-50字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  originalPrice: [
    { required: true, message: '请输入原价', trigger: 'blur' }
  ],
  currentPrice: [
    { required: true, message: '请输入现价', trigger: 'blur' }
  ],
  condition: [
    { required: true, message: '请选择成色', trigger: 'change' }
  ],
  location: [
    { required: true, message: '请输入交易地点', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' }
  ]
}

const handleImageChange = async (file, fileList) => {
  if (fileList.length > 5) {
    ElMessage.warning('最多只能上传5张图片')
    fileList.pop()
    return
  }

  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.raw.type)) {
    ElMessage.error('只能上传图片文件')
    fileList.pop()
    return
  }

  // 验证文件大小
  if (file.raw.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过10MB')
    fileList.pop()
    return
  }

  uploadedImages.value = fileList
}

const handleSubmit = async () => {
  await formRef.value.validate()

  if (uploadedImages.value.length === 0) {
    ElMessage.warning('请至少上传一张商品图片')
    return
  }

  loading.value = true
  try {
    // 上传图片
    const files = uploadedImages.value.map(item => item.raw)
    const uploadRes = await uploadMultipleFiles(files)
    const imageUrls = uploadRes.data

    // 设置图片URL
    form.value.images = imageUrls.join(',')
    form.value.mainImage = imageUrls[0]

    // 发布商品
    await publishGoods(form.value)
    ElMessage.success('发布成功，等待审核')
    router.push('/user/my-goods')
  } catch (error) {
    ElMessage.error(error.message || '发布失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = () => {
  router.back()
}
</script>

<style lang="scss" scoped>
.publish-page {
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
    max-width: 900px;
    margin: 0 auto;
  }

  .back-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #1E293B;
    text-decoration: none;
    font-weight: 500;
    cursor: pointer;

    .icon {
      width: 20px;
      height: 20px;
    }

    &:hover {
      color: #2563EB;
    }
  }

  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #1E293B;
  }
}

.content-wrapper {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 16px;
}

.publish-form {
  padding: 40px;

  .icon {
    width: 24px;
    height: 24px;
    color: #64748B;
  }

  .tip {
    color: #64748B;
    font-size: 12px;
    margin-top: 8px;
  }
}
</style>
