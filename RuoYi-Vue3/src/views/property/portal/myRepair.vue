<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>在线报修记录</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">我要报修</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="报修单号" align="center" prop="repairId" width="80" />
        <el-table-column label="报修标题" align="center" prop="repairTitle" />
        <el-table-column label="报修内容" align="center" prop="repairContent" show-overflow-tooltip />
        <el-table-column label="状态" align="center" prop="repairStatus" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.repairStatus === '0' ? 'danger' : scope.row.repairStatus === '1' ? 'warning' : 'success'">
              {{ scope.row.repairStatus === '0' ? '待处理' : scope.row.repairStatus === '1' ? '处理中' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="我要报修" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="报修标题" prop="repairTitle">
          <el-input v-model="form.repairTitle" placeholder="简述您遇到的问题" />
        </el-form-item>
        <el-form-item label="详细内容" prop="repairContent">
          <el-input v-model="form.repairContent" type="textarea" :rows="4" placeholder="请详细描述问题，如有必要可附上具体位置等信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MyRepair">
/**
 * 在线报修页面
 * 业主提交报修申请，查看报修进度。
 *
 * 注意：提交时后端会自动补充业主ID和小区ID，
 * 前端不需要传这两个字段。
 */
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const list = ref([]);
const open = ref(false);
const loading = ref(true);
const total = ref(0);

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10 },
  rules: {
    repairTitle: [{ required: true, message: '报修标题不能为空', trigger: 'blur' }],
    repairContent: [{ required: true, message: '报修内容不能为空', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

/** 查询报修列表 */
function getList() {
  loading.value = true;
  console.log('【MyRepair】查询报修列表...');
  request({ url: '/property/portal/myRepair', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() { form.value = { repairTitle: undefined, repairContent: undefined }; proxy.resetForm('formRef'); }

/** 打开报修弹窗 */
function handleAdd() { reset(); open.value = true; }

/** 提交报修表单 */
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      console.log('【MyRepair】提交报修：', form.value.repairTitle);
      request({ url: '/property/portal/repair', method: 'post', data: form.value }).then(() => {
        proxy.$modal.msgSuccess('报修提交成功！我们会尽快处理！');
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
