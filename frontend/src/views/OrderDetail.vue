<template>
  <div class="order-detail" v-if="order">
    <h2>订单详情</h2>
    <el-card>
      <div class="order-info">
        <p><strong>订单号:</strong> {{ order.orderNo }}</p>
        <p><strong>订单状态:</strong> 
          <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
        </p>
        <p><strong>总金额:</strong> <span class="price">¥{{ order.totalAmount }}</span></p>
        <p><strong>收货地址:</strong> {{ order.shippingAddress }}</p>
        <p><strong>下单时间:</strong> {{ formatDate(order.createdAt) }}</p>
      </div>
    </el-card>
    
    <el-card style="margin-top: 20px;">
      <h3>订单商品</h3>
      <el-table :data="orderItems" style="width: 100%">
        <el-table-column label="商品">
          <template #default="{ row }">
            <div class="product-cell">
              <img :src="getProductImage(row.product)" class="product-thumb" />
              <span>{{ row.product?.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="100" prop="quantity" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ row.subtotal }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '@/api'

const route = useRoute()
const order = ref(null)
const orderItems = ref([])

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

const getProductImage = (product) => {
  if (product?.imageUrls) {
    try {
      const images = JSON.parse(product.imageUrls)
      return images[0] || 'https://via.placeholder.com/100'
    } catch {
      return 'https://via.placeholder.com/100'
    }
  }
  return 'https://via.placeholder.com/100'
}

const loadOrder = async () => {
  try {
    const orderResponse = await api.get(`/orders/${route.params.id}`)
    order.value = orderResponse.data
    
    const itemsResponse = await api.get(`/orders/${route.params.id}/items`)
    orderItems.value = itemsResponse.data || []
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

onMounted(() => {
  loadOrder()
})
</script>

<style scoped>
.order-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.order-info p {
  margin: 10px 0;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
}
</style>

