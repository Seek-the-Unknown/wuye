<template>
  <div class="app-container fee-container">
    <!-- Top KPI Stats Dashboard -->
    <el-row :gutter="16" class="mb20 dashboard-row">
      <!-- Card 1: Blue -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card blue-card">
          <div class="kpi-icon"><el-icon><Money /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">本月应收总额</div>
            <div class="kpi-value">¥ {{ parseFloat(stats.totalPayable).toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2}) }}</div>
            <div class="kpi-badge">应收总额</div>
          </div>
        </div>
      </el-col>
      <!-- Card 2: Green -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card green-card">
          <div class="kpi-icon"><el-icon><CircleCheck /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">本月实收总额</div>
            <div class="kpi-value">¥ {{ parseFloat(stats.totalPaid).toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2}) }}</div>
            <div class="kpi-badge">收缴率 {{ stats.payRate }}%</div>
          </div>
        </div>
      </el-col>
      <!-- Card 3: Orange -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card orange-card">
          <div class="kpi-icon"><el-icon><Warning /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">待收金额</div>
            <div class="kpi-value">¥ {{ parseFloat(stats.totalUnpaid).toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2}) }}</div>
            <div class="kpi-badge">涉及 {{ stats.unpaidHouseholds }} 户</div>
          </div>
        </div>
      </el-col>
      <!-- Card 4: Purple -->
      <el-col :xs="24" :sm="12" :md="6">
        <div class="kpi-card purple-card">
          <div class="kpi-icon"><el-icon><Document /></el-icon></div>
          <div class="kpi-content">
            <div class="kpi-title">今日收款笔数</div>
            <div class="kpi-value">{{ stats.todayPaidCount }} 笔</div>
            <div class="kpi-badge">合计 ¥{{ parseFloat(stats.todayPaidAmount).toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2}) }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Filters Section -->
    <el-card class="filter-card mb20" shadow="hover">
      <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
        <el-form-item label="收费月份" prop="feeMonth">
          <el-date-picker v-model="queryParams.feeMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" style="width:140px" clearable @change="handleQuery" />
        </el-form-item>
        <el-form-item label="缴费状态" prop="payStatus">
          <el-select v-model="queryParams.payStatus" placeholder="请选择状态" clearable style="width:120px" @change="handleQuery">
            <el-option label="未缴" value="0" />
            <el-option label="已缴" value="1" />
            <el-option label="部分缴" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型" prop="feeTypeId">
          <el-select v-model="queryParams.feeTypeId" placeholder="费用类型" clearable style="width:150px" @change="handleQuery">
            <el-option v-for="item in feeTypeOptions" :key="item.feeTypeId" :label="item.typeName" :value="item.feeTypeId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>

        <div class="actions-right">
          <el-button type="primary" color="#3B82F6" icon="Plus" @click="handleOpenBulkBilling">生成本月账单</el-button>
          <el-button type="success" color="#8B5CF6" icon="Message" @click="handleBulkReminder">批量发送提醒</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- Data Table Card -->
    <el-card shadow="hover" class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title"><el-icon><List /></el-icon> 账单明细列表</span>
          <div class="table-actions">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:feeRecord:add']">新增账单</el-button>
            <el-button type="success" plain icon="CreditCard" :disabled="multiple" @click="handleBulkPay" v-hasPermi="['property:feeRecord:edit']">批量收款</el-button>
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:feeRecord:remove']">删除</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange" stripe border class="custom-table">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="账单单号" align="center" prop="recordId" width="90" />
        <el-table-column label="小区" align="center" prop="communityName" min-width="120" show-overflow-tooltip />
        <el-table-column label="房产信息" align="center" width="130">
          <template #default="scope">
            <span>{{ scope.row.roomName }}</span>
            <span v-if="scope.row.ownerName" class="owner-tag">({{ scope.row.ownerName }})</span>
          </template>
        </el-table-column>
        <el-table-column label="费用类型" align="center" prop="typeName" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.typeName === '停车费' ? 'warning' : 'primary'" effect="plain">{{ scope.row.typeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账单周期" align="center" prop="feeMonth" width="100" />
        <el-table-column label="金额(元)" align="center" prop="feeAmount" width="110">
          <template #default="scope"><span class="amount-text">¥ {{ scope.row.feeAmount }}</span></template>
        </el-table-column>
        <el-table-column label="已缴金额" align="center" prop="paidAmount" width="110">
          <template #default="scope">
            <span :class="scope.row.payStatus === '1' ? 'paid-text' : 'unpaid-text'">¥ {{ scope.row.paidAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="payStatus" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.payStatus === '0' ? 'danger' : scope.row.payStatus === '1' ? 'success' : 'warning'" effect="dark">
              {{ scope.row.payStatus === '0' ? '待支付' : scope.row.payStatus === '1' ? '已支付' : '部分缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费时间" align="center" prop="payTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.payTime) || '-' }}</span></template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.payStatus === '0'" link type="primary" icon="CreditCard" @click="handleQuickPay(scope.row)">收款</el-button>
            <el-button v-if="scope.row.payStatus === '1'" link type="success" icon="Printer" @click="handlePrintReceipt(scope.row)">打印凭证</el-button>
            <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:feeRecord:edit']">编辑</el-button>
            <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:feeRecord:remove']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- Create Monthly Bill Dialog -->
    <el-dialog title="一键批量生成账单" v-model="bulkBillOpen" width="400px" append-to-body>
      <el-form label-width="100px" style="padding: 10px 0;">
        <el-form-item label="生成月份">
          <el-date-picker v-model="bulkBillMonth" type="month" value-format="YYYY-MM" placeholder="选择收费月份" style="width: 100%" />
        </el-form-item>
        <div style="font-size: 13px; color: #909399; padding-left: 10px;">
          <el-icon><InfoFilled /></el-icon> 系统将为所有已入住或已售房屋批量生成此月份的<b>物业管理费</b>（以建筑面积 * 2.5元/㎡/月标准计算，不重复生成）。
        </div>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitBulkBilling" :loading="bulkBillingLoading">生成账单</el-button>
        <el-button @click="bulkBillOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <!-- Simulated SMS Reminders Logs Dialog -->
    <el-dialog title="短信催缴执行日志 (模拟发送)" v-model="reminderOpen" width="600px" append-to-body>
      <div class="reminder-log-container">
        <el-scrollbar max-height="300px">
          <div v-for="(log, idx) in reminderLogs" :key="idx" class="reminder-log-item">
            <el-tag type="info" size="small" class="mr5">{{ idx + 1 }}</el-tag>
            <span class="log-text">{{ log }}</span>
          </div>
        </el-scrollbar>
      </div>
      <template #footer>
        <el-button type="primary" @click="reminderOpen = false">我知道了</el-button>
      </template>
    </el-dialog>

    <!-- Quick Pay / Add Bill Form Dialog -->
    <el-dialog :title="title" v-model="open" width="560px" append-to-body draggable>
      <el-form ref="recordRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="小区" prop="communityId">
              <el-select v-model="form.communityId" placeholder="请选择小区" filterable style="width:100%">
                <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用类型" prop="feeTypeId">
              <el-select v-model="form.feeTypeId" placeholder="请选择费用类型" filterable style="width:100%">
                <el-option v-for="item in feeTypeOptions" :key="item.feeTypeId" :label="item.typeName" :value="item.feeTypeId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="对应房屋" prop="roomId">
              <el-select v-model="form.roomId" placeholder="选择或搜索房屋" filterable clearable style="width:100%" @change="handleRoomChange">
                <el-option v-for="item in roomOptions" :key="item.roomId" :label="item.roomName" :value="item.roomId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费业主" prop="ownerId">
              <el-select v-model="form.ownerId" placeholder="选择或搜索业主" filterable clearable style="width:100%">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="item.ownerName" :value="item.ownerId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费月份" prop="feeMonth">
              <el-date-picker v-model="form.feeMonth" type="month" value-format="YYYY-MM" placeholder="选择月份" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应缴金额" prop="feeAmount">
              <el-input-number v-model="form.feeAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="已缴金额" prop="paidAmount">
              <el-input-number v-model="form.paidAmount" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费状态" prop="payStatus">
              <el-select v-model="form.payStatus" style="width:100%">
                <el-option label="未缴" value="0" />
                <el-option label="已缴" value="1" />
                <el-option label="部分缴" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="缴费电子收据凭证" v-model="receiptOpen" width="650px" append-to-body class="receipt-dialog">
      <div id="receipt-print-area" class="receipt-voucher">
        <div class="receipt-watermark">已收款</div>
        <h2 class="receipt-header">物业收费收据</h2>
        <div class="receipt-meta">
          <span><b>凭证单号:</b> BILL-{{ selectedReceipt.recordId }}-{{ new Date().getTime().toString().substr(-5) }}</span>
          <span><b>开票时间:</b> {{ parseTime(new Date()) }}</span>
        </div>
        <table class="receipt-table">
          <tbody>
            <tr>
              <td class="td-label">交款人姓名</td>
              <td>{{ selectedReceipt.ownerName }}</td>
              <td class="td-label">对应房号</td>
              <td>{{ selectedReceipt.roomName }}</td>
            </tr>
            <tr>
              <td class="td-label">所属小区</td>
              <td>{{ selectedReceipt.communityName }}</td>
              <td class="td-label">账单周期</td>
              <td>{{ selectedReceipt.feeMonth }}</td>
            </tr>
            <tr>
              <td class="td-label">收费项目</td>
              <td>{{ selectedReceipt.typeName }}</td>
              <td class="td-label">支付渠道</td>
              <td>在线支付</td>
            </tr>
            <tr>
              <td class="td-label">应收金额</td>
              <td class="bold">¥ {{ selectedReceipt.feeAmount }}</td>
              <td class="td-label">实收金额</td>
              <td class="bold paid-green">¥ {{ selectedReceipt.paidAmount }}</td>
            </tr>
            <tr>
              <td class="td-label">人民币大写</td>
              <td colspan="3" class="bold uppercase-money">{{ chinesePaidAmount }}</td>
            </tr>
            <tr>
              <td class="td-label">备注信息</td>
              <td colspan="3">{{ selectedReceipt.remark || '无' }}</td>
            </tr>
          </tbody>
        </table>
        <div class="receipt-footer">
          <span><b>收款单位:</b> 物业服务中心</span>
          <span><b>开票员:</b> admin</span>
        </div>
        <div class="receipt-stamp-circle">财务专用</div>
      </div>
      <template #footer>
        <el-button type="success" icon="Printer" @click="triggerPrint">直接打印收据</el-button>
        <el-button @click="receiptOpen = false">关闭预览</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="FeeRecord">
/**
 * 物业费用记录管理页面（管理员视角）
 *
 * 功能说明：
 * 1. 查看所有业主的账单列表，支持按月份、状态、类型筛选
 * 2. 新增/编辑/删除账单
 * 3. 一键生成本月账单（根据房屋面积 * 物业费单价）
 * 4. 快捷收款确认（模拟扫码收款）
 * 5. 打印缴费凭证
 * 6. 批量催缴短信提醒（模拟发送）
 */
import { listFeeRecord, getFeeRecord, addFeeRecord, updateFeeRecord, delFeeRecord } from '@/api/property/feeRecord';
import { listAllCommunity } from '@/api/property/community';
import { listAllFeeType } from '@/api/property/feeType';
import { listAllRoom } from '@/api/property/room';
import { listAllOwner } from '@/api/property/owner';
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const recordList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const communityOptions = ref([]);
const feeTypeOptions = ref([]);
const roomOptions = ref([]);
const ownerOptions = ref([]);

// ===== KPI 统计卡片数据 =====
const stats = ref({
  totalPayable: 0,
  totalPaid: 0,
  totalUnpaid: 0,
  payRate: 0,
  unpaidHouseholds: 0,
  todayPaidCount: 0,
  todayPaidAmount: 0
});

/**
 * 获取当前月份的字符串（YYYY-MM格式）
 * 用来默认选中当月
 */
const getCurrMonth = () => {
  const d = new Date();
  return d.getFullYear() + '-' + String(d.getMonth() + 1).padStart(2, '0');
};

// ===== 批量生成账单相关 =====
const bulkBillOpen = ref(false);
const bulkBillMonth = ref(getCurrMonth());
const bulkBillingLoading = ref(false);

// ===== 短信催缴相关 =====
const reminderOpen = ref(false);
const reminderLogs = ref([]);

// ===== 电子收据相关 =====
const receiptOpen = ref(false);
const selectedReceipt = ref({});
const chinesePaidAmount = ref('');

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, feeMonth: undefined, payStatus: undefined, feeTypeId: undefined },
  rules: {
    communityId: [{ required: true, message: '请选择小区', trigger: 'change' }],
    feeTypeId: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
    roomId: [{ required: true, message: '请选择对应房屋', trigger: 'change' }],
    ownerId: [{ required: true, message: '请选择收费业主', trigger: 'change' }],
    feeMonth: [{ required: true, message: '请选择收费月份', trigger: 'change' }],
    feeAmount: [{ required: true, message: '请填写应缴金额', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

/**
 * 获取当月统计面板数据
 * 调用后端API计算本月应收、实收、待收等指标
 */
function getStats() {
  const currentMonth = queryParams.value.feeMonth || getCurrMonth();
  request({
    url: '/property/feeRecord/stats',
    method: 'get',
    params: { month: currentMonth }
  }).then(res => {
    if (res.code === 200 || res.totalPayable !== undefined) {
      stats.value = res;
    }
  });
}

/** 分页查询账单列表 + 同时更新统计面板 */
function getList() {
  loading.value = true;
  listFeeRecord(queryParams.value).then(res => {
    recordList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
  getStats();
}

/** 加载下拉框数据（小区列表、费用类型列表、房屋列表、业主列表） */
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
  listAllFeeType().then(res => { feeTypeOptions.value = res.data || []; });
  listAllRoom().then(res => { roomOptions.value = res.rows || res.data || []; });
  listAllOwner().then(res => { ownerOptions.value = res.rows || res.data || []; });
}

/** 房屋选中值改变时的处理，用于自动联想对应的业主 */
function handleRoomChange(roomId) {
  // 根据历史缴费记录或账单列表查找该房屋的业主
  const matched = recordList.value.find(item => item.roomId === roomId && item.ownerId);
  if (matched) {
    form.value.ownerId = matched.ownerId;
  } else {
    // 也可以直接到 roomOptions 里看看有没有绑定的业主相关信息，或者保持空让用户手动选
  }
}

/** 一键批量收款逻辑 */
function handleBulkPay() {
  const selectIds = ids.value;
  if (!selectIds || selectIds.length === 0) {
    proxy.$modal.msgError("请先勾选需要收款的账单记录");
    return;
  }
  proxy.$modal.confirm(`确认对选中的 ${selectIds.length} 笔未缴账单进行一键批量收款？`).then(() => {
    loading.value = true;
    return request({
      url: '/property/feeRecord/payBatch',
      method: 'post',
      params: { recordIds: selectIds.join(',') }
    });
  }).then(res => {
    proxy.$modal.msgSuccess(res.msg || "批量收款成功！");
    getList();
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { recordId: undefined, communityId: undefined, roomId: undefined, ownerId: undefined, feeTypeId: undefined, feeAmount: 0, paidAmount: 0, feeMonth: undefined, payStatus: '0', remark: undefined };
  proxy.resetForm('recordRef');
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.recordId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

function handleAdd() { reset(); loadOptions(); open.value = true; title.value = '新增费用账单'; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const recordId = row.recordId || ids.value;
  getFeeRecord(recordId).then(res => { form.value = res.data; open.value = true; title.value = '修改/缴费'; });
}

function submitForm() {
  proxy.$refs['recordRef'].validate(valid => {
    if (valid) {
      if (form.value.recordId != undefined) {
        updateFeeRecord(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList(); });
      } else {
        addFeeRecord(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList(); });
      }
    }
  });
}

function handleDelete(row) {
  const recordIds = row.recordId || ids.value;
  proxy.$modal.confirm('是否确认删除？').then(() => delFeeRecord(recordIds)).then(() => { getList(); proxy.$modal.msgSuccess('删除成功'); }).catch(() => {});
}

// Bulk generate billing records
function handleOpenBulkBilling() {
  bulkBillMonth.value = queryParams.value.feeMonth || getCurrMonth();
  bulkBillOpen.value = true;
}

function submitBulkBilling() {
  if (!bulkBillMonth.value) {
    proxy.$modal.msgError("请先选择收费月份");
    return;
  }
  bulkBillingLoading.value = true;
  request({
    url: '/property/feeRecord/generateMonthly',
    method: 'post',
    params: { month: bulkBillMonth.value }
  }).then(res => {
    proxy.$modal.msgSuccess(res.msg || "批量生成成功！");
    bulkBillOpen.value = false;
    bulkBillingLoading.value = false;
    getList();
  }).catch(() => { bulkBillingLoading.value = false; });
}

// Simulated bulk reminders
function handleBulkReminder() {
  const month = queryParams.value.feeMonth || getCurrMonth();
  proxy.$modal.confirm(`确认对 ${month} 月未缴纳物业费的业主进行短信一键催缴？`).then(() => {
    return request({
      url: '/property/feeRecord/remindAll',
      method: 'post',
      params: { month: month }
    });
  }).then(res => {
    proxy.$modal.msgSuccess(res.msg || "短信已成功发布！");
    reminderLogs.value = res.logs || [];
    reminderOpen.value = true;
  }).catch(() => {});
}

// Quick Payment
function handleQuickPay(row) {
  proxy.$modal.confirm(`确认对房号为 【${row.roomName} (${row.ownerName || '业主'})】 的 ${row.typeName} 进行快捷扫码/现金收款？金额：¥${row.feeAmount}`).then(() => {
    return request({
      url: '/property/feeRecord/payQuick',
      method: 'post',
      params: { recordId: row.recordId }
    });
  }).then(() => {
    proxy.$modal.msgSuccess("收款录入成功！");
    getList();
  }).catch(() => {});
}

// Print receipt preview
function handlePrintReceipt(row) {
  selectedReceipt.value = row;
  chinesePaidAmount.value = digitToChinese(row.paidAmount);
  receiptOpen.value = true;
}

function triggerPrint() {
  const printContent = document.getElementById("receipt-print-area").innerHTML;
  const originalContent = document.body.innerHTML;
  
  // Set printable view with inline style
  document.body.innerHTML = `
    <div style="padding: 20px; font-family: SimSun, 'STSong', serif;">
      ${printContent}
    </div>
  `;
  window.print();
  
  // Restore screen layout
  window.location.reload();
}

// Digit conversion to Chinese uppercase money string
function digitToChinese(n) {
  const fraction = ['角', '分'];
  const digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
  const unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']];
  let num = Math.abs(n);
  let s = '';
  for (let i = 0; i < fraction.length; i++) {
    s += (digit[Math.floor(num * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
  }
  s = s || '整';
  num = Math.floor(num);
  for (let i = 0; i < unit[0].length && num > 0; i++) {
    let p = '';
    for (let j = 0; j < unit[1].length && num > 0; j++) {
      p = digit[num % 10] + unit[1][j] + p;
      num = Math.floor(num / 10);
    }
    s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
  }
  return s.replace(/(零.)*零元/, '元').replace(/(零.)+/g, '零').replace(/^整$/, '零元整');
}

getList();
loadOptions();
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

/* Filters & Actions */
.filter-card {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}
.filter-card :deep(.el-form-item) {
  margin-bottom: 0;
}
.actions-right {
  float: right;
  display: flex;
  gap: 8px;
}

/* Custom Table */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-weight: 600;
  font-size: 16px;
  color: #1E293B;
  display: flex;
  align-items: center;
  gap: 6px;
}
.owner-tag {
  color: #64748B;
  font-size: 12px;
  margin-left: 4px;
}
.amount-text {
  font-weight: 600;
  color: #334155;
}
.paid-text {
  color: #10B981;
  font-weight: 600;
}
.unpaid-text {
  color: #EF4444;
  font-weight: 600;
}

/* SMS reminder log */
.reminder-log-container {
  background: #0F172A;
  padding: 16px;
  border-radius: 6px;
  color: #38BDF8;
  font-family: Consolas, monospace;
  font-size: 12px;
}
.reminder-log-item {
  margin-bottom: 8px;
  line-height: 1.5;
  border-bottom: 1px solid #1E293B;
  padding-bottom: 6px;
}
.reminder-log-item:last-child {
  border-bottom: none;
}
.log-text {
  color: #E2E8F0;
}

/* Receipt styling */
.receipt-voucher {
  border: 2px double #64748B;
  padding: 24px;
  position: relative;
  background-color: #FFFFFF;
  font-family: SimSun, 'STSong', serif;
  color: #000;
}
.receipt-watermark {
  position: absolute;
  top: 35%;
  left: 30%;
  transform: rotate(-30deg);
  font-size: 80px;
  color: rgba(16, 185, 129, 0.15);
  font-weight: bold;
  border: 4px double rgba(16, 185, 129, 0.15);
  padding: 5px 20px;
  border-radius: 8px;
  pointer-events: none;
  z-index: 1;
}
.receipt-header {
  text-align: center;
  font-size: 22px;
  letter-spacing: 2px;
  margin-bottom: 18px;
  border-bottom: 1px solid #64748B;
  padding-bottom: 8px;
  font-weight: bold;
  color: #000;
}
.receipt-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  margin-bottom: 8px;
  color: #334155;
}
.receipt-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 16px;
  z-index: 5;
  position: relative;
}
.receipt-table td {
  border: 1px solid #000;
  padding: 10px 12px;
  font-size: 14px;
  color: #000;
}
.td-label {
  background-color: #F8FAFC;
  font-weight: bold;
  width: 110px;
  text-align: center;
}
.bold {
  font-weight: bold;
}
.paid-green {
  color: #047857;
  font-weight: bold;
}
.uppercase-money {
  letter-spacing: 1px;
}
.receipt-footer {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  border-top: 1px dashed #64748B;
  padding-top: 12px;
  color: #334155;
}
.receipt-stamp-circle {
  position: absolute;
  bottom: 20px;
  right: 60px;
  width: 90px;
  height: 90px;
  border: 2px solid rgba(239, 68, 68, 0.7);
  border-radius: 50%;
  color: rgba(239, 68, 68, 0.7);
  text-align: center;
  line-height: 90px;
  font-weight: bold;
  font-size: 14px;
  transform: rotate(-15deg);
  pointer-events: none;
  letter-spacing: 1px;
  background-color: transparent;
}
</style>
