<template>
  <div class="app-container">
    <!-- Top KPI Stats Dashboard -->
    <el-row :gutter="16" class="mb20 dashboard-row">
      <!-- Card 1: Blue -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card blue-card">
          <div class="kpi-icon"><el-icon><Warning /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">待处理报修</div>
            <div class="kpi-value">{{ stats.pending }} 件</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card orange-card">
          <div class="kpi-icon"><el-icon><Tools /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">进行中工单</div>
            <div class="kpi-value">{{ stats.processing }} 件</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card green-card">
          <div class="kpi-icon"><el-icon><CircleCheck /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">本月已完成</div>
            <div class="kpi-value">{{ stats.completed }} 件</div>
            <div class="kpi-badge">完成率 {{ stats.completionRate }}%</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card purple-card">
          <div class="kpi-icon"><el-icon><Odometer /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">总计工单</div>
            <div class="kpi-value">{{ stats.pending + stats.processing + stats.completed }} 件</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属小区" prop="communityId">
        <el-select v-model="queryParams.communityId" placeholder="请选择小区" clearable filterable style="width:150px">
          <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
        </el-select>
      </el-form-item>
      <el-form-item label="报修标题" prop="repairTitle">
        <el-input v-model="queryParams.repairTitle" placeholder="请输入报修标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="处理状态" prop="repairStatus">
        <el-select v-model="queryParams.repairStatus" placeholder="请选择状态" clearable style="width:120px">
          <el-option label="待处理" value="0" />
          <el-option label="处理中" value="1" />
          <el-option label="已完成" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:repair:add']">新增报修</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:repair:edit']">处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:repair:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="repairList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="报修ID" align="center" prop="repairId" width="80" />
      <el-table-column label="小区ID" align="center" prop="communityId" width="80" />
      <el-table-column label="业主ID" align="center" prop="ownerId" width="80" />
      <el-table-column label="报修标题" align="center" prop="repairTitle" min-width="150" show-overflow-tooltip>
        <template #default="scope">
          <el-link type="primary" @click="viewRepairDetail(scope.row)">{{ scope.row.repairTitle }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="报修内容" align="center" prop="repairContent" min-width="180" show-overflow-tooltip />
      <el-table-column label="指派工人" align="center" prop="workerName" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.workerName" type="info" effect="plain">{{ scope.row.workerName }}</el-tag>
          <span v-else style="color: #909399;">未指派</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="repairStatus" width="110">
        <template #default="scope">
          <el-tag :type="scope.row.repairStatus === '0' ? 'danger' : scope.row.repairStatus === '1' ? 'warning' : 'success'" effect="dark">
            {{ scope.row.repairStatus === '0' ? '待处理' : scope.row.repairStatus === '1' ? '处理中' : '已完成' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="报修时间" align="center" prop="createTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="warning" icon="Share" @click="handleAssign(scope.row)" v-if="scope.row.repairStatus === '0'" v-hasPermi="['property:repair:edit']">指派</el-button>
          <el-button link type="success" icon="Check" @click="quickProcess(scope.row, '2')" v-if="scope.row.repairStatus === '1'" v-hasPermi="['property:repair:edit']">完成</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:repair:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:repair:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body draggable>
      <el-form ref="repairRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属小区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择小区" filterable style="width:100%">
            <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主ID" prop="ownerId">
          <el-input-number v-model="form.ownerId" :min="1" placeholder="请输入业主ID" style="width:100%" />
        </el-form-item>
        <el-form-item label="报修标题" prop="repairTitle">
          <el-input v-model="form.repairTitle" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="报修内容" prop="repairContent">
          <el-input v-model="form.repairContent" type="textarea" :rows="4" placeholder="请详细描述报修问题" />
        </el-form-item>
        <el-form-item label="处理状态" prop="repairStatus">
          <el-select v-model="form.repairStatus" placeholder="请选择状态" style="width:100%">
            <el-option label="待处理" value="0" />
            <el-option label="处理中" value="1" />
            <el-option label="已完成" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 报修详情 -->
    <el-dialog title="报修详情" v-model="detailOpen" width="500px" append-to-body>
      <div v-if="currentRepair" class="repair-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="报修ID">{{ currentRepair.repairId }}</el-descriptions-item>
          <el-descriptions-item label="处理状态">
            <el-tag :type="currentRepair.repairStatus === '0' ? 'danger' : currentRepair.repairStatus === '1' ? 'warning' : 'success'" effect="dark">
              {{ currentRepair.repairStatus === '0' ? '待处理' : currentRepair.repairStatus === '1' ? '处理中' : '已完成' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="小区ID">{{ currentRepair.communityId }}</el-descriptions-item>
          <el-descriptions-item label="业主ID">{{ currentRepair.ownerId }}</el-descriptions-item>
          <el-descriptions-item label="指派工人" :span="2">
            <el-tag v-if="currentRepair.workerName" type="info">{{ currentRepair.workerName }}</el-tag>
            <span v-else style="color: #909399;">未指派</span>
          </el-descriptions-item>
          <el-descriptions-item label="报修标题" :span="2">{{ currentRepair.repairTitle }}</el-descriptions-item>
          <el-descriptions-item label="报修内容" :span="2">
            <div style="white-space:pre-wrap">{{ currentRepair.repairContent }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="指派时间" :span="2" v-if="currentRepair.assignTime">{{ parseTime(currentRepair.assignTime) }}</el-descriptions-item>
          <el-descriptions-item label="完工时间" :span="2" v-if="currentRepair.finishTime">{{ parseTime(currentRepair.finishTime) }}</el-descriptions-item>
          <el-descriptions-item label="提交时间" :span="2">{{ parseTime(currentRepair.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 指派派单弹窗 -->
    <el-dialog title="指派报修工单" v-model="assignOpen" width="400px" append-to-body>
      <el-form ref="assignFormRef" :model="assignForm" :rules="assignRules" label-width="80px">
        <el-form-item label="选择工人" prop="workerId">
          <el-select v-model="assignForm.workerId" placeholder="请选择负责的维修工人" filterable style="width: 100%">
            <el-option v-for="item in workerOptions" :key="item.userId" :label="item.nickName" :value="item.userId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAssign">确 定</el-button>
        <el-button @click="assignOpen = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Repair">
import { listRepair, getRepair, delRepair, addRepair, updateRepair } from "@/api/property/repair";
import { listAllCommunity } from "@/api/property/community";
import request from "@/utils/request";
import { computed } from "vue";

const { proxy } = getCurrentInstance();
const repairList = ref([]);
const open = ref(false);
const detailOpen = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const communityOptions = ref([]);
const currentRepair = ref(null);

const stats = computed(() => {
  let pending = 0;
  let processing = 0;
  let completed = 0;
  repairList.value.forEach(item => {
    if (item.repairStatus === '0') pending++;
    else if (item.repairStatus === '1') processing++;
    else if (item.repairStatus === '2') completed++;
  });
  
  const totalCount = pending + processing + completed;
  const rate = totalCount > 0 ? ((completed / totalCount) * 100).toFixed(1) : "0.0";
  
  return {
    pending: pending,
    processing: processing,
    completed: completed,
    completionRate: rate
  };
});

// 工人指派变量
const assignOpen = ref(false);
const workerOptions = ref([]);
const assignForm = ref({ repairId: undefined, workerId: undefined });
const assignRules = {
  workerId: [{ required: true, message: "请选择负责的维修工人", trigger: "change" }]
};

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, repairTitle: undefined, repairStatus: undefined, communityId: undefined },
  rules: {
    repairTitle: [{ required: true, message: "报修标题不能为空", trigger: "blur" }],
    repairContent: [{ required: true, message: "报修内容不能为空", trigger: "blur" }],
    communityId: [{ required: true, message: "请选择小区", trigger: "change" }],
    ownerId: [{ required: true, message: "业主ID不能为空", trigger: "blur" }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listRepair(queryParams.value).then(res => {
    repairList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
}
function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { repairId: undefined, communityId: undefined, ownerId: undefined, repairTitle: undefined, repairContent: undefined, repairStatus: "0" };
  proxy.resetForm("repairRef");
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.repairId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); open.value = true; title.value = "新增报修"; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const repairId = row.repairId || ids.value;
  getRepair(repairId).then(res => { form.value = res.data; open.value = true; title.value = "处理/修改报修"; });
}
function submitForm() {
  proxy.$refs["repairRef"].validate(valid => {
    if (valid) {
      if (form.value.repairId != undefined) {
        updateRepair(form.value).then(res => { proxy.$modal.msgSuccess("修改成功"); open.value = false; getList(); });
      } else {
        addRepair(form.value).then(res => { proxy.$modal.msgSuccess("新增成功"); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const repairIds = row.repairId || ids.value;
  proxy.$modal.confirm('确认删除？').then(() => delRepair(repairIds)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功"); }).catch(() => {});
}
function quickProcess(row, status) {
  const label = status === '1' ? '开始处理' : '标记完成';
  proxy.$modal.confirm(`确认${label}该报修工单？`).then(() => {
    updateRepair({ repairId: row.repairId, repairStatus: status }).then(() => {
      proxy.$modal.msgSuccess('操作成功');
      getList();
    });
  }).catch(() => {});
}
function viewRepairDetail(row) {
  currentRepair.value = row;
  detailOpen.value = true;
}

// 指派工单函数
function handleAssign(row) {
  assignForm.value.repairId = row.repairId;
  assignForm.value.workerId = undefined;
  request({ url: '/property/repair/workers', method: 'get' }).then(res => {
    workerOptions.value = res.data || [];
    assignOpen.value = true;
  });
}
function submitAssign() {
  proxy.$refs["assignFormRef"].validate(valid => {
    if (valid) {
      request({
        url: '/property/repair/assign',
        method: 'put',
        data: assignForm.value
      }).then(() => {
        proxy.$modal.msgSuccess("指派成功");
        assignOpen.value = false;
        getList();
      });
    }
  });
}

loadOptions();
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
