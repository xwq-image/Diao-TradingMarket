<template>
  <div class="my-orders-page glass-card">
    <h2 class="page-title">我的订单</h2>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="我买到的" name="buy" />
      <el-tab-pane label="我卖出的" name="sell" />
    </el-tabs>

    <el-table v-loading="loading" :data="orders" stripe>
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column label="商品信息" width="300">
        <template #default="{ row }">
          <div class="goods-info">
            <img :src="getGoodsImage(row)" class="goods-image" />
            <div class="goods-details">
              <div class="goods-title">{{ getGoodsTitle(row) }}</div>
              <div class="goods-price">¥{{ row.price }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'PENDING'" type="info">待确认</el-tag>
          <el-tag v-else-if="row.status === 'CONFIRMED'" type="warning">待付款</el-tag>
          <el-tag v-else-if="row.status === 'PAID'" type="primary">待发货</el-tag>
          <el-tag v-else-if="row.status === 'SHIPPED'" type="primary">待收货</el-tag>
          <el-tag v-else-if="row.status === 'COMPLETED'" type="success">已完成</el-tag>
          <el-tag v-else-if="row.status === 'CANCELLED'" type="danger">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <!-- 买家操作 -->
          <template v-if="activeTab === 'buy'">
            <el-button
              v-if="row.status === 'CONFIRMED'"
              type="primary"
              size="small"
              @click="handlePay(row)"
            >
              立即付款
            </el-button>
            <el-button
              v-if="row.status === 'SHIPPED'"
              type="success"
              size="small"
              @click="handleComplete(row.id)"
            >
              确认收货
            </el-button>
            <el-button
              v-if="['PENDING', 'CONFIRMED'].includes(row.status)"
              type="danger"
              size="small"
              @click="handleCancel(row.id)"
            >
              取消订单
            </el-button>
          </template>

          <!-- 卖家操作 -->
          <template v-else>
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handleConfirm(row.id)"
            >
              确认订单
            </el-button>
            <el-button
              v-if="row.status === 'PAID'"
              type="success"
              size="small"
              @click="handleShip(row.id)"
            >
              确认发货
            </el-button>
            <el-button
              v-if="row.status === 'PENDING'"
              type="danger"
              size="small"
              @click="handleCancel(row.id)"
            >
              拒绝订单
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="订单支付" width="400px">
      <div class="pay-info">
        <div class="pay-item">
          <span class="label">订单金额：</span>
          <span class="value">¥{{ currentOrder?.price }}</span>
        </div>
        <div class="pay-item">
          <span class="label">账户余额：</span>
          <span class="value">¥{{ balance }}</span>
        </div>
      </div>
      <el-form :model="payForm" label-width="80px">
        <el-form-item label="支付密码">
          <el-input
            v-model="payForm.password"
            type="password"
            placeholder="请输入登录密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">
          确认支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyBuyOrders, getMySellOrders, confirmOrder, payOrder, shipOrder, completeOrder, cancelOrder } from '@/api/order'
import { getBalance } from '@/api/wallet'

const loading = ref(false)
const activeTab = ref('buy')
const orders = ref([])
const balance = ref(0)

const payDialogVisible = ref(false)
const paying = ref(false)
const currentOrder = ref(null)
const payForm = ref({
  password: ''
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const api = activeTab.value === 'buy' ? getMyBuyOrders : getMySellOrders
    const res = await api({ page: 1, size: 100 })
    orders.value = res.data.records
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const fetchBalance = async () => {
  try {
    const res = await getBalance()
    balance.value = res.data
  } catch (error) {
    console.error('获取余额失败', error)
  }
}

const handleTabChange = () => {
  fetchOrders()
}

const getGoodsImage = (order) => {
  try {
    const snapshot = JSON.parse(order.goodsSnapshot)
    return snapshot.mainImage || '/placeholder.jpg'
  } catch {
    return '/placeholder.jpg'
  }
}

const getGoodsTitle = (order) => {
  try {
    const snapshot = JSON.parse(order.goodsSnapshot)
    return snapshot.title || '商品'
  } catch {
    return '商品'
  }
}

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认接受此订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await confirmOrder(id)
    ElMessage.success('已确认订单')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handlePay = async (order) => {
  currentOrder.value = order
  payForm.value.password = ''
  await fetchBalance()
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!payForm.value.password) {
    ElMessage.warning('请输入支付密码')
    return
  }
  
  paying.value = true
  try {
    await payOrder(currentOrder.value.id, payForm.value.password)
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  } finally {
    paying.value = false
  }
}

const handleShip = async (id) => {
  try {
    await ElMessageBox.confirm('确认已发货？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await shipOrder(id)
    ElMessage.success('已确认发货')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleComplete = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await completeOrder(id)
    ElMessage.success('交易完成')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确认取消此订单？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await cancelOrder(id)
    ElMessage.success('已取消订单')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.my-orders-page {
  padding: 32px;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }
}

.goods-info {
  display: flex;
  gap: 12px;
  align-items: center;

  .goods-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 8px;
  }

  .goods-details {
    flex: 1;

    .goods-title {
      font-size: 14px;
      color: #1E293B;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .goods-price {
      font-size: 16px;
      font-weight: 600;
      color: #EF4444;
    }
  }
}

.pay-info {
  margin-bottom: 20px;
  padding: 16px;
  background: #F8FAFC;
  border-radius: 8px;

  .pay-item {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;

    .label {
      color: #64748B;
      font-size: 14px;
    }

    .value {
      color: #1E293B;
      font-size: 16px;
      font-weight: 600;
    }
  }
}
</style>
