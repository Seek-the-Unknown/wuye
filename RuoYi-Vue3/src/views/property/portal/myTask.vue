<template>
  <div class="app-container worker-container">
    <!-- Top Modern KPI Cards -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="8">
        <el-card class="kpi-card glass-card" shadow="hover">
          <div class="kpi-body">
            <el-icon class="kpi-icon text-warning"><Timer /></el-icon>
            <div class="kpi-info">
              <div class="kpi-num">{{ pendingCount }}</div>
              <div class="kpi-title">待处理工单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="kpi-card glass-card" shadow="hover">
          <div class="kpi-body">
            <el-icon class="kpi-icon text-success"><CircleCheck /></el-icon>
            <div class="kpi-info">
              <div class="kpi-num">{{ completedCount }}</div>
              <div class="kpi-title">已完工工单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="kpi-card glass-card" shadow="hover">
          <div class="kpi-body">
            <el-icon class="kpi-icon text-primary"><Notebook /></el-icon>
            <div class="kpi-info">
              <div class="kpi-num">{{ totalCount }}</div>
              <div class="kpi-title">分配工单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Main Workspace Tabs -->
    <el-tabs v-model="activeTab" class="worker-tabs glass-card">
      <el-tab-pane label="我的待办" name="pending">
        <div v-loading="loading" class="tab-content">
          <el-empty v-if="!pendingTasks.length" description="暂无待指派或处理中的工单" />
          <el-row v-else :gutter="20">
            <el-col :span="12" v-for="item in pendingTasks" :key="item.repairId" class="mb20">
              <el-card class="task-card" shadow="hover">
                <template #header>
                  <div class="task-header">
                    <span class="task-title">{{ item.repairTitle }}</span>
                    <el-tag type="warning" size="small" effect="dark">处理中</el-tag>
                  </div>
                </template>
                <div class="task-info">
                  <p><strong>报修小区:</strong> {{ item.communityName || '-' }} (ID: {{ item.communityId }})</p>
                  <p><strong>报修业主:</strong> {{ item.ownerName || '-' }} (ID: {{ item.ownerId }})</p>
                  <p><strong>详细诉求:</strong> {{ item.repairContent }}</p>
                  <p><strong>指派时间:</strong> {{ parseTime(item.assignTime) }}</p>
                </div>
                <div class="task-actions">
                  <el-button type="success" icon="Check" @click="handleFinish(item)">确认完工</el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-tab-pane>

      <el-tab-pane label="历史工单" name="completed">
        <div v-loading="loading" class="tab-content">
          <el-empty v-if="!completedTasks.length" description="暂无历史完工记录" />
          <el-table v-else :data="completedTasks" stripe border>
            <el-table-column label="工单ID" align="center" prop="repairId" width="90" />
            <el-table-column label="报修标题" align="center" prop="repairTitle" show-overflow-tooltip />
            <el-table-column label="报修内容" align="center" prop="repairContent" show-overflow-tooltip />
            <el-table-column label="业主ID" align="center" prop="ownerId" width="90" />
            <el-table-column label="指派时间" align="center" prop="assignTime" width="160">
              <template #default="scope"><span>{{ parseTime(scope.row.assignTime) }}</span></template>
            </el-table-column>
            <el-table-column label="完工时间" align="center" prop="finishTime" width="160">
              <template #default="scope"><span>{{ parseTime(scope.row.finishTime) }}</span></template>
            </el-table-column>
            <el-table-column label="状态" align="center" width="100">
              <template #default="scope">
                <el-tag type="success" effect="dark">已完工</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup name="MyTask">
import request from "@/utils/request";

const { proxy } = getCurrentInstance();
const loading = ref(false);
const activeTab = ref("pending");

const pendingTasks = ref([]);
const completedTasks = ref([]);

const pendingCount = computed(() => pendingTasks.value.length);
const completedCount = computed(() => completedTasks.value.length);
const totalCount = computed(() => pendingTasks.value.length + completedTasks.value.length);

function getTasks() {
  loading.value = true;
  request({ url: '/property/portal/worker/list', method: 'get' }).then(res => {
    const list = res.rows || [];
    pendingTasks.value = list.filter(item => item.repairStatus === '1');
    completedTasks.value = list.filter(item => item.repairStatus === '2');
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function handleFinish(row) {
  proxy.$modal.confirm(`确认工单【${row.repairTitle}】已经完成维修并确认完工吗？`).then(() => {
    request({
      url: `/property/portal/worker/finish/${row.repairId}`,
      method: 'put'
    }).then(() => {
      proxy.$modal.msgSuccess("工单完工确认成功");
      getTasks();
    });
  }).catch(() => {});
}

getTasks();
</script>

<style scoped>
.worker-container {
  padding: 24px;
}
.glass-card {
  background: rgba(255, 255, 255, 0.8) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 16px !important;
  overflow: hidden;
}
.kpi-card {
  min-height: 100px;
}
.kpi-body {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}
.kpi-icon {
  font-size: 40px;
  padding: 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.6);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}
.kpi-info {
  display: flex;
  flex-direction: column;
}
.kpi-num {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
}
.kpi-title {
  margin-top: 6px;
  font-size: 13px;
  color: #7f8c8d;
  font-weight: 500;
}
.text-warning { color: #f39c12; }
.text-success { color: #2ecc71; }
.text-primary { color: #3498db; }

.worker-tabs {
  padding: 20px;
  min-height: 500px;
  margin-top: 10px;
}
.tab-content {
  padding: 15px 0;
}
.task-card {
  border-radius: 12px;
  border: 1px solid #ebeef5;
  transition: all 0.3s;
}
.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}
.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.task-title {
  font-weight: 600;
  color: #2c3e50;
  font-size: 15px;
}
.task-info {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 15px;
}
.task-info p {
  margin: 6px 0;
}
.task-actions {
  display: flex;
  justify-content: flex-end;
}
.mb20 { margin-bottom: 20px; }
</style>
