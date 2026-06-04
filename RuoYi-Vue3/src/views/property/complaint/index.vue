<template>
  <div class="app-container">
    <!-- Top KPI Stats Dashboard -->
    <el-row :gutter="16" class="mb20 dashboard-row">
      <!-- Card 1: Red/Orange -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card blue-card" style="background: linear-gradient(135deg, #EF4444 0%, #B91C1C 100%);">
          <div class="kpi-icon"><el-icon><Warning /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">待回复投诉</div>
            <div class="kpi-value">{{ stats.pending }} 件</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card orange-card" style="background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);">
          <div class="kpi-icon"><el-icon><ChatLineRound /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">处理中建议</div>
            <div class="kpi-value">{{ stats.active }} 件</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card green-card">
          <div class="kpi-icon"><el-icon><CircleCheck /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">本月回复率</div>
            <div class="kpi-value">{{ stats.rate }}%</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card purple-card">
          <div class="kpi-icon"><el-icon><Star /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">业主满意度</div>
            <div class="kpi-value">{{ stats.rate }} 分</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="投诉类型" prop="complaintType">
        <el-select v-model="queryParams.complaintType" placeholder="请选择" clearable style="width:120px">
          <el-option label="投诉" value="0" />
          <el-option label="建议" value="1" />
          <el-option label="表扬" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-select v-model="queryParams.handleStatus" placeholder="请选择处理状态" clearable style="width:120px">
          <el-option v-for="dict in pms_complaint_status" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="list" border stripe>
      <el-table-column label="编号" align="center" prop="complaintId" width="80" />
      <el-table-column label="类型" align="center" prop="complaintType" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.complaintType === '0' ? 'danger' : scope.row.complaintType === '1' ? 'warning' : 'success'">
            {{ scope.row.complaintType === '0' ? '投诉' : scope.row.complaintType === '1' ? '建议' : '表扬' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="complaintTitle" show-overflow-tooltip />
      <el-table-column label="业主" align="center" prop="ownerName" width="100" />
      <el-table-column label="状态" align="center" prop="handleStatus" width="100">
        <template #default="scope">
          <dict-tag :options="pms_complaint_status" :value="scope.row.handleStatus" />
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleProcess(scope.row)" v-hasPermi="['property:complaint:edit']">处理</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:complaint:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-descriptions title="投诉详情" :column="2" border style="margin-bottom: 20px;">
        <el-descriptions-item label="投诉标题" :span="2">{{ form.complaintTitle }}</el-descriptions-item>
        <el-descriptions-item label="投诉类型">
          <el-tag size="small">{{ form.complaintType === '0' ? '投诉' : form.complaintType === '1' ? '建议' : '表扬' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(form.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="投诉内容" :span="2">{{ form.complaintContent }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="处理状态" prop="handleStatus">
          <el-radio-group v-model="form.handleStatus">
            <el-radio label="1">处理中</el-radio>
            <el-radio label="2">已回复</el-radio>
            <el-radio label="3">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="form.handleResult" type="textarea" :rows="3" placeholder="请输入处理意见或回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Complaint">
import { listComplaint, getComplaint, delComplaint, addComplaint, updateComplaint } from "@/api/property/complaint";
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const { pms_complaint_status } = proxy.useDict('pms_complaint_status');
const list = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const title = ref('');
const ids = ref([]);

const stats = ref({
  pending: 0,
  active: 0,
  completed: 0,
  rate: 0
});

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, complaintType: undefined, handleStatus: undefined },
  rules: {
    handleStatus: [{ required: true, message: '请选择处理状态', trigger: 'change' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listComplaint(queryParams.value).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
    getStats();
  }).catch(() => { loading.value = false; });
}

function getStats() {
  request({ url: '/property/dashboard/todoSummary', method: 'get' }).then(res => {
    if (res.data) {
      stats.value.pending = res.data.pendingComplaints || 0;
    }
  });
}

function cancel() { open.value = false; reset(); }
function reset() { form.value = { complaintId: undefined, handleStatus: '1', handleResult: '' }; proxy.resetForm('formRef'); }
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }

function handleProcess(row) {
  reset();
  getComplaint(row.complaintId).then(res => {
    form.value = res.data;
    if (form.value.handleStatus === '0') {
      form.value.handleStatus = '1';
    }
    open.value = true;
    title.value = '处理投诉建议';
  });
}
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      request({ url: '/property/complaint/handle', method: 'put', data: form.value }).then(() => {
        proxy.$modal.msgSuccess('处理成功');
        open.value = false;
        getList();
      });
    }
  });
}
function handleDelete(row) {
  proxy.$modal.confirm('确认删除？').then(() => {
    return delComplaint(row.complaintId);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess('删除成功');
  }).catch(() => {});
}

getList();
</script>

<style scoped>
.dashboard-row {
  margin-bottom: 24px;
}
.kpi-card {
  display: flex;
  align-items: center;
  padding: 24px;
  border-radius: 12px;
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  overflow: hidden;
  height: 110px;
}
.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.16);
}
.kpi-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  transform: rotate(45deg);
}

.blue-card {
  background: linear-gradient(135deg, #3B82F6 0%, #1D4ED8 100%);
}
.green-card {
  background: linear-gradient(135deg, #10B981 0%, #047857 100%);
}
.orange-card {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
}
.purple-card {
  background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
}

.kpi-icon {
  font-size: 38px;
  margin-right: 18px;
  opacity: 0.85;
}
.kpi-content {
  flex-grow: 1;
}
.kpi-title {
  font-size: 13px;
  text-transform: uppercase;
  opacity: 0.85;
  margin-bottom: 4px;
  font-weight: 500;
}
.kpi-value {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 2px;
}
.kpi-badge {
  font-size: 11px;
  background: rgba(255, 255, 255, 0.22);
  display: inline-block;
  padding: 1px 6px;
  border-radius: 4px;
  font-weight: 600;
}
</style>
