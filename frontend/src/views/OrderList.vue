<template>
  <div class="order-list">
    <h2>我的订单</h2>
    <el-empty v-if="orders.length === 0" description="暂无订单" />
    <div v-else>
      <el-card v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号: {{ order.orderNo }}</span>
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </div>
        <div class="order-info">
          <p>总金额: <span class="price">¥{{ order.totalAmount }}</span></p>
          <p>下单时间: {{ formatDate(order.createdAt) }}</p>
        </div>
        <div class="order-actions">
          <el-button @click="viewDetail(order.id)">查看详情</el-button>
        </div>
      </el-card>
      
      <el-pagination
        v-model:current-page="page"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadOrders"
        style="margin-top: 20px;"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const orders = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'success',
    4: 'info'
  }
  return typeMap[status] || ''
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const viewDetail = (id) => {
  router.push(`/orders/${id}`)
}

const loadOrders = async () => {
  try {
    const response = await api.get('/orders', {
      params: {
        page: page.value,
        pageSize: pageSize.value
      }
    })
    orders.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-list {
  max-width: 1200px;
  margin: 0 auto;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-info {
  margin-bottom: 15px;
}

.order-info p {
  margin: 5px 0;
}

.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.order-actions {
  text-align: right;
}
</style>

