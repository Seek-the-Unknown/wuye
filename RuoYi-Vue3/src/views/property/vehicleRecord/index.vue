<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="车牌号" prop="plateNumber">
        <el-input v-model="queryParams.plateNumber" placeholder="请输入车牌号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="payStatus">
        <el-select v-model="queryParams.payStatus" placeholder="状态" clearable style="width:110px">
          <el-option label="在场" value="0" />
          <el-option label="已离场" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="车辆类型" prop="vehicleType">
        <el-select v-model="queryParams.vehicleType" placeholder="车辆类型" clearable style="width:110px">
          <el-option label="临时车" value="0" />
          <el-option label="月租车" value="1" />
          <el-option label="业主车" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="$router.push('/property/vehicleRecordEnter')">车辆入场</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:vehicleRecord:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange" stripe border style="width:100%">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="车牌号" align="center" prop="plateNumber" min-width="140">
        <template #default="scope">
          <span class="plate-number">{{ scope.row.plateNumber }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车辆类型" align="center" min-width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.vehicleType === '0'" type="info" size="small">临时车</el-tag>
          <el-tag v-else-if="scope.row.vehicleType === '1'" size="small">月租车</el-tag>
          <el-tag v-else-if="scope.row.vehicleType === '2'" type="success" size="small">业主车</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入场时间" align="center" prop="entryTime" min-width="170" />
      <el-table-column label="出场时间" align="center" prop="exitTime" min-width="170">
        <template #default="scope">
          <span v-if="scope.row.exitTime">{{ scope.row.exitTime }}</span>
          <el-tag v-else type="info" size="small">在场</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="停车时长" align="center" min-width="120">
        <template #default="scope">
          <template v-if="scope.row.payStatus === '0'">
            <span style="font-weight:bold;color:#1a56db">{{ liveDuration(scope.row) }}</span>
          </template>
          <template v-else>
            <span>{{ scope.row.parkingDuration }} 小时</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="计费单价" align="center" min-width="100">
        <template #default="scope">
          <span>{{ scope.row.unitPrice || defaultRate }} 元/小时</span>
        </template>
      </el-table-column>
      <el-table-column label="实收金额" align="center" min-width="110">
        <template #default="scope">
          <template v-if="scope.row.payStatus === '1'">
            <span style="color:#F56C6C;font-weight:bold;font-size:15px">¥{{ scope.row.paidAmount }}</span>
          </template>
          <template v-else>
            <el-tag type="warning" size="small">待结算</el-tag>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" min-width="90">
        <template #default="scope">
          <el-tag :type="scope.row.payStatus === '1' ? 'success' : ''" size="small">
            {{ scope.row.payStatus === '1' ? '已离场' : '在场' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="140" fixed="right">
        <template #default="scope">
          <el-button v-if="scope.row.payStatus === '0'" link type="success" icon="Money" @click="handleSettle(scope.row)">结算出场</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:vehicleRecord:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 结算出场对话框 -->
    <el-dialog title="结算出场" v-model="settleOpen" width="420px" append-to-body :close-on-click-modal="false">
      <div v-if="settleRecord" class="settle-content">
        <div class="plate-display">
          <span class="plate-number large">{{ settleRecord.plateNumber }}</span>
        </div>
        <el-descriptions :column="1" border size="small" style="margin:16px 0">
          <el-descriptions-item label="入场时间">{{ settleRecord.entryTime }}</el-descriptions-item>
          <el-descriptions-item label="停车时长">
            <span style="font-weight:bold;color:#1a56db">{{ settleDuration }} 小时</span>
          </el-descriptions-item>
          <el-descriptions-item label="计费单价">{{ settleUnitPrice }} 元/小时</el-descriptions-item>
          <el-descriptions-item label="应付金额">
            <span style="font-size:20px;font-weight:bold;color:#F56C6C">{{ settleFee }} 元</span>
          </el-descriptions-item>
        </el-descriptions>
        <el-form label-width="80px">
          <el-form-item label="实收金额">
            <el-input-number v-model="settlePaid" :min="0" :precision="2" style="width:160px" size="large" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="settleOpen = false">取消</el-button>
        <el-button type="primary" @click="confirmSettle" :loading="settling">确认收费 {{ settlePaid }} 元</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="VehicleRecord">
import { listVehicleRecord, delVehicleRecord, vehicleExit, getHourlyRate } from '@/api/property/vehicleRecord'

const { proxy } = getCurrentInstance()
const recordList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const settleOpen = ref(false)
const settleRecord = ref(null)
const settleDuration = ref(0)
const settleUnitPrice = ref(5)
const settleFee = ref(0)
const settlePaid = ref(0)
const settling = ref(false)
const now = ref(Date.now())
const defaultRate = ref(5)
let timer = null

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10, plateNumber: undefined, payStatus: undefined, vehicleType: undefined }
})
const { queryParams } = toRefs(data)

function liveDuration(row) {
  if (!row.entryTime) return '--'
  const minutes = Math.max(0, Math.floor((now.value - new Date(row.entryTime).getTime()) / 60000))
  if (minutes < 60) return minutes + ' 分钟'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  return h + 'h' + (m > 0 ? ' ' + m + 'm' : '')
}

function getList() {
  loading.value = true
  listVehicleRecord(queryParams.value).then(res => {
    recordList.value = res.rows
    total.value = res.total
    loading.value = false
  }).catch(() => { loading.value = false })
}
function handleQuery() { queryParams.value.pageNum = 1; getList() }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery() }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.recordId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}
function handleSettle(row) {
  settleRecord.value = row
  getHourlyRate().then(res => { if (res.unitPrice) settleUnitPrice.value = res.unitPrice })
  const entry = new Date(row.entryTime)
  const hours = Math.max(1, Math.ceil((Date.now() - entry.getTime()) / 3600000))
  settleDuration.value = hours
  settleFee.value = (hours * settleUnitPrice.value).toFixed(2)
  settlePaid.value = parseFloat(settleFee.value)
  settleOpen.value = true
}
function confirmSettle() {
  settling.value = true
  const formData = new FormData()
  formData.append('plateNumber', settleRecord.value.plateNumber)
  vehicleExit(formData).then(() => {
    settling.value = false
    settleOpen.value = false
    proxy.$modal.msgSuccess('收费完成：' + settlePaid.value + ' 元')
    getList()
  }).catch(() => { settling.value = false })
}
function handleDelete(row) {
  const recordIds = row.recordId || ids.value
  proxy.$modal.confirm('是否确认删除所选记录？').then(() => delVehicleRecord(recordIds)).then(() => {
    getList()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {})
}

getHourlyRate().then(res => { if (res.unitPrice) { defaultRate.value = res.unitPrice; settleUnitPrice.value = res.unitPrice } })
getList()

// 每秒刷新实时计时
timer = setInterval(() => { now.value = Date.now() }, 10000)
</script>

<style scoped>
.plate-number {
  font-family: monospace;
  font-weight: 600;
  background: #ecf5ff;
  padding: 2px 8px;
  border-radius: 4px;
  color: #409eff;
  letter-spacing: 1px;
}
.settle-content { text-align: center }
.plate-display { margin-bottom: 8px }
.plate-number.large {
  display: inline-block; padding: 8px 28px;
  background: #1a56db; color: #fff;
  font-size: 26px; font-weight: bold;
  letter-spacing: 4px; border-radius: 6px;
}
</style>
