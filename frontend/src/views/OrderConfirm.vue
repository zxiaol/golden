<template>
  <div class="order-confirm">
    <h2>确认订单</h2>
    <el-card>
      <h3>收货地址</h3>
      <el-input
        v-model="shippingAddress"
        type="textarea"
        :rows="3"
        placeholder="请输入收货地址"
      />
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
            ¥{{ row.product?.price }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="100" prop="quantity" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ (row.product?.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
      <div class="total-section">
        <span>总计: <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span></span>
      </div>
    </el-card>
    
    <div class="actions">
      <el-button @click="$router.back()">返回</el-button>
      <el-button type="primary" size="large" @click="submitOrder" :loading="loading">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const route = useRoute()
const router = useRouter()
const shippingAddress = ref('')
const orderItems = ref([])
const loading = ref(false)

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

const totalPrice = computed(() => {
  return orderItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0)
})

const loadCart = async () => {
  try {
    const response = await api.get('/cart')
    orderItems.value = response.data || []
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

const submitOrder = async () => {
  if (!shippingAddress.value.trim()) {
    ElMessage.warning('请输入收货地址')
    return
  }
  
  loading.value = true
  try {
    const response = await api.post('/orders', null, {
      params: { shippingAddress: shippingAddress.value }
    })
    ElMessage.success('订单创建成功')
    router.push(`/orders/${response.data.id}`)
  } catch (error) {
    ElMessage.error(error.message || '创建订单失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.order-confirm {
  max-width: 1200px;
  margin: 0 auto;
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

.total-section {
  text-align: right;
  padding: 20px;
  font-size: 18px;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>

