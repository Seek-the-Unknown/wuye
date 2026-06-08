<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header><span style="font-weight:bold">车辆入场登记</span></template>

          <el-form label-width="80px">
            <el-form-item label="车牌号">
              <el-input v-model="plateNumber" placeholder="请输入车牌号，如：京A12345" size="large" clearable @keyup.enter="handleEnter" />
            </el-form-item>
            <el-form-item label="车辆类型">
              <el-radio-group v-model="vehicleType">
                <el-radio value="0">临时车</el-radio>
                <el-radio value="1">月租车</el-radio>
                <el-radio value="2">业主车</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="handleEnter" :loading="submitting" style="width:100%">
                确认入场
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider content-position="center">或 拍照识别入场</el-divider>

          <el-upload
            class="upload-area"
            drag
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleUpload"
            accept="image/*"
          >
            <el-icon :size="40"><UploadFilled /></el-icon>
            <div class="el-upload__text">拖拽或<em>点击上传</em>车辆照片</div>
            <template #tip><div class="el-upload__tip">需先启动 Python 识别服务</div></template>
          </el-upload>
        </el-card>
      </el-col>

      <el-col :span="14">
        <el-card v-loading="submitting">
          <template #header><span style="font-weight:bold">最近入场记录</span></template>

          <div v-if="!lastRecord && records.length === 0" class="empty-state">
            <el-icon :size="48" color="#DCDFE6"><Picture /></el-icon>
            <p>暂无记录，请登记车辆入场</p>
          </div>

          <div v-if="lastRecord" class="success-banner">
            <el-result icon="success" title="入场成功" :sub-title="'车牌号：' + lastRecord.plateNumber" />
          </div>

          <el-table v-if="records.length > 0" :data="records" stripe border size="small" style="margin-top:12px">
            <el-table-column label="车牌号" prop="plateNumber" width="130">
              <template #default="scope">
                <span class="plate-tag">{{ scope.row.plateNumber }}</span>
              </template>
            </el-table-column>
            <el-table-column label="入场时间" prop="entryTime" />
            <el-table-column label="类型" prop="vehicleType" width="80">
              <template #default="scope">
                <el-tag v-if="scope.row.vehicleType === '0'" type="info" size="small">临时</el-tag>
                <el-tag v-else-if="scope.row.vehicleType === '1'" size="small">月租</el-tag>
                <el-tag v-else type="success" size="small">业主</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" prop="payStatus" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.payStatus === '1' ? 'success' : 'danger'" size="small">
                  {{ scope.row.payStatus === '1' ? '已离场' : '在场' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="VehicleEnter">
import { vehicleEnter, listVehicleRecord } from '@/api/property/vehicleRecord'

const { proxy } = getCurrentInstance()
const submitting = ref(false)
const plateNumber = ref('')
const vehicleType = ref('0')
const lastRecord = ref(null)
const records = ref([])

function refreshRecords() {
  listVehicleRecord({ pageNum: 1, pageSize: 10, payStatus: '0' }).then(res => {
    records.value = res.rows || []
  })
}

function handleEnter() {
  if (!plateNumber.value.trim()) { proxy.$modal.msgWarning('请输入车牌号'); return }
  submitting.value = true
  const formData = new FormData()
  formData.append('plateNumber', plateNumber.value.trim())
  formData.append('vehicleType', vehicleType.value)
  vehicleEnter(formData).then(res => {
    lastRecord.value = res.data
    plateNumber.value = ''
    proxy.$modal.msgSuccess('车辆【' + res.data.plateNumber + '】入场成功')
    refreshRecords()
  }).finally(() => { submitting.value = false })
}

function handleUpload(file) {
  submitting.value = true
  const formData = new FormData()
  formData.append('file', file.raw)
  formData.append('vehicleType', vehicleType.value)
  vehicleEnter(formData).then(res => {
    lastRecord.value = res.data
    proxy.$modal.msgSuccess('识别成功：' + res.data.plateNumber + ' 已入场')
    refreshRecords()
  }).finally(() => { submitting.value = false })
}

refreshRecords()
</script>

<style scoped>
.upload-area { text-align: center }
.empty-state { text-align: center; padding: 60px 0; color: #909399 }
.success-banner { padding: 20px 0 }
.plate-tag {
  font-family: monospace; font-weight: 600;
  background: #ecf5ff; padding: 2px 10px;
  border-radius: 4px; color: #409eff; letter-spacing: 1px;
}
</style>
