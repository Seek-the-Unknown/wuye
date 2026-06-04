<template>
  <div class="errPage-container">
    <el-result
      icon="warning"
      title="401 无权限访问"
      sub-title="抱歉，您没有权限访问该页面，请联系系统管理员获取权限"
    >
      <template #extra>
        <el-button type="primary" @click="back">返回上一页</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

function back() {
  if (route.query.noGoBack) {
    router.push({ path: '/index' })
  } else {
    router.go(-1)
  }
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
