<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input v-model="queryParams.noticeTitle" placeholder="请输入公告标题" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="公告类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" placeholder="请选择类型" clearable style="width:110px">
          <el-option label="通知" value="0" />
          <el-option label="公告" value="1" />
          <el-option label="紧急" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width:110px">
          <el-option label="草稿" value="0" />
          <el-option label="已发布" value="1" />
          <el-option label="已下线" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:notice:add']">发布公告</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:notice:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:notice:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="公告类型" align="center" prop="noticeType" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.noticeType === '2' ? 'danger' : scope.row.noticeType === '1' ? 'primary' : 'info'" effect="dark">
            {{ scope.row.noticeType === '0' ? '通知' : scope.row.noticeType === '1' ? '公告' : '紧急' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="公告标题" align="center" prop="noticeTitle" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <el-link type="primary" @click="viewDetail(scope.row)">{{ scope.row.noticeTitle }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="适用小区" align="center" prop="communityName" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.communityName" type="success" effect="plain">{{ scope.row.communityName }}</el-tag>
          <el-tag v-else type="warning" effect="plain">全局</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发布时间" align="center" prop="publishTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.publishTime) }}</span></template>
      </el-table-column>
      <el-table-column label="发布人" align="center" prop="createBy" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'info' : scope.row.status === '1' ? 'success' : 'danger'" effect="light">
            {{ scope.row.status === '0' ? '草稿' : scope.row.status === '1' ? '已发布' : '已下线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="success" icon="Check" @click="publish(scope.row)" v-if="scope.row.status === '0'" v-hasPermi="['property:notice:edit']">发布</el-button>
          <el-button link type="warning" icon="Remove" @click="offline(scope.row)" v-if="scope.row.status === '1'" v-hasPermi="['property:notice:edit']">下线</el-button>
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:notice:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:notice:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="680px" append-to-body draggable>
      <el-form ref="noticeRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" style="width:100%">
                <el-option label="通知" value="0" />
                <el-option label="公告" value="1" />
                <el-option label="紧急" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="草稿" value="0" />
                <el-option label="已发布" value="1" />
                <el-option label="已下线" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用小区" prop="communityId">
              <el-select v-model="form.communityId" placeholder="不选则为全局公告" filterable clearable style="width:100%">
                <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择发布时间" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="公告内容" prop="noticeContent">
              <el-input v-model="form.noticeContent" type="textarea" :rows="6" placeholder="请输入公告内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="公告详情" v-model="detailOpen" width="600px" append-to-body>
      <div class="notice-detail" v-if="currentNotice">
        <div class="notice-header">
          <el-tag :type="currentNotice.noticeType === '2' ? 'danger' : currentNotice.noticeType === '1' ? 'primary' : 'info'" class="mr8">
            {{ currentNotice.noticeType === '0' ? '通知' : currentNotice.noticeType === '1' ? '公告' : '紧急' }}
          </el-tag>
          <h3>{{ currentNotice.noticeTitle }}</h3>
        </div>
        <el-divider />
        <div class="notice-meta">
          <span><el-icon><User /></el-icon> {{ currentNotice.createBy }}</span>
          <span><el-icon><Clock /></el-icon> {{ parseTime(currentNotice.publishTime) }}</span>
          <span v-if="currentNotice.communityName">适用：{{ currentNotice.communityName }}</span>
          <span v-else>适用：全局</span>
        </div>
        <div class="notice-content">{{ currentNotice.noticeContent }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="Notice">
import { listNotice, getNotice, addNotice, updateNotice, delNotice } from '@/api/property/notice';
import { listAllCommunity } from '@/api/property/community';

const { proxy } = getCurrentInstance();
const noticeList = ref([]);
const open = ref(false);
const detailOpen = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const communityOptions = ref([]);
const currentNotice = ref(null);

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, noticeTitle: undefined, noticeType: undefined, status: undefined },
  rules: {
    noticeTitle: [{ required: true, message: '公告标题不能为空', trigger: 'blur' }],
    noticeContent: [{ required: true, message: '公告内容不能为空', trigger: 'blur' }],
    noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listNotice(queryParams.value).then(res => {
    noticeList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
}
function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { noticeId: undefined, communityId: null, noticeTitle: undefined, noticeType: '0', noticeContent: undefined, status: '0', publishTime: undefined, remark: undefined };
  proxy.resetForm('noticeRef');
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.noticeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); open.value = true; title.value = '发布公告'; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const noticeId = row.noticeId || ids.value;
  getNotice(noticeId).then(res => { form.value = res.data; open.value = true; title.value = '修改公告'; });
}
function submitForm() {
  proxy.$refs['noticeRef'].validate(valid => {
    if (valid) {
      if (form.value.noticeId != undefined) {
        updateNotice(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList(); });
      } else {
        addNotice(form.value).then(() => { proxy.$modal.msgSuccess('发布成功'); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const noticeIds = row.noticeId || ids.value;
  proxy.$modal.confirm('确认删除该公告？').then(() => delNotice(noticeIds)).then(() => { getList(); proxy.$modal.msgSuccess('删除成功'); }).catch(() => {});
}
function publish(row) {
  proxy.$modal.confirm('确认发布该公告？').then(() => {
    updateNotice({ noticeId: row.noticeId, status: '1' }).then(() => { proxy.$modal.msgSuccess('已发布'); getList(); });
  }).catch(() => {});
}
function offline(row) {
  proxy.$modal.confirm('确认下线该公告？').then(() => {
    updateNotice({ noticeId: row.noticeId, status: '2' }).then(() => { proxy.$modal.msgSuccess('已下线'); getList(); });
  }).catch(() => {});
}
function viewDetail(row) {
  currentNotice.value = row;
  detailOpen.value = true;
}

getList();
</script>

<style scoped>
.notice-detail { padding: 8px 0; }
.notice-header { display: flex; align-items: center; gap: 12px; }
.notice-header h3 { margin: 0; font-size: 18px; color: #303133; }
.notice-meta { display: flex; gap: 24px; color: #909399; font-size: 13px; margin-bottom: 16px; align-items: center; }
.notice-meta span { display: flex; align-items: center; gap: 4px; }
.notice-content { line-height: 1.8; color: #606266; white-space: pre-wrap; background: #f5f7fa; padding: 16px; border-radius: 8px; min-height: 100px; }
</style>
