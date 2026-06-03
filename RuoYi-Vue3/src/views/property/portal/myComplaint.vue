<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>投诉建议记录</span>
          <el-button type="primary" icon="Plus" @click="handleAdd">新建投诉/建议</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="类型" align="center" prop="complaintType" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.complaintType === '0' ? 'danger' : scope.row.complaintType === '1' ? 'warning' : 'success'">
              {{ scope.row.complaintType === '0' ? '投诉' : scope.row.complaintType === '1' ? '建议' : '表扬' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标题" align="center" prop="complaintTitle" show-overflow-tooltip />
        <el-table-column label="状态" align="center" prop="handleStatus" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.handleStatus === '0' ? 'danger' : scope.row.handleStatus === '1' ? 'warning' : scope.row.handleStatus === '2' ? 'success' : 'info'" effect="dark">
              {{ scope.row.handleStatus === '0' ? '待处理' : scope.row.handleStatus === '1' ? '处理中' : scope.row.handleStatus === '2' ? '已回复' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" align="center" prop="createTime" width="160">
          <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="100">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <el-dialog title="新建投诉建议" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型" prop="complaintType">
          <el-radio-group v-model="form.complaintType">
            <el-radio label="0">投诉</el-radio>
            <el-radio label="1">建议</el-radio>
            <el-radio label="2">表扬</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="complaintTitle">
          <el-input v-model="form.complaintTitle" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="详细内容" prop="complaintContent">
          <el-input v-model="form.complaintContent" type="textarea" :rows="4" placeholder="请详细描述您的诉求或建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">提 交</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="详情" v-model="viewOpen" width="500px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="标题">{{ viewData.complaintTitle }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag size="small" :type="viewData.complaintType === '0' ? 'danger' : viewData.complaintType === '1' ? 'warning' : 'success'">
            {{ viewData.complaintType === '0' ? '投诉' : viewData.complaintType === '1' ? '建议' : '表扬' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="内容">{{ viewData.complaintContent }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag size="small" :type="viewData.handleStatus === '0' ? 'danger' : viewData.handleStatus === '1' ? 'warning' : viewData.handleStatus === '2' ? 'success' : 'info'">
            {{ viewData.handleStatus === '0' ? '待处理' : viewData.handleStatus === '1' ? '处理中' : viewData.handleStatus === '2' ? '已回复' : '已关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理回复" v-if="viewData.handleResult">{{ viewData.handleResult }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="viewData.handleTime">{{ parseTime(viewData.handleTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup name="MyComplaint">
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const list = ref([]);
const open = ref(false);
const viewOpen = ref(false);
const loading = ref(true);
const total = ref(0);
const viewData = ref({});

const data = reactive({
  form: { complaintType: '0' },
  queryParams: { pageNum: 1, pageSize: 10 },
  rules: {
    complaintTitle: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
    complaintContent: [{ required: true, message: '内容不能为空', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  request({ url: '/property/portal/myComplaint', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() { form.value = { complaintType: '0', complaintTitle: undefined, complaintContent: undefined }; proxy.resetForm('formRef'); }
function handleAdd() { reset(); open.value = true; }
function handleView(row) {
  viewData.value = row;
  viewOpen.value = true;
}
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      request({ url: '/property/portal/complaint', method: 'post', data: form.value }).then(() => {
        proxy.$modal.msgSuccess('提交成功');
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
