<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header><span style="font-weight:bold">车辆出场结算</span></template>

          <el-form label-width="80px">
            <el-form-item label="车牌号">
              <el-input v-model="plateNumber" placeholder="请输入车牌号查找入场记录" size="large" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="handleSearch" :loading="searching" style="width:100%">
                查找并结算
              </el-button>
            </el-form-item>
          </el-form>

          <el-divider content-position="center">或 拍照识别出场</el-divider>

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
        <el-card v-loading="searching || submitting">
          <template #header><span style="font-weight:bold">结算结果</span></template>

          <div v-if="!hasResult" class="empty-state">
            <el-icon :size="48" color="#DCDFE6"><Money /></el-icon>
            <p>请输入车牌号查找入场记录</p>
          </div>

          <div v-else>
            <div class="plate-display">
              <span class="plate-number large">{{ record.plateNumber }}</span>
            </div>

            <el-descriptions :column="2" border size="small" style="margin:16px 0">
              <el-descriptions-item label="入场时间">{{ record.entryTime }}</el-descriptions-item>
              <el-descriptions-item label="出场时间">{{ nowTime }}</el-descriptions-item>
              <el-descriptions-item label="停车时长">
                <span style="font-weight:bold;color:#1a56db">{{ parkingDuration }} 小时</span>
              </el-descriptions-item>
              <el-descriptions-item label="计费单价">{{ unitPrice }} 元/小时</el-descriptions-item>
              <el-descriptions-item label="应收金额">
                <span style="font-size:18px;font-weight:bold;color:#F56C6C">{{ payableAmount }} 元</span>
              </el-descriptions-item>
              <el-descriptions-item label="实收金额">
                <el-input-number v-model="actualPaid" :min="0" :precision="2" style="width:120px" size="small" />
              </el-descriptions-item>
            </el-descriptions>

            <el-button type="success" size="large" @click="handleConfirm" :loading="submitting" style="width:100%">
              确认收费 {{ actualPaid }} 元
            </el-button>

            <el-button size="small" @click="resetAll" style="width:100%;margin-top:8px">清除，下一辆车</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="VehicleExit">
import { vehicleExit, getHourlyRate } from '@/api/property/vehicleRecord'

const { proxy } = getCurrentInstance()
const searching = ref(false)
const submitting = ref(false)
const hasResult = ref(false)
const plateNumber = ref('')
const unitPrice = ref(5)
const actualPaid = ref(0)
const nowTime = ref('')
const record = ref({})
const parkingDuration = ref(0)
const payableAmount = ref(0)

getHourlyRate().then(res => { if (res.unitPrice) unitPrice.value = res.unitPrice })

function updateNow() { nowTime.value = new Date().toLocaleString('zh-CN', { hour12: false }) }
setInterval(() => updateNow(), 30000)
updateNow()

function handleSearch() {
  if (!plateNumber.value.trim()) { proxy.$modal.msgWarning('请输入车牌号'); return }
  searching.value = true
  const formData = new FormData()
  formData.append('plateNumber', plateNumber.value.trim())
  vehicleExit(formData).then(res => {
    processResult(res.data)
    proxy.$modal.msgSuccess('车辆【' + res.data.plateNumber + '】出场成功')
  }).finally(() => { searching.value = false })
}

function handleUpload(file) {
  searching.value = true
  const formData = new FormData()
  formData.append('file', file.raw)
  vehicleExit(formData).then(res => {
    processResult(res.data)
    proxy.$modal.msgSuccess('车辆【' + res.data.plateNumber + '】出场成功')
  }).finally(() => { searching.value = false })
}

function processResult(data) {
  record.value = data
  hasResult.value = true
  parkingDuration.value = data.parkingDuration || 1
  payableAmount.value = parseFloat(data.feeAmount || (parkingDuration.value * unitPrice.value))
  actualPaid.value = payableAmount.value
  updateNow()
}

function handleConfirm() {
  submitting.value = true
  setTimeout(() => {
    submitting.value = false
    proxy.$modal.msgSuccess('收费完成：' + actualPaid.value + ' 元')
    resetAll()
  }, 400)
}

function resetAll() {
  hasResult.value = false; record.value = {}
  plateNumber.value = ''; actualPaid.value = 0
  parkingDuration.value = 0; payableAmount.value = 0
}
</script>

<style scoped>
.upload-area { text-align: center }
.empty-state { text-align: center; padding: 60px 0; color: #909399 }
.plate-display { text-align: center; margin-bottom: 8px }
.plate-number.large {
  display: inline-block; padding: 8px 28px;
  background: #1a56db; color: #fff;
  font-size: 28px; font-weight: bold;
  letter-spacing: 4px; border-radius: 6px;
}
</style>
