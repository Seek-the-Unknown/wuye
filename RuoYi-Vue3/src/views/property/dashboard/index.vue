<template>
  <div class="dashboard-container">
    <!-- Top KPI Row -->
    <el-row :gutter="24" class="kpi-row">
      <el-col :sm="24" :lg="6" v-for="(kpi, index) in kpis" :key="index">
        <div class="kpi-card" :class="kpi.colorClass">
          <div class="kpi-icon-wrap">
            <el-icon class="kpi-icon"><component :is="kpi.icon" /></el-icon>
          </div>
          <div class="kpi-info">
            <div class="kpi-title">{{ kpi.title }}</div>
            <div class="kpi-value">
              <span class="num">{{ kpi.value }}</span>
            </div>
          </div>
          <div class="kpi-bg-shape"></div>
        </div>
      </el-col>
    </el-row>

    <!-- Middle Charts Row -->
    <el-row :gutter="24" class="chart-row">
      <el-col :sm="24" :lg="16">
        <el-card class="premium-card">
          <template #header>
            <div class="card-header">
              <span class="title">近半年报修工单趋势</span>
              <el-tag type="primary" size="small" effect="light">月度</el-tag>
            </div>
          </template>
          <div ref="repairTrendRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :sm="24" :lg="8">
        <el-card class="premium-card">
          <template #header>
            <div class="card-header">
              <span class="title">物业费收缴情况</span>
            </div>
          </template>
          <div ref="feeCollectionRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Bottom Action Row -->
    <el-row :gutter="24" class="action-row">
      <el-col :sm="24" :lg="16">
        <el-card class="premium-card">
          <template #header>
            <div class="card-header">
              <span class="title">最新报修动态</span>
              <el-button type="primary" link @click="$router.push('/property/repair')">查看更多</el-button>
            </div>
          </template>
          <el-table :data="recentRepairs" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="repairTitle" label="报修标题" show-overflow-tooltip />
            <el-table-column prop="ownerName" label="报修人" width="120" />
            <el-table-column prop="repairStatus" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.repairStatus === '0' ? 'danger' : scope.row.repairStatus === '1' ? 'warning' : 'success'" size="small">
                  {{ scope.row.repairStatus === '0' ? '待处理' : scope.row.repairStatus === '1' ? '处理中' : '已完成' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="报修时间" width="180" align="center" />
          </el-table>
        </el-card>
      </el-col>

      <el-col :sm="24" :lg="8">
        <el-card class="premium-card todo-card">
          <template #header>
            <div class="card-header">
              <span class="title">待办事项</span>
            </div>
          </template>
          <div class="todo-list">
            <div class="todo-item" @click="$router.push('/property/repair')">
              <div class="todo-icon danger"><el-icon><Warning /></el-icon></div>
              <div class="todo-content">
                <div class="todo-name">待处理报修</div>
                <div class="todo-desc">需尽快分配维修人员</div>
              </div>
              <div class="todo-count">{{ todoSummary.pendingRepairs || 0 }}</div>
            </div>
            
            <div class="todo-item" @click="$router.push('/property/complaint')">
              <div class="todo-icon warning"><el-icon><ChatLineSquare /></el-icon></div>
              <div class="todo-content">
                <div class="todo-name">未回复投诉建议</div>
                <div class="todo-desc">业主反馈意见待处理</div>
              </div>
              <div class="todo-count">{{ todoSummary.pendingComplaints || 0 }}</div>
            </div>

            <div class="todo-item" @click="$router.push('/property/visitor')">
              <div class="todo-icon primary"><el-icon><UserFilled /></el-icon></div>
              <div class="todo-content">
                <div class="todo-name">访客待审批</div>
                <div class="todo-desc">访客通行申请审核</div>
              </div>
              <div class="todo-count">{{ todoSummary.pendingVisitors || 0 }}</div>
            </div>

            <div class="todo-item" @click="$router.push('/property/feeRecord')">
              <div class="todo-icon success"><el-icon><Wallet /></el-icon></div>
              <div class="todo-content">
                <div class="todo-name">未缴费账单</div>
                <div class="todo-desc">需向业主催缴物业费</div>
              </div>
              <div class="todo-count">{{ todoSummary.unpaidFees || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Dashboard">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { getDashboardStats, getRepairTrend, getFeeCollection, getRecentRepairs, getTodoSummary } from '@/api/property/dashboard';

const router = useRouter();
const stats = ref({});
const recentRepairs = ref([]);
const todoSummary = ref({});
const loading = ref(true);

const repairTrendRef = ref(null);
const feeCollectionRef = ref(null);
let repairChart = null;
let feeChart = null;

const kpis = computed(() => [
  { title: '管理小区总数', value: stats.value.communityCount || 0, icon: 'OfficeBuilding', colorClass: 'bg-blue' },
  { title: '入驻登记业主', value: stats.value.ownerCount || 0, icon: 'User', colorClass: 'bg-green' },
  { title: '物业房屋总数', value: stats.value.roomCount || 0, icon: 'House', colorClass: 'bg-orange' },
  { title: '累计报修工单', value: stats.value.repairCount || 0, icon: 'Service', colorClass: 'bg-purple' }
]);

function initData() {
  getDashboardStats().then(res => {
    stats.value = res.data || {};
  });

  getRecentRepairs().then(res => {
    recentRepairs.value = res.data || [];
    loading.value = false;
  });

  getTodoSummary().then(res => {
    todoSummary.value = res.data || {};
  });

  getRepairTrend().then(res => {
    initRepairChart(res.data || []);
  });

  getFeeCollection().then(res => {
    initFeeChart(res.data || {});
  });
}

function initRepairChart(data) {
  if (!repairTrendRef.value) return;
  repairChart = echarts.init(repairTrendRef.value);
  
  const months = data.map(item => item.month);
  const counts = data.map(item => item.count);

  const option = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255, 255, 255, 0.9)',
      padding: [10, 15],
      textStyle: { color: '#333' },
      axisPointer: { type: 'shadow' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '10%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months.length > 0 ? months : ['一月', '二月', '三月', '四月', '五月', '六月'],
      axisLine: { lineStyle: { color: '#E4E7ED' } },
      axisLabel: { color: '#909399', margin: 15 }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { type: 'dashed', color: '#E4E7ED' } },
      axisLabel: { color: '#909399' }
    },
    series: [
      {
        name: '报修单量',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        itemStyle: { color: '#409EFF', borderWidth: 2, borderColor: '#fff' },
        lineStyle: { width: 3, color: '#409EFF', shadowColor: 'rgba(64,158,255, 0.3)', shadowBlur: 10, shadowOffsetY: 5 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64,158,255,0.4)' },
            { offset: 1, color: 'rgba(64,158,255,0.05)' }
          ])
        },
        data: counts.length > 0 ? counts : [0, 0, 0, 0, 0, 0]
      }
    ]
  };
  repairChart.setOption(option);
}

function initFeeChart(data) {
  if (!feeCollectionRef.value) return;
  feeChart = echarts.init(feeCollectionRef.value);
  
  const paidAmount = data.paidAmount || 0;
  const unpaidAmount = data.unpaidAmount || 0;
  const payRate = data.payRate ? data.payRate.toFixed(1) : 0;

  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} 元 ({d}%)' },
    legend: { bottom: '5%', left: 'center', itemWidth: 12, itemHeight: 12, textStyle: { color: '#606266' } },
    title: {
      text: `${payRate}%`,
      subtext: '收缴率',
      left: 'center',
      top: '38%',
      textStyle: { fontSize: 32, fontWeight: 'bold', color: '#303133' },
      subtextStyle: { fontSize: 14, color: '#909399' }
    },
    series: [
      {
        name: '费用统计',
        type: 'pie',
        radius: ['50%', '75%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: { show: false, position: 'center' },
        labelLine: { show: false },
        data: [
          { value: paidAmount, name: '已缴费金额', itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{offset: 0, color: '#67C23A'}, {offset: 1, color: '#A0C95E'}]) } },
          { value: unpaidAmount, name: '未缴费金额', itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{offset: 0, color: '#F56C6C'}, {offset: 1, color: '#E99B9B'}]) } }
        ]
      }
    ]
  };
  feeChart.setOption(option);
}

function resizeCharts() {
  if (repairChart) repairChart.resize();
  if (feeChart) feeChart.resize();
}

onMounted(() => {
  initData();
  window.addEventListener('resize', resizeCharts);
});

onUnmounted(() => {
  window.removeEventListener('resize', resizeCharts);
  if (repairChart) repairChart.dispose();
  if (feeChart) feeChart.dispose();
});
</script>

<style scoped lang="scss">
.dashboard-container {
  padding: 24px;
  background-color: #f6f8fb;
  min-height: calc(100vh - 84px);
}

/* KPIs */
.kpi-row {
  margin-bottom: 24px;
}

.kpi-card {
  position: relative;
  display: flex;
  align-items: center;
  height: 130px;
  border-radius: 16px;
  padding: 0 24px;
  overflow: hidden;
  box-shadow: 0 10px 20px rgba(0,0,0,0.04);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  color: #fff;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0,0,0,0.1);
    
    .kpi-icon-wrap {
      transform: scale(1.1);
    }
  }

  &.bg-blue { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
  &.bg-green { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
  &.bg-orange { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
  &.bg-purple { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }

  .kpi-icon-wrap {
    width: 64px;
    height: 64px;
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    justify-content: center;
    align-items: center;
    backdrop-filter: blur(4px);
    transition: transform 0.3s ease;
    z-index: 2;

    .kpi-icon {
      font-size: 32px;
      color: #fff;
    }
  }

  .kpi-info {
    margin-left: 20px;
    z-index: 2;

    .kpi-title {
      font-size: 14px;
      font-weight: 500;
      opacity: 0.9;
      margin-bottom: 8px;
    }

    .kpi-value {
      font-size: 32px;
      font-weight: 700;
      letter-spacing: 1px;
    }
  }

  .kpi-bg-shape {
    position: absolute;
    right: -20%;
    top: -50%;
    width: 150px;
    height: 150px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    transform: rotate(45deg);
    z-index: 1;
  }
}

/* Premium Cards */
.chart-row, .action-row {
  margin-bottom: 24px;
}

.premium-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0,0,0,0.03);
  
  :deep(.el-card__header) {
    padding: 18px 24px;
    border-bottom: 1px solid #f0f2f5;
  }

  :deep(.el-card__body) {
    padding: 24px;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      position: relative;
      padding-left: 12px;

      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 16px;
        background-color: #409EFF;
        border-radius: 2px;
      }
    }
  }
}

/* Todo List */
.todo-card {
  height: 100%;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .todo-item {
    display: flex;
    align-items: center;
    padding: 16px;
    background-color: #f8f9fc;
    border-radius: 10px;
    transition: all 0.2s ease;
    cursor: pointer;

    &:hover {
      background-color: #fff;
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      transform: translateX(4px);
    }

    .todo-icon {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 24px;

      &.danger { background-color: rgba(245, 108, 108, 0.1); color: #F56C6C; }
      &.warning { background-color: rgba(230, 162, 60, 0.1); color: #E6A23C; }
      &.primary { background-color: rgba(64, 158, 255, 0.1); color: #409EFF; }
      &.success { background-color: rgba(103, 194, 58, 0.1); color: #67C23A; }
    }

    .todo-content {
      flex: 1;
      margin-left: 16px;

      .todo-name {
        font-size: 15px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
      }

      .todo-desc {
        font-size: 13px;
        color: #909399;
      }
    }

    .todo-count {
      font-size: 24px;
      font-weight: 700;
      color: #303133;
      background: #f0f2f5;
      padding: 4px 12px;
      border-radius: 20px;
    }
    
    &:hover .todo-count {
      background: #409EFF;
      color: #fff;
    }
  }
}
</style>
