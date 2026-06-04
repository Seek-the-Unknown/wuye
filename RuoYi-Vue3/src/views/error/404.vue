<template>
  <div class="errPage-container">
    <el-result
      icon="error"
      title="404 页面不存在"
      sub-title="抱歉，您访问的页面不存在或已被移除"
    >
      <template #extra>
        <el-button type="primary" @click="back">返回上一页</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function back() {
  router.go(-1)
}

function goHome() {
  router.push({ path: '/index' })
}
</script>

<style scoped>
.errPage-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}
</style>
