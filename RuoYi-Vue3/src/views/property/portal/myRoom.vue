<template>
  <div class="app-container">
    <el-card shadow="never" class="box-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">
            <el-icon class="title-icon"><House /></el-icon> 我的房屋档案
          </span>
        </div>
      </template>

      <div v-loading="loading">
        <el-row v-if="rooms && rooms.length > 0" :gutter="20">
          <el-col v-for="room in rooms" :key="room.roomId" :xs="24" :sm="12" :md="8" :lg="8" class="room-col">
            <el-card shadow="hover" class="room-card">
              <template #header>
                <div class="room-header">
                  <div class="room-title">
                    <span class="room-name">{{ room.roomName }}</span>
                    <span class="room-code">#{{ room.roomCode }}</span>
                  </div>
                  <el-tag :type="getStatusType(room.status)" size="small" effect="dark">
                    {{ getStatusLabel(room.status) }}
                  </el-tag>
                </div>
              </template>
              
              <div class="room-body">
                <div class="room-item">
                  <span class="label"><el-icon><Calendar /></el-icon> 所在楼层</span>
                  <span class="value">{{ room.floorNum || 1 }} 层</span>
                </div>
                <div class="room-item">
                  <span class="label"><el-icon><Odometer /></el-icon> 建筑面积</span>
                  <span class="value highlight">{{ room.constructionArea }} ㎡</span>
                </div>
                <div class="room-item">
                  <span class="label"><el-icon><Crop /></el-icon> 使用面积</span>
                  <span class="value">{{ room.useArea || room.constructionArea }} ㎡</span>
                </div>
                <div class="room-item" v-if="room.remark">
                  <span class="label"><el-icon><Notebook /></el-icon> 备注说明</span>
                  <span class="value desc">{{ room.remark }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-empty v-else description="暂未查询到绑定的房屋信息，如有疑问请联系物业管理员" />
      </div>
    </el-card>
  </div>
</template>

<script setup name="MyRoom">
import { ref, getCurrentInstance } from 'vue';
import request from '@/utils/request';

const loading = ref(true);
const rooms = ref([]);

function getMyRooms() {
  loading.value = true;
  request({
    url: '/property/portal/myRoom',
    method: 'get'
  }).then(res => {
    rooms.value = res.rows || [];
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function getStatusLabel(status) {
  const map = {
    '0': '未售',
    '1': '已售未入住',
    '2': '已入住'
  };
  return map[status] || '未知状态';
}

function getStatusType(status) {
  const map = {
    '0': 'danger',
    '1': 'warning',
    '2': 'success'
  };
  return map[status] || 'info';
}

getMyRooms();
</script>

<style scoped>
.box-card {
  border-radius: 8px;
  background-color: #fff;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}
.title-icon {
  margin-right: 8px;
  color: #409eff;
}
.room-col {
  margin-bottom: 20px;
}
.room-card {
  border-radius: 6px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid #ebeef5;
}
.room-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.05);
  border-color: #c6e2ff;
}
.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.room-title {
  display: flex;
  flex-direction: column;
}
.room-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
.room-code {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
.room-body {
  padding: 5px 0;
}
.room-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 13px;
}
.room-item:last-child {
  margin-bottom: 0;
}
.label {
  color: #606266;
  display: flex;
  align-items: center;
}
.label .el-icon {
  margin-right: 6px;
}
.value {
  color: #303133;
  font-weight: 500;
}
.value.highlight {
  color: #409eff;
  font-weight: 600;
}
.value.desc {
  color: #909399;
  font-style: italic;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
