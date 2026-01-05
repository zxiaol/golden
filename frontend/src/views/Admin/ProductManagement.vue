<template>
  <div class="product-management">
    <div class="header">
      <h2>商品管理</h2>
      <el-button type="primary" @click="showDialog = true">添加商品</el-button>
    </div>
    
    <el-table :data="products" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="商品图片" width="100">
        <template #default="{ row }">
          <img :src="getProductImage(row)" class="product-thumb" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" />
      <el-table-column prop="price" label="价格" width="120">
        <template #default="{ row }">
          ¥{{ row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="editProduct(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteProduct(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog v-model="showDialog" :title="editingProduct ? '编辑商品' : '添加商品'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api'

const products = ref([])
const categories = ref([])
const showDialog = ref(false)
const editingProduct = ref(null)
const form = ref({
  name: '',
  description: '',
  price: 0,
  stock: 0,
  categoryId: null,
  status: 1
})

const getProductImage = (product) => {
  if (product.imageUrls) {
    try {
      const images = JSON.parse(product.imageUrls)
      return images[0] || 'https://via.placeholder.com/100'
    } catch {
      return 'https://via.placeholder.com/100'
    }
  }
  return 'https://via.placeholder.com/100'
}

const loadProducts = async () => {
  try {
    const response = await api.get('/products', {
      params: { page: 1, pageSize: 100 }
    })
    products.value = response.data.list || []
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const loadCategories = async () => {
  try {
    const response = await api.get('/categories')
    categories.value = response.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const editProduct = (product) => {
  editingProduct.value = product
  form.value = { ...product }
  showDialog.value = true
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.delete(`/products/${id}`)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const saveProduct = async () => {
  try {
    if (editingProduct.value) {
      await api.put(`/products/${editingProduct.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await api.post('/products', form.value)
      ElMessage.success('添加成功')
    }
    showDialog.value = false
    editingProduct.value = null
    form.value = {
      name: '',
      description: '',
      price: 0,
      stock: 0,
      categoryId: null,
      status: 1
    }
    loadProducts()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadProducts()
  loadCategories()
})
</script>

<style scoped>
.product-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.product-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
}
</style>

