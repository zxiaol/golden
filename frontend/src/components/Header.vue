<template>
  <el-header class="header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <h2>货架电商</h2>
      </div>
      <el-menu
        mode="horizontal"
        :default-active="activeIndex"
        class="header-menu"
        @select="handleSelect"
      >
        <el-menu-item index="1" @click="$router.push('/')">首页</el-menu-item>
        <el-menu-item index="2" @click="$router.push('/products')">商品</el-menu-item>
        <el-menu-item index="3" @click="$router.push('/cart')" v-if="userStore.isLoggedIn">
          购物车
        </el-menu-item>
      </el-menu>
      <div class="user-info">
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              {{ userStore.user?.username || '用户' }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="text" @click="$router.push('/login')">登录</el-button>
          <el-button type="text" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const activeIndex = ref('1')

const handleSelect = (key) => {
  activeIndex.value = key
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/')
  } else if (command === 'profile') {
    router.push('/user')
  } else if (command === 'orders') {
    router.push('/orders')
  }
}
</script>

<style scoped>
.header {
  background-color: #409eff;
  color: white;
  height: 60px;
  line-height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
}

.logo {
  cursor: pointer;
  margin-right: 40px;
}

.logo h2 {
  color: white;
  margin: 0;
}

.header-menu {
  flex: 1;
  background-color: transparent;
  border: none;
}

.header-menu :deep(.el-menu-item) {
  color: white;
}

.header-menu :deep(.el-menu-item:hover),
.header-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .logo h2 {
    font-size: 18px;
  }
  
  .header-menu {
    display: none;
  }
}
</style>

