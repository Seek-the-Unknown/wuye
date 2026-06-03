<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-menu :default-active="activeMenu" class="portal-menu" router>
          <el-menu-item index="/portal/home">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/portal/myBill">
            <el-icon><Money /></el-icon>
            <span>我的账单</span>
          </el-menu-item>
          <el-menu-item index="/portal/myRepair">
            <el-icon><Service /></el-icon>
            <span>在线报修</span>
          </el-menu-item>
          <el-menu-item index="/portal/myVisitor">
            <el-icon><Guide /></el-icon>
            <span>访客登记</span>
          </el-menu-item>
          <el-menu-item index="/portal/myComplaint">
            <el-icon><Comment /></el-icon>
            <span>投诉建议</span>
          </el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="18">
        <router-view />
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router';
const route = useRoute();
const activeMenu = computed(() => route.path);
</script>

<style scoped>
.portal-menu { min-height: calc(100vh - 120px); border-right: none; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05); }
.portal-menu .el-menu-item { border-radius: 8px; margin: 5px 10px; }
.portal-menu .el-menu-item.is-active { background-color: #ecf5ff; }
</style>
