<template>
  <div class="home">
    <el-carousel height="400px" indicator-position="outside">
      <el-carousel-item v-for="item in banners" :key="item">
        <div class="banner-item" :style="{ backgroundImage: `url(${item})` }"></div>
      </el-carousel-item>
    </el-carousel>
    
    <div class="product-section">
      <h2>热门商品</h2>
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="product in products" :key="product.id">
          <el-card class="product-card" @click="goToDetail(product.id)">
            <img :src="getProductImage(product)" class="product-image" />
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p class="price">¥{{ product.price }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const products = ref([])
const banners = ref([
  'https://via.placeholder.com/1200x400',
  'https://via.placeholder.com/1200x400',
  'https://via.placeholder.com/1200x400'
])

const getProductImage = (product) => {
  if (product.imageUrls) {
    try {
      const images = JSON.parse(product.imageUrls)
      return images[0] || 'https://via.placeholder.com/300'
    } catch {
      return 'https://via.placeholder.com/300'
    }
  }
  return 'https://via.placeholder.com/300'
}

const goToDetail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(async () => {
  try {
    const response = await api.get('/products', {
      params: { page: 1, pageSize: 8 }
    })
    products.value = response.data.list || []
  } catch (error) {
    console.error('加载商品失败:', error)
  }
})
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.banner-item {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
}

.product-section {
  margin-top: 40px;
}

.product-section h2 {
  margin-bottom: 20px;
  font-size: 24px;
}

.product-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 10px 0;
}

.product-info h3 {
  font-size: 16px;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

@media (max-width: 768px) {
  .product-section h2 {
    font-size: 20px;
  }
}
</style>

