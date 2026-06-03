<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属小区" prop="communityId">
        <el-select v-model="queryParams.communityId" placeholder="请选择小区" clearable filterable style="width:160px">
          <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
        </el-select>
      </el-form-item>
      <el-form-item label="楼宇名称" prop="buildingName">
        <el-input v-model="queryParams.buildingName" placeholder="请输入楼宇名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="楼宇编码" prop="buildingCode">
        <el-input v-model="queryParams.buildingCode" placeholder="请输入楼宇编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:building:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:building:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:building:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="buildingList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="楼宇ID" align="center" prop="buildingId" width="80" />
      <el-table-column label="楼宇名称" align="center" prop="buildingName" min-width="110">
        <template #default="scope">
          <el-tag type="primary" effect="plain">{{ scope.row.buildingName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="楼宇编码" align="center" prop="buildingCode" width="110" />
      <el-table-column label="所属小区" align="center" prop="communityName" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.communityName || scope.row.communityId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总楼层数" align="center" prop="floors" width="100">
        <template #default="scope">
          <span>{{ scope.row.floors }} 层</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" effect="dark">{{ scope.row.status === '0' ? '正常' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:building:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:building:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="520px" append-to-body draggable>
      <el-form ref="buildingRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="所属小区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择小区" filterable style="width:100%">
            <el-option v-for="item in communityOptions" :key="item.communityId" :label="item.communityName" :value="item.communityId" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼宇名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼宇名称，如：1号楼" />
        </el-form-item>
        <el-form-item label="楼宇编码" prop="buildingCode">
          <el-input v-model="form.buildingCode" placeholder="请输入楼宇编码，如：B001" />
        </el-form-item>
        <el-form-item label="总楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" :max="200" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Building">
import { listBuilding, getBuilding, delBuilding, addBuilding, updateBuilding } from "@/api/property/building";
import { listAllCommunity } from "@/api/property/community";

const { proxy } = getCurrentInstance();
const buildingList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const communityOptions = ref([]);

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, buildingName: undefined, buildingCode: undefined, communityId: undefined },
  rules: {
    buildingName: [{ required: true, message: "楼宇名称不能为空", trigger: "blur" }],
    buildingCode: [{ required: true, message: "楼宇编码不能为空", trigger: "blur" }],
    communityId: [{ required: true, message: "请选择所属小区", trigger: "change" }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listBuilding(queryParams.value).then(res => {
    buildingList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllCommunity().then(res => { communityOptions.value = res.data || []; });
}

function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { buildingId: undefined, communityId: undefined, buildingName: undefined, buildingCode: undefined, floors: 1, status: "0", remark: undefined };
  proxy.resetForm("buildingRef");
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.buildingId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); open.value = true; title.value = "添加楼宇"; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const buildingId = row.buildingId || ids.value;
  getBuilding(buildingId).then(res => {
    form.value = res.data;
    open.value = true;
    title.value = "修改楼宇";
  });
}
function submitForm() {
  proxy.$refs["buildingRef"].validate(valid => {
    if (valid) {
      if (form.value.buildingId != undefined) {
        updateBuilding(form.value).then(res => { proxy.$modal.msgSuccess("修改成功"); open.value = false; getList(); });
      } else {
        addBuilding(form.value).then(res => { proxy.$modal.msgSuccess("新增成功"); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const buildingIds = row.buildingId || ids.value;
  proxy.$modal.confirm('确认删除？').then(() => delBuilding(buildingIds)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功"); }).catch(() => {});
}

loadOptions();
getList();
</script>
