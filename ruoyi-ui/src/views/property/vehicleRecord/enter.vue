<template>
  <div class="app-container vehicle-enter-container">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header">
            <span><i class="el-icon-camera" /> 车辆入场 - 拍照识别</span>
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

          <el-divider>或手动输入</el-divider>
          <el-input v-model="manualPlate" placeholder="输入车牌号，如：京A12345" clearable size="large">
            <el-button slot="append" icon="el-icon-check" @click="handleManualEnter" :loading="recognizing">入场</el-button>
          </el-input>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover" v-loading="recognizing">
          <div slot="header">
            <span><i class="el-icon-picture-outline" /> 识别结果</span>
          </div>

          <div v-if="!lastRecord" class="empty-state">
            <i class="el-icon-camera" style="font-size: 48px; color: #DCDFE6;" />
            <p>请拍照或手动输入车牌号</p>
          </div>

          <div v-else class="result-area">
            <div class="plate-display">
              <span class="plate-label">车牌号</span>
              <span class="plate-number">{{ lastRecord.plateNumber }}</span>
            </div>

            <el-descriptions :column="1" border size="medium" style="margin: 16px 0;">
              <el-descriptions-item label="入场时间">{{ lastRecord.entryTime }}</el-descriptions-item>
              <el-descriptions-item label="车辆类型">
                <el-tag v-if="lastRecord.vehicleType === '0'" type="info" size="small">临时车</el-tag>
                <el-tag v-else-if="lastRecord.vehicleType === '1'" size="small">月租车</el-tag>
                <el-tag v-else-if="lastRecord.vehicleType === '2'" type="success" size="small">业主车</el-tag>
              </el-descriptions-item>
            </el-descriptions>

            <el-alert title="入场成功" :description="'车辆【' + lastRecord.plateNumber + '】已放行入场'" type="success" show-icon :closable="false" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { vehicleEnter } from "@/api/property/vehicleRecord"

export default {
  name: "VehicleEnter",
  data() {
    return {
      recognizing: false,
      manualPlate: "",
      selectedFile: null,
      previewUrl: "",
      lastRecord: null
    }
  },
  methods: {
    handleImageChange(file) {
      this.selectedFile = file.raw
      this.manualPlate = ""
      // 生成预览
      const reader = new FileReader()
      reader.onload = (e) => { this.previewUrl = e.target.result }
      reader.readAsDataURL(file.raw)
      this.submitEnter()
    },
    handleManualEnter() {
      if (!this.manualPlate) {
        this.$message.warning("请输入车牌号")
        return
      }
      this.selectedFile = null
      this.submitEnter()
    },
    submitEnter() {
      const formData = new FormData()
      if (this.selectedFile) {
        formData.append("file", this.selectedFile)
      } else if (this.manualPlate) {
        // 创建一个空的 Blob 模拟文件，避免后端 NPE
        const blob = new Blob([""], { type: "text/plain" })
        formData.append("file", blob, "manual.txt")
        formData.append("plateNumber", this.manualPlate)
      } else {
        return
      }

      this.recognizing = true

      vehicleEnter(formData).then(response => {
        this.recognizing = false
        this.lastRecord = {
          plateNumber: response.data.plateNumber,
          entryTime: response.data.entryTime,
          vehicleType: response.data.vehicleType || "0"
        }
        this.$modal.msgSuccess("车辆【" + response.data.plateNumber + "】入场成功")
        // 3秒后自动清除，准备下一辆车
        setTimeout(() => { this.reset() }, 5000)
      }).catch(() => {
        this.recognizing = false
      })
    },
    reset() {
      this.lastRecord = null
      this.selectedFile = null
      this.manualPlate = ""
      this.previewUrl = ""
    }
  }
}
</script>

<style scoped>
.upload-area { text-align: center; margin-bottom: 16px; }
.preview-img { max-width: 100%; max-height: 240px; border-radius: 4px; }
.empty-state { text-align: center; padding: 60px 0; color: #909399; }
.plate-display { text-align: center; }
.plate-label { display: block; color: #909399; font-size: 14px; margin-bottom: 8px; }
.plate-number {
  display: inline-block; padding: 8px 28px;
  background: #1a56db; color: #fff; font-size: 30px; font-weight: bold;
  letter-spacing: 4px; border-radius: 6px;
  box-shadow: 0 2px 12px rgba(26,86,219,0.35);
}
</style>
