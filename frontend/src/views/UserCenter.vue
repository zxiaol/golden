<template>
  <div class="user-center">
    <h2>个人中心</h2>
    <el-card>
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload/image"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateProfile">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/api'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const form = ref({
  username: '',
  email: '',
  phone: '',
  avatar: ''
})

const loadProfile = async () => {
  try {
    const response = await api.get('/user/profile')
    form.value = response.data
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

const handleAvatarSuccess = (response) => {
  if (response.data) {
    form.value.avatar = response.data
    ElMessage.success('头像上传成功')
  }
}

const updateProfile = async () => {
  try {
    await api.put('/user/profile', form.value)
    ElMessage.success('更新成功')
    await userStore.fetchProfile()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.user-center {
  max-width: 800px;
  margin: 0 auto;
}

.avatar-uploader {
  display: flex;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
  border-radius: 50%;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
}
</style>

