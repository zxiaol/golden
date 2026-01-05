<template>
  <div class="order-management">
    <h2>订单管理</h2>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="totalAmount" label="总金额" width="120">
        <template #default="{ row }">
          ¥{{ row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="下单时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">查看</el-button>
          <el-button
            v-if="row.status === 1"
            type="success"
            size="small"
            @click="updateStatus(row.id, 2)"
          >
            发货
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="page"
      :page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      @current-change="loadOrders"
      style="margin-top: 20px;"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
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

const updateStatus = async (id, status) => {
  try {
    await api.put(`/orders/${id}/status`, null, {
      params: { status }
    })
    ElMessage.success('更新成功')
    loadOrders()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const loadOrders = async () => {
  try {
    const response = await api.get('/orders/admin/all', {
      params: {
        page: page.value,
        pageSize: pageSize.value
      }
    })
    orders.value = response.data.list || []
    total.value = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载订单失败')
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-management {
  padding: 20px;
}
</style>

