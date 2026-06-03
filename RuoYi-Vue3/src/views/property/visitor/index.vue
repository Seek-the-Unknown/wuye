<template>
  <div class="app-container">
    <!-- Top KPI Stats Dashboard -->
    <el-row :gutter="16" class="mb20 dashboard-row">
      <!-- Card 1: Blue -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card blue-card">
          <div class="kpi-icon"><el-icon><Calendar /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">今日预约人数</div>
            <div class="kpi-value">{{ stats.today }} 人</div>
          </div>
        </div>
      </el-col>
      <!-- Card 2: Green -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card green-card">
          <div class="kpi-icon"><el-icon><User /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">当前在访人数</div>
            <div class="kpi-value">{{ stats.active }} 人</div>
          </div>
        </div>
      </el-col>
      <!-- Card 3: Orange -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card orange-card">
          <div class="kpi-icon"><el-icon><Warning /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">待审核访客</div>
            <div class="kpi-value">{{ stats.pending }} 人</div>
          </div>
        </div>
      </el-col>
      <!-- Card 4: Purple -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card purple-card">
          <div class="kpi-icon"><el-icon><Checked /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">本月累计访客</div>
            <div class="kpi-value">{{ stats.total }} 人次</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="访客姓名" prop="visitorName">
        <el-input v-model="queryParams.visitorName" placeholder="请输入访客姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="访客电话" prop="visitorPhone">
        <el-input v-model="queryParams.visitorPhone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="访问状态" prop="visitStatus">
        <el-select v-model="queryParams.visitStatus" placeholder="请选择状态" clearable style="width:120px">
          <el-option label="待审核" value="0" />
          <el-option label="已放行" value="1" />
          <el-option label="已离开" value="2" />
          <el-option label="已拒绝" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:visitor:add']">登记访客</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:visitor:edit']">处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:visitor:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="visitorList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="访客姓名" align="center" prop="visitorName" width="100" />
      <el-table-column label="访客电话" align="center" prop="visitorPhone" width="130" />
      <el-table-column label="所属小区" align="center" prop="communityName" min-width="100" show-overflow-tooltip />
      <el-table-column label="被访房屋" align="center" prop="visitRoom" width="110" />
      <el-table-column label="被访业主" align="center" prop="ownerName" width="90" />
      <el-table-column label="来访事由" align="center" prop="visitReason" min-width="120" show-overflow-tooltip />
      <el-table-column label="来访时间" align="center" prop="visitTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.visitTime) }}</span></template>
      </el-table-column>
      <el-table-column label="离开时间" align="center" prop="leaveTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.leaveTime) || '-' }}</span></template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="visitStatus" width="100">
        <template #default="scope">
          <el-tag :type="statusType(scope.row.visitStatus)" effect="dark">
            {{ statusLabel(scope.row.visitStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <el-button link type="success" icon="Check" @click="quickAction(scope.row, '1')" v-if="scope.row.visitStatus === '0'" v-hasPermi="['property:visitor:edit']">放行</el-button>
          <el-button link type="warning" icon="Close" @click="quickAction(scope.row, '3')" v-if="scope.row.visitStatus === '0'" v-hasPermi="['property:visitor:edit']">拒绝</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:visitor:edit']">编辑</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:visitor:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="580px" append-to-body draggable>
      <el-form ref="visitorRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="被访房屋/业主" prop="ownerId">
              <el-select v-model="selectedHouseIndex" filterable placeholder="请选择对应小区/楼栋/房号及业主" style="width: 100%" @change="handleHouseChange">
                <el-option
                  v-for="(house, index) in houseOptions"
                  :key="index"
                  :label="`${house.community} - ${house.ownerName}`"
                  :value="index"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属小区" prop="communityId">
              <el-select v-model="form.communityId" placeholder="选择房屋后自动关联小区" filterable style="width:100%">
                <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访客姓名" prop="visitorName">
              <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访客电话" prop="visitorPhone">
              <el-input v-model="form.visitorPhone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" placeholder="可选填" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来访时间" prop="visitTime">
              <el-date-picker v-model="form.visitTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择时间" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="离开时间" prop="leaveTime">
              <el-date-picker v-model="form.leaveTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="可选填" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访问状态" prop="visitStatus">
              <el-select v-model="form.visitStatus" style="width:100%">
                <el-option label="待审核" value="0" />
                <el-option label="已放行" value="1" />
                <el-option label="已离开" value="2" />
                <el-option label="已拒绝" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="来访事由" prop="visitReason">
              <el-input v-model="form.visitReason" type="textarea" :rows="2" placeholder="请输入来访事由" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Visitor">
import { listVisitor, getVisitor, addVisitor, updateVisitor, delVisitor } from '@/api/property/visitor';
import { listAllCommunity } from '@/api/property/community';
import request from '@/utils/request';
import { computed } from 'vue';

const { proxy } = getCurrentInstance();
const visitorList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const communityOptions = ref([]);
const selectedHouseIndex = ref(null);

const houseOptions = ref([]);

function loadHouseOptions() {
  request({ url: '/property/owner/listAll', method: 'get' }).then(res => {
    houseOptions.value = (res.data || []).map(o => ({
      ownerId: o.ownerId,
      communityId: o.communityId,
      community: o.communityName || '',
      building: '',
      room: '',
      ownerName: o.ownerName
    }));
  });
}

function handleHouseChange(index) {
  const match = houseOptions.value[index];
  if (match) {
    form.value.ownerId = match.ownerId;
    form.value.communityId = match.communityId;
    form.value.ownerName = match.ownerName;
  }
}

const stats = computed(() => {
  let pending = 0;
  let active = 0;
  let today = 0;
  let totalCount = visitorList.value.length;
  
  visitorList.value.forEach(item => {
    if (item.visitStatus === '0') pending++;
    else if (item.visitStatus === '1') active++;
    
    if (item.visitTime) {
      const todayStr = new Date().toISOString().substring(0, 10);
      if (item.visitTime.startsWith(todayStr)) {
        today++;
      }
    }
  });
  
  return {
    today: today,
    active: active,
    pending: pending,
    total: totalCount
  };
});

const statusMap = { '0': { label: '待审核', type: 'warning' }, '1': { label: '已放行', type: 'success' }, '2': { label: '已离开', type: 'info' }, '3': { label: '已拒绝', type: 'danger' } };
const statusLabel = (s) => statusMap[s]?.label || '-';
const statusType = (s) => statusMap[s]?.type || '';

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, visitorName: undefined, visitorPhone: undefined, visitStatus: undefined },
  rules: {
    communityId: [{ required: true, message: '请选择小区', trigger: 'change' }],
    ownerId: [{ required: true, message: '请选择被访房产及业主', trigger: 'change' }],
    visitorName: [{ required: true, message: '访客姓名不能为空', trigger: 'blur' }],
    visitorPhone: [{ required: true, message: '手机号不能为空', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listVisitor(queryParams.value).then(res => {
    visitorList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
}
function cancel() { open.value = false; reset(); }
function reset() {
  selectedHouseIndex.value = null;
  form.value = { visitorId: undefined, communityId: undefined, visitorName: undefined, visitorPhone: undefined, idCard: undefined, visitRoom: undefined, ownerId: null, visitReason: undefined, visitTime: undefined, leaveTime: undefined, visitStatus: '0' };
  proxy.resetForm('visitorRef');
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.visitorId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); loadHouseOptions(); open.value = true; title.value = '登记访客'; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const visitorId = row.visitorId || ids.value;
  getVisitor(visitorId).then(res => {
    form.value = res.data;
    // Map back to selectedHouseIndex
    const index = houseOptions.value.findIndex(h => h.ownerId === form.value.ownerId && form.value.visitRoom && form.value.visitRoom.includes(h.room));
    if (index !== -1) {
      selectedHouseIndex.value = index;
    } else {
      const ownerIndex = houseOptions.value.findIndex(h => h.ownerId === form.value.ownerId);
      if (ownerIndex !== -1) {
        selectedHouseIndex.value = ownerIndex;
      }
    }
    open.value = true;
    title.value = '修改访客信息';
  });
}
function submitForm() {
  proxy.$refs['visitorRef'].validate(valid => {
    if (valid) {
      if (form.value.visitorId != undefined) {
        updateVisitor(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList(); });
      } else {
        addVisitor(form.value).then(() => { proxy.$modal.msgSuccess('登记成功'); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const visitorIds = row.visitorId || ids.value;
  proxy.$modal.confirm('确认删除访客记录？').then(() => delVisitor(visitorIds)).then(() => { getList(); proxy.$modal.msgSuccess('删除成功'); }).catch(() => {});
}
function quickAction(row, status) {
  const map = { '1': '放行', '3': '拒绝' };
  proxy.$modal.confirm(`确认${map[status]}该访客？`).then(() => {
    updateVisitor({ visitorId: row.visitorId, visitStatus: status }).then(() => {
      proxy.$modal.msgSuccess(`操作成功`);
      getList();
    });
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
