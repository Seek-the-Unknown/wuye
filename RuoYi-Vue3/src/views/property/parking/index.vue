<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="车位编号" prop="parkingCode">
        <el-input v-model="queryParams.parkingCode" placeholder="请输入车位编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="车牌号" prop="plateNumber">
        <el-input v-model="queryParams.plateNumber" placeholder="请输入车牌号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="车位类型" prop="parkingType">
        <el-select v-model="queryParams.parkingType" placeholder="请选择" clearable style="width:110px">
          <el-option label="地上" value="0" />
          <el-option label="地下" value="1" />
          <el-option label="立体" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="绑定状态" prop="bindStatus">
        <el-select v-model="queryParams.bindStatus" placeholder="请选择" clearable style="width:110px">
          <el-option label="空闲" value="0" />
          <el-option label="已绑定" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:parking:add']">新增车位</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:parking:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:parking:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="parkingList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="车位编号" align="center" prop="parkingCode" width="110">
        <template #default="scope">
          <el-tag type="info" effect="plain">{{ scope.row.parkingCode }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="所属小区" align="center" prop="communityName" min-width="100" show-overflow-tooltip />
      <el-table-column label="车位类型" align="center" prop="parkingType" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.parkingType === '0' ? '' : scope.row.parkingType === '1' ? 'success' : 'warning'">
            {{ scope.row.parkingType === '0' ? '地上' : scope.row.parkingType === '1' ? '地下' : '立体' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="绑定业主" align="center" prop="ownerName" width="100">
        <template #default="scope">
          <span v-if="scope.row.ownerName">{{ scope.row.ownerName }}</span>
          <el-tag v-else type="info" size="small">-</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="车牌号" align="center" prop="plateNumber" width="120">
        <template #default="scope">
          <span v-if="scope.row.plateNumber" class="plate-number">{{ scope.row.plateNumber }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="绑定状态" align="center" prop="bindStatus" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.bindStatus === '0' ? 'success' : 'warning'" effect="dark">
            {{ scope.row.bindStatus === '0' ? '空闲' : '已绑定' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:parking:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:parking:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="520px" append-to-body draggable>
      <el-form ref="parkingRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属小区" prop="communityId">
              <el-select v-model="form.communityId" placeholder="请选择小区" filterable style="width:100%">
                <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车位编号" prop="parkingCode">
              <el-input v-model="form.parkingCode" placeholder="如：A-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车位类型" prop="parkingType">
              <el-select v-model="form.parkingType" style="width:100%">
                <el-option label="地上" value="0" />
                <el-option label="地下" value="1" />
                <el-option label="立体" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绑定业主ID" prop="ownerId">
              <el-input-number v-model="form.ownerId" :min="0" placeholder="选填" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车牌号" prop="plateNumber">
              <el-input v-model="form.plateNumber" placeholder="如：京A12345" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="绑定状态" prop="bindStatus">
              <el-select v-model="form.bindStatus" style="width:100%">
                <el-option label="空闲" value="0" />
                <el-option label="已绑定" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
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
  </div>
</template>

<script setup name="Parking">
import { listParking, getParking, addParking, updateParking, delParking } from '@/api/property/parking';
import { listAllCommunity } from '@/api/property/community';

const { proxy } = getCurrentInstance();
const parkingList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const communityOptions = ref([]);

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, parkingCode: undefined, plateNumber: undefined, parkingType: undefined, bindStatus: undefined },
  rules: {
    communityId: [{ required: true, message: '请选择小区', trigger: 'change' }],
    parkingCode: [{ required: true, message: '车位编号不能为空', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listParking(queryParams.value).then(res => {
    parkingList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
}
function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { parkingId: undefined, communityId: undefined, parkingCode: undefined, parkingType: '1', ownerId: null, plateNumber: '', bindStatus: '0', status: '0', remark: undefined };
  proxy.resetForm('parkingRef');
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.parkingId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); open.value = true; title.value = '新增车位'; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const parkingId = row.parkingId || ids.value;
  getParking(parkingId).then(res => { form.value = res.data; open.value = true; title.value = '修改车位信息'; });
}
function submitForm() {
  proxy.$refs['parkingRef'].validate(valid => {
    if (valid) {
      if (form.value.parkingId != undefined) {
        updateParking(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList(); });
      } else {
        addParking(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const parkingIds = row.parkingId || ids.value;
  proxy.$modal.confirm('是否确认删除选中的停车位？').then(() => delParking(parkingIds)).then(() => { getList(); proxy.$modal.msgSuccess('删除成功'); }).catch(() => {});
}

getList();
</script>

<style scoped>
.plate-number {
  font-family: monospace;
  font-weight: 600;
  background: #ecf5ff;
  padding: 2px 8px;
  border-radius: 4px;
  color: #409eff;
  letter-spacing: 1px;
}
</style>
