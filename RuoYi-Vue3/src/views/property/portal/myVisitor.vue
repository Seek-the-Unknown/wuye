<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的访客登记</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">登记访客</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="访客姓名" align="center" prop="visitorName" />
        <el-table-column label="联系电话" align="center" prop="visitorPhone" />
        <el-table-column label="身份证号" align="center" prop="idCard" />
        <el-table-column label="来访时间" align="center" prop="visitTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.visitTime, '{y}-{m}-{d} {h}:{i}') }}</span></template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="visitStatus">
          <template #default="scope">
            <el-tag :type="scope.row.visitStatus === '0' ? 'warning' : scope.row.visitStatus === '1' ? 'success' : scope.row.visitStatus === '3' ? 'danger' : 'info'">
              {{ scope.row.visitStatus === '0' ? '待审核' : scope.row.visitStatus === '1' ? '已放行' : scope.row.visitStatus === '3' ? '已拒绝' : '已离开' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="登记访客" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="访客姓名" prop="visitorName">
          <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="visitorPhone">
          <el-input v-model="form.visitorPhone" placeholder="请输入访客联系电话" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="可选填身份证号" />
        </el-form-item>
        <el-form-item label="来访时间" prop="visitTime">
          <el-date-picker v-model="form.visitTime" type="datetime" placeholder="选择来访时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="来访事由" prop="visitReason">
          <el-input v-model="form.visitReason" type="textarea" :rows="2" placeholder="请输入来访事由" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MyVisitor">
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const list = ref([]);
const open = ref(false);
const loading = ref(true);
const total = ref(0);

const data = reactive({
  form: { visitStatus: '0' },
  queryParams: { pageNum: 1, pageSize: 10 },
  rules: {
    visitorName: [{ required: true, message: '访客姓名不能为空', trigger: 'blur' }],
    visitorPhone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
    visitTime: [{ required: true, message: '来访时间不能为空', trigger: 'change' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  request({ url: '/property/portal/myVisitor', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() { form.value = { visitorName: undefined, visitorPhone: undefined, idCard: undefined, visitTime: undefined, visitReason: undefined, visitStatus: '0' }; proxy.resetForm('formRef'); }
function handleAdd() { reset(); open.value = true; }
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      request({ url: '/property/portal/visitor', method: 'post', data: form.value }).then(() => {
        proxy.$modal.msgSuccess('登记成功');
        open.value = false;
        getList();
      });
    }
  });
}

getList();
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
