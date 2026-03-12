<template>
  <div class="wallet-page glass-card">
    <h2 class="page-title">我的钱包</h2>

    <!-- 余额卡片 -->
    <div class="balance-card glass-card">
      <div class="balance-info">
        <div class="balance-label">账户余额</div>
        <div class="balance-amount">¥{{ balance }}</div>
      </div>
      <el-button type="primary" size="large" @click="showRechargeDialog">充值</el-button>
    </div>

    <!-- 交易记录 -->
    <div class="transactions-section">
      <h3 class="section-title">交易记录</h3>
      <el-table v-loading="loading" :data="transactions" stripe>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 'RECHARGE'" type="success">充值</el-tag>
            <el-tag v-else-if="row.type === 'PAYMENT'" type="warning">支付</el-tag>
            <el-tag v-else-if="row.type === 'REFUND'" type="info">退款</el-tag>
            <el-tag v-else-if="row.type === 'INCOME'" type="success">收入</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="['amount', row.type === 'PAYMENT' ? 'negative' : 'positive']">
              {{ row.type === 'PAYMENT' ? '-' : '+' }}¥{{ row.amount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="balanceAfter" label="余额" width="120">
          <template #default="{ row }">
            ¥{{ row.balanceAfter }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </div>

    <!-- 充值对话框 -->
    <el-dialog v-model="rechargeDialogVisible" title="账户充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :step="100"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="快捷金额">
          <div class="quick-amounts">
            <el-button @click="rechargeForm.amount = 100">100元</el-button>
            <el-button @click="rechargeForm.amount = 500">500元</el-button>
            <el-button @click="rechargeForm.amount = 1000">1000元</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">
          确认充值
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBalance, recharge, getTransactions } from '@/api/wallet'

const loading = ref(false)
const balance = ref(0)
const transactions = ref([])

const rechargeDialogVisible = ref(false)
const recharging = ref(false)
const rechargeForm = ref({
  amount: 100
})

const fetchBalance = async () => {
  try {
    const res = await getBalance()
    balance.value = res.data
  } catch (error) {
    console.error('获取余额失败', error)
  }
}

const fetchTransactions = async () => {
  loading.value = true
  try {
    const res = await getTransactions()
    transactions.value = res.data
  } catch (error) {
    ElMessage.error('获取交易记录失败')
  } finally {
    loading.value = false
  }
}

const showRechargeDialog = () => {
  rechargeForm.value.amount = 100
  rechargeDialogVisible.value = true
}

const handleRecharge = async () => {
  if (rechargeForm.value.amount <= 0) {
    ElMessage.warning('请输入正确的充值金额')
    return
  }
  
  recharging.value = true
  try {
    await recharge(rechargeForm.value.amount)
    ElMessage.success('充值成功')
    rechargeDialogVisible.value = false
    await fetchBalance()
    await fetchTransactions()
  } catch (error) {
    ElMessage.error(error.message || '充值失败')
  } finally {
    recharging.value = false
  }
}

onMounted(() => {
  fetchBalance()
  fetchTransactions()
})
</script>

<style lang="scss" scoped>
.wallet-page {
  padding: 32px;
  max-width: 1200px;
  margin: 0 auto;

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 24px;
  }
}

.balance-card {
  padding: 32px;
  margin-bottom: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;

  .balance-info {
    .balance-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }

    .balance-amount {
      font-size: 48px;
      font-weight: 700;
    }
  }
}

.transactions-section {
  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #1E293B;
    margin-bottom: 16px;
  }

  .amount {
    font-weight: 600;

    &.positive {
      color: #10B981;
    }

    &.negative {
      color: #EF4444;
    }
  }
}

.quick-amounts {
  display: flex;
  gap: 8px;
}
</style>
