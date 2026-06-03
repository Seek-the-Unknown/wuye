<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的停车位</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="车位编号" align="center" prop="parkingCode" />
        <el-table-column label="车位类型" align="center" prop="parkingType">
          <template #default="scope">
            <el-tag :type="scope.row.parkingType === '1' ? 'success' : 'primary'">
              {{ scope.row.parkingType === '0' ? '地上' : scope.row.parkingType === '1' ? '地下' : '立体' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定车牌" align="center" prop="plateNumber">
          <template #default="scope">
            <span v-if="scope.row.plateNumber" style="font-weight: bold; color: #409EFF; padding: 4px 8px; background: #ecf5ff; border: 1px solid #b3d8ff; border-radius: 4px;">{{ scope.row.plateNumber }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="绑定状态" align="center" prop="bindStatus">
          <template #default="scope">
            <el-tag :type="scope.row.bindStatus === '0' ? 'info' : 'success'">
              {{ scope.row.bindStatus === '0' ? '空闲' : '已绑定' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="MyParking">
import request from '@/utils/request';

const list = ref([]);
const loading = ref(true);
const total = ref(0);
const queryParams = ref({ pageNum: 1, pageSize: 10 });

function getList() {
  loading.value = true;
  request({ url: '/property/portal/myParking', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

getList();
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
