<template>
  <div class="product-detail" v-if="product">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="12" :md="12">
        <el-image
          :src="currentImage"
          :preview-src-list="imageList"
          fit="cover"
          class="main-image"
        />
        <div class="thumbnail-list">
          <el-image
            v-for="(img, index) in imageList"
            :key="index"
            :src="img"
            @click="currentImage = img"
            class="thumbnail"
            :class="{ active: currentImage === img }"
          />
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12">
        <h1>{{ product.name }}</h1>
        <p class="price">¥{{ product.price }}</p>
        <p class="stock">库存: {{ product.stock }}</p>
        <div class="description">
          <h3>商品描述</h3>
          <p>{{ product.description }}</p>
        </div>
        <div class="quantity-selector">
          <span>数量:</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock" />
        </div>
        <div class="actions">
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button type="success" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </el-col>
    </el-row>
    
    <el-divider />
    
    <div class="reviews-section">
      <h2>商品评价</h2>
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <span class="username">{{ review.user?.username || '匿名用户' }}</span>
          <el-rate v-model="review.rating" disabled />
          <span class="date">{{ formatDate(review.createdAt) }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div v-if="review.images" class="review-images">
          <el-image
            v-for="(img, index) in getReviewImages(review)"
            :key="index"
            :src="img"
            class="review-image"
          />
        </div>
      </div>
      <el-pagination
        v-model:current-page="reviewPage"
        :page-size="reviewPageSize"
        :total="reviewTotal"
        layout="prev, pager, next"
        @current-change="loadReviews"
        style="margin-top: 20px;"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const product = ref(null)
const quantity = ref(1)
const currentImage = ref('')
const imageList = ref([])
const reviews = ref([])
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)

const getReviewImages = (review) => {
  if (review.images) {
    try {
      return JSON.parse(review.images)
    } catch {
      return []
    }
  }
  return []
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const loadProduct = async () => {
  try {
    const response = await api.get(`/products/${route.params.id}`)
    product.value = response.data
    
    if (product.value.imageUrls) {
      try {
        imageList.value = JSON.parse(product.value.imageUrls)
        currentImage.value = imageList.value[0] || 'https://via.placeholder.com/500'
      } catch {
        imageList.value = ['https://via.placeholder.com/500']
        currentImage.value = imageList.value[0]
      }
    } else {
      imageList.value = ['https://via.placeholder.com/500']
      currentImage.value = imageList.value[0]
    }
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const loadReviews = async () => {
  try {
    const response = await api.get(`/reviews/product/${route.params.id}`, {
      params: {
        page: reviewPage.value,
        pageSize: reviewPageSize.value
      }
    })
    reviews.value = response.data.list || []
    reviewTotal.value = response.data.total || 0
  } catch (error) {
    console.error('加载评价失败:', error)
  }
}

const addToCart = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  try {
    await api.post('/cart', null, {
      params: {
        productId: product.value.id,
        quantity: quantity.value
      }
    })
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  }
}

const buyNow = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 跳转到订单确认页
  router.push({
    path: '/orders/confirm',
    query: {
      productId: product.value.id,
      quantity: quantity.value
    }
  })
}

onMounted(() => {
  loadProduct()
  loadReviews()
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.main-image {
  width: 100%;
  height: 500px;
  margin-bottom: 10px;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
}

.thumbnail {
  width: 80px;
  height: 80px;
  cursor: pointer;
  border: 2px solid transparent;
}

.thumbnail.active {
  border-color: #409eff;
}

.price {
  font-size: 32px;
  color: #f56c6c;
  font-weight: bold;
  margin: 20px 0;
}

.stock {
  color: #666;
  margin-bottom: 20px;
}

.description {
  margin: 30px 0;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 30px 0;
}

.actions {
  display: flex;
  gap: 10px;
}

.reviews-section {
  margin-top: 40px;
}

.review-item {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.username {
  font-weight: bold;
}

.date {
  color: #999;
  margin-left: auto;
}

.review-content {
  margin: 10px 0;
}

.review-images {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.review-image {
  width: 100px;
  height: 100px;
}

@media (max-width: 768px) {
  .main-image {
    height: 300px;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .actions .el-button {
    width: 100%;
  }
}
</style>

