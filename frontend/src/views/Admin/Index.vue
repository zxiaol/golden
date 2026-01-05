<template>
  <div class="admin">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="products">
            <el-icon><Goods /></el-icon>
            <span>商品管理</span>
          </el-menu-item>
          <el-menu-item index="orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Goods, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  return route.path.split('/').pop() || 'products'
})

const handleMenuSelect = (key) => {
  router.push(`/admin/${key}`)
}
</script>

<style scoped>
.admin {
  height: calc(100vh - 120px);
}

.el-aside {
  background-color: #fff;
  border-right: 1px solid #eee;
}
</style>

