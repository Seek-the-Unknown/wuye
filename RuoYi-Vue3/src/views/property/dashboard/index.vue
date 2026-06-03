<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :sm="24" :lg="6">
        <el-card class="box-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #409EFF"><OfficeBuilding /></el-icon>
            <div class="stat-info">
              <div class="stat-title">小区总数</div>
              <div class="stat-value">{{ stats.communityCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="6">
        <el-card class="box-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #67C23A"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-title">登记业主总数</div>
              <div class="stat-value">{{ stats.ownerCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="6">
        <el-card class="box-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #E6A23C"><House /></el-icon>
            <div class="stat-info">
              <div class="stat-title">房屋总数</div>
              <div class="stat-value">{{ stats.roomCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="6">
        <el-card class="box-card">
          <div class="stat-item">
            <el-icon class="stat-icon" style="color: #F56C6C"><Service /></el-icon>
            <div class="stat-info">
              <div class="stat-title">报修工单数</div>
              <div class="stat-value">{{ stats.repairCount || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <template #header>物业概览</template>
          <div style="height: 300px; display: flex; justify-content: center; align-items: center; color: #909399;">
            统计图表（报修趋势、费用收缴比例等）
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Dashboard">
/**
 * 物业管理系统 - 仪表盘首页
 *
 * 这里展示物业的统计数据概览，
 * 包括小区数量、业主数量、房屋数量、报修工单等。
 *
 * TODO: 统计图表部分还没有用 ECharts 实现，后续有时间再补上
 */
import { getDashboardStats } from '@/api/property/dashboard';

const stats = ref({});

/** 从后端获取统计数据 */
function getStats() {
  console.log('【Dashboard】加载统计数据...');
  getDashboardStats().then(res => {
    stats.value = res.data || {};
    console.log('【Dashboard】统计加载完成');
  });
}

getStats();
</script>

<style scoped>
.box-card { height: 120px; }
.stat-item { display: flex; align-items: center; height: 100%; padding-left: 20px; }
.stat-icon { font-size: 50px; }
.stat-info { margin-left: 20px; }
.stat-title { font-size: 14px; color: #909399; margin-bottom: 10px; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; }
</style>
