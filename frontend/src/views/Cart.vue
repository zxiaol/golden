<template>
  <div class="cart">
    <h2>购物车</h2>
    <el-empty v-if="cartItems.length === 0" description="购物车为空" />
    <div v-else>
      <el-table :data="cartItems" style="width: 100%">
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
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.quantity"
              :min="1"
              :max="row.product?.stock"
              @change="updateQuantity(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            ¥{{ (row.product?.price * row.quantity).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="cart-footer">
        <div class="total">
          总计: <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <el-button type="primary" size="large" @click="checkout">去结算</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const router = useRouter()
const cartItems = ref([])

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
  return cartItems.value.reduce((sum, item) => {
    return sum + (item.product?.price || 0) * item.quantity
  }, 0)
})

const loadCart = async () => {
  try {
    const response = await api.get('/cart')
    cartItems.value = response.data || []
  } catch (error) {
    ElMessage.error('加载购物车失败')
  }
}

const updateQuantity = async (item) => {
  try {
    await api.put(`/cart/${item.id}`, null, {
      params: { quantity: item.quantity }
    })
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
    loadCart()
  }
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/cart/${id}`)
    ElMessage.success('删除成功')
    loadCart()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const checkout = () => {
  router.push('/orders/confirm')
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.cart {
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

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f5f5;
}

.total {
  font-size: 18px;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

@media (max-width: 768px) {
  .cart-footer {
    flex-direction: column;
    gap: 15px;
  }
  
  .cart-footer .el-button {
    width: 100%;
  }
}
</style>

