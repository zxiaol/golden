<template>
  <div class="product-list">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="6" :md="6">
        <el-card class="filter-card">
          <h3>商品分类</h3>
          <el-tree
            :data="categoryTree"
            :props="{ children: 'children', label: 'name' }"
            @node-click="handleCategoryClick"
          />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="18" :md="18">
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索商品"
            @keyup.enter="search"
          >
            <template #append>
              <el-button @click="search">搜索</el-button>
            </template>
          </el-input>
        </div>
        
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8" :md="6" v-for="product in products" :key="product.id">
            <el-card class="product-card" @click="goToDetail(product.id)">
              <img :src="getProductImage(product)" class="product-image" />
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p class="price">¥{{ product.price }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadProducts"
          style="margin-top: 20px; justify-content: center;"
        />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()
const products = ref([])
const categories = ref([])
const categoryTree = ref([])
const keyword = ref('')
const selectedCategoryId = ref(null)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

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

const handleCategoryClick = (data) => {
  selectedCategoryId.value = data.id
  page.value = 1
  loadProducts()
}

const search = () => {
  page.value = 1
  loadProducts()
}

const loadProducts = async () => {
  try {
    const response = await api.get('/products', {
      params: {
        categoryId: selectedCategoryId.value,
        keyword: keyword.value,
        page: page.value,
        pageSize: pageSize.value
      }
    })
    products.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('加载商品失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data || []
    buildCategoryTree()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const buildCategoryTree = () => {
  const map = {}
  const roots = []
  
  categories.value.forEach(cat => {
    map[cat.id] = { ...cat, children: [] }
  })
  
  categories.value.forEach(cat => {
    if (cat.parentId === 0 || !cat.parentId) {
      roots.push(map[cat.id])
    } else if (map[cat.parentId]) {
      map[cat.parentId].children.push(map[cat.id])
    }
  })
  
  categoryTree.value = roots
}

onMounted(() => {
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.product-list {
  max-width: 1200px;
  margin: 0 auto;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-card h3 {
  margin-bottom: 15px;
}

.search-bar {
  margin-bottom: 20px;
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
  .filter-card {
    margin-bottom: 10px;
  }
}
</style>

