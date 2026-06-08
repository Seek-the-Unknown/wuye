<template>
  <div class="app-container vehicle-exit-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-camera" /> 车辆出场 - 拍照识别</span>
          </div>

          <el-upload
            class="upload-area"
            drag
            action=""
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleImageChange"
            accept="image/*"
          >
            <img v-if="previewUrl" :src="previewUrl" class="preview-img" />
            <template v-else>
              <i class="el-icon-upload" style="font-size: 67px; color: #C0C4CC;" />
              <div class="el-upload__text">将车辆照片拖到此处，或<em>点击拍照/上传</em></div>
            </template>
          </el-upload>

          <el-divider>或按车牌查找</el-divider>
          <el-input v-model="searchPlate" placeholder="输入车牌号查找入场记录" clearable size="large">
            <el-button slot="append" icon="el-icon-search" @click="handleSearchPlate" :loading="searching">查找</el-button>
          </el-input>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" v-loading="recognizing || searching">
          <div slot="header">
            <span><i class="el-icon-s-order" /> 计费结算</span>
          </div>

          <div v-if="!hasRecord" class="empty-state">
            <i class="el-icon-receiving" style="font-size: 48px; color: #DCDFE6;" />
            <p>请拍照或查找车辆入场记录</p>
          </div>

          <div v-else class="fee-detail">
            <div class="plate-display">
              <span class="plate-number">{{ record.plateNumber }}</span>
            </div>

            <el-descriptions :column="1" border size="medium" style="margin: 16px 0;">
              <el-descriptions-item label="入场时间">{{ formatTime(record.entryTime) }}</el-descriptions-item>
              <el-descriptions-item label="出场时间">{{ nowTime }}</el-descriptions-item>
              <el-descriptions-item label="停车时长">
                <span style="font-weight: bold; color: #1a56db;">{{ parkingDuration }} 小时</span>
              </el-descriptions-item>
              <el-descriptions-item label="计费单价">{{ unitPrice }} 元/小时</el-descriptions-item>
              <el-descriptions-item label="应付金额">
                <span style="font-size: 18px; font-weight: bold; color: #F56C6C;">{{ payableAmount }} 元</span>
              </el-descriptions-item>
            </el-descriptions>

            <el-alert
              v-if="exited"
              title="出场成功"
              :description="'停车' + parkingDuration + '小时，实收' + actualPaid + '元'"
              type="success" show-icon :closable="false"
              style="margin-bottom: 12px;"
            />

            <el-form :inline="true" v-if="!exited" style="margin-top: 8px;">
              <el-form-item label="实收金额">
                <el-input-number v-model="actualPaid" :min="0" :precision="2" :step="1" style="width: 130px;" />
              </el-form-item>
              <el-form-item label="减免">
                <el-input-number v-model="discount" :min="0" :max="payableAmount" :precision="2" style="width: 100px;" />
              </el-form-item>
            </el-form>

            <el-button v-if="!exited" type="success" size="large" @click="confirmExit" :loading="submitting" style="width: 100%;">
              确认离场并收费
            </el-button>
            <el-button v-else type="info" size="large" @click="resetAll" style="width: 100%;">
              下一辆车
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { vehicleExit, searchActiveRecord, getHourlyRate } from "@/api/property/vehicleRecord"

export default {
  name: "VehicleExit",
  data() {
    return {
      recognizing: false,
      searching: false,
      submitting: false,
      hasRecord: false,
      exited: false,
      searchPlate: "",
      previewUrl: "",
      selectedFile: null,
      unitPrice: 5,
      discount: 0,
      actualPaid: 0,
      nowTime: "",
      record: {},
      parkingDuration: 0,
      payableAmount: 0
    }
  },
  created() {
    this.fetchHourlyRate()
    this.updateNowTime()
    this.timer = setInterval(() => { this.updateNowTime() }, 30000)
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    fetchHourlyRate() {
      getHourlyRate().then(response => {
        if (response.unitPrice) this.unitPrice = response.unitPrice
      })
    },
    updateNowTime() {
      this.nowTime = new Date().toLocaleString("zh-CN", { hour12: false })
    },
    handleImageChange(file) {
      this.selectedFile = file.raw
      this.searchPlate = ""
      const reader = new FileReader()
      reader.onload = (e) => { this.previewUrl = e.target.result }
      reader.readAsDataURL(file.raw)
      this.submitExit()
    },
    handleSearchPlate() {
      if (!this.searchPlate) {
        this.$message.warning("请输入车牌号")
        return
      }
      this.searching = true
      searchActiveRecord(this.searchPlate).then(response => {
        this.searching = false
        this.record = response.data
        this.hasRecord = true
        this.calcFee()
      }).catch(() => {
        this.searching = false
      })
    },
    submitExit() {
      const formData = new FormData()
      if (this.selectedFile) {
        formData.append("file", this.selectedFile)
      }
      this.recognizing = true
      vehicleExit(formData).then(response => {
        this.recognizing = false
        this.record = response.data
        this.hasRecord = true
        this.exited = true
        this.calcFee()
        this.$modal.msgSuccess("车辆【" + response.data.plateNumber + "】出场成功")
      }).catch(() => {
        this.recognizing = false
      })
    },
    calcFee() {
      const entry = new Date(this.record.entryTime)
      const now = new Date()
      const minutes = Math.ceil((now - entry) / 60000)
      const hours = Math.max(1, Math.ceil(minutes / 60))
      this.parkingDuration = hours
      this.payableAmount = (hours * this.unitPrice).toFixed(2)
      this.actualPaid = parseFloat(this.payableAmount)
      this.discount = 0
      this.updateNowTime()
    },
    confirmExit() {
      this.submitting = true
      this.exited = true
      setTimeout(() => {
        this.submitting = false
        this.$modal.msgSuccess("收费完成：" + this.actualPaid + " 元")
      }, 500)
    },
    resetAll() {
      this.hasRecord = false
      this.exited = false
      this.record = {}
      this.selectedFile = null
      this.previewUrl = ""
      this.searchPlate = ""
      this.discount = 0
      this.actualPaid = 0
      this.parkingDuration = 0
      this.payableAmount = 0
    },
    formatTime(dateStr) {
      if (!dateStr) return "--"
      return new Date(dateStr).toLocaleString("zh-CN", { hour12: false })
    }
  },
  watch: {
    discount(val) {
      this.actualPaid = Math.max(0, this.payableAmount - val)
    }
  }
}
</script>

<style scoped>
.upload-area { text-align: center; margin-bottom: 16px; }
.preview-img { max-width: 100%; max-height: 240px; border-radius: 4px; }
.empty-state { text-align: center; padding: 60px 0; color: #909399; }
.plate-display { text-align: center; }
.plate-number {
  display: inline-block; padding: 8px 28px;
  background: #1a56db; color: #fff; font-size: 30px; font-weight: bold;
  letter-spacing: 4px; border-radius: 6px;
  box-shadow: 0 2px 12px rgba(26,86,219,0.35);
}
.fee-detail { padding: 0 4px; }
</style>
