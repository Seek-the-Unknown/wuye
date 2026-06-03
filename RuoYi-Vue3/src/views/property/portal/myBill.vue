<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的账单</span>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="账单月份" align="center" prop="feeMonth" width="100" />
        <el-table-column label="费用类型" align="center" prop="typeName">
          <template #default="scope"><el-tag>{{ scope.row.typeName }}</el-tag></template>
        </el-table-column>
        <el-table-column label="房屋号" align="center" prop="roomName" />
        <el-table-column label="应缴金额(元)" align="center" prop="feeAmount" />
        <el-table-column label="缴费状态" align="center" prop="payStatus" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.payStatus === '0' ? 'danger' : 'success'">
              {{ scope.row.payStatus === '0' ? '未缴' : '已缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template #default="scope">
            <el-button v-if="scope.row.payStatus === '0'" type="primary" size="small" @click="handlePay(scope.row)">在线缴费</el-button>
            <span v-else style="color: #67C23A; font-size: 13px;"><el-icon><Check /></el-icon> 缴费成功</span>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="MyBill">
/**
 * 我的账单页面
 * 业主查看自己名下的物业费账单，支持在线缴费。
 * 数据来源：/property/portal/myBill
 *
 * TODO: 目前"在线缴费"只是前端确认弹窗，实际支付接口还没有对接
 */
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const list = ref([]);
const loading = ref(true);
const total = ref(0);

const data = reactive({
  queryParams: { pageNum: 1, pageSize: 10 }
});
const { queryParams } = toRefs(data);

/** 加载账单数据 */
function getList() {
  loading.value = true;
  console.log('【MyBill】正在获取账单列表...');
  request({ url: '/property/portal/myBill', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
    console.log('【MyBill】获取成功，共' + total.value + '条记录');
  }).catch(() => { loading.value = false; });
}

/**
 * 缴费操作
 * 这里只是模拟缴费，实际应该调后端支付接口
 */
function handlePay(row) {
  console.log('【MyBill】用户点击缴费：', row.recordId, '金额：' + row.feeAmount);
  proxy.$modal.confirm('确认支付该笔账单？(金额：' + row.feeAmount + '元)').then(() => {
    // TODO: 这里应该调后端支付接口，目前只做提示
    proxy.$modal.msgSuccess('支付成功！');
    getList();
  }).catch(() => {});
}

getList();
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
