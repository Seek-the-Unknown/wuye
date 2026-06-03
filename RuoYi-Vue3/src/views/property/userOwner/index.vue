<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="系统账号" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入系统用户名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="业主姓名" prop="ownerName">
        <el-input v-model="queryParams.ownerName" placeholder="请输入业主姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:userOwner:add']">新增绑定</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:userOwner:remove']">解绑</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="系统账号" align="center" prop="userName" />
      <el-table-column label="系统昵称" align="center" prop="nickName" />
      <el-table-column label="业主姓名" align="center" prop="ownerName">
        <template #default="scope">
          <el-tag type="success">{{ scope.row.ownerName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="业主电话" align="center" prop="phone" />
      <el-table-column label="绑定时间" align="center" prop="createTime" width="160">
        <template #default="scope"><span>{{ parseTime(scope.row.createTime) }}</span></template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:userOwner:remove']">解绑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择系统用户" prop="userId">
          <el-select v-model="form.userId" filterable placeholder="请选择系统用户账号" style="width: 100%">
            <el-option
              v-for="u in userOptions"
              :key="u.userId"
              :label="`${u.userName} (${u.nickName})`"
              :value="u.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择绑定房产" prop="ownerId">
          <el-select v-model="form.ownerId" filterable placeholder="请选择对应小区/楼栋/房号及业主" style="width: 100%">
            <el-option
              v-for="house in houseOptions"
              :key="house.ownerId"
              :label="`${house.community} - ${house.ownerName}`"
              :value="house.ownerId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="UserOwner">
import request from '@/utils/request';

const { proxy } = getCurrentInstance();
const list = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const title = ref('');

const userOptions = ref([]);
const houseOptions = ref([]);

function getUserOptions() {
  request({ url: '/system/user/list', method: 'get', params: { pageNum: 1, pageSize: 100 } }).then(res => {
    userOptions.value = res.rows || [];
  });
}

function getHouseOptions() {
  request({ url: '/property/owner/listAll', method: 'get' }).then(res => {
    houseOptions.value = (res.data || []).map(o => ({
      ownerId: o.ownerId,
      community: o.communityName || '',
      building: '',
      room: '',
      ownerName: o.ownerName
    }));
  });
}

getUserOptions();
getHouseOptions();

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, userName: undefined, ownerName: undefined },
  rules: {
    userId: [{ required: true, message: '请选择要绑定的系统用户', trigger: 'change' }],
    ownerId: [{ required: true, message: '请选择要绑定的房屋和业主信息', trigger: 'change' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  request({ url: '/property/userOwner/list', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() { form.value = { id: undefined, userId: undefined, ownerId: undefined }; proxy.resetForm('formRef'); }
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  multiple.value = !selection.length;
}
function handleAdd() { reset(); open.value = true; title.value = '新增绑定'; }
function submitForm() {
  proxy.$refs['formRef'].validate(valid => {
    if (valid) {
      request({ url: '/property/userOwner', method: 'post', data: form.value }).then(() => {
        proxy.$modal.msgSuccess('绑定成功');
        open.value = false;
        getList();
      });
    }
  });
}
function handleDelete(row) {
  const delIds = row.id || ids.value;
  proxy.$modal.confirm('确认解绑？').then(() => {
    return request({ url: '/property/userOwner/' + delIds, method: 'delete' });
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess('解绑成功');
  }).catch(() => {});
}

getList();
</script>
