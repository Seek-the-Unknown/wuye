<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px" class="search-form">
      <el-form-item label="类型名称" prop="typeName">
        <el-input v-model="queryParams.typeName" placeholder="请输入费用类型名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类型编码" prop="typeCode">
        <el-input v-model="queryParams.typeCode" placeholder="请输入类型编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width:120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:feeType:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:feeType:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:feeType:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="feeTypeList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="feeTypeId" width="80" />
      <el-table-column label="费用类型名称" align="center" prop="typeName" min-width="130">
        <template #default="scope">
          <el-tag type="primary" effect="plain">{{ scope.row.typeName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="类型编码" align="center" prop="typeCode" width="120" />
      <el-table-column label="单价" align="center" prop="unitPrice" width="100">
        <template #default="scope">
          <span class="price-text">¥ {{ scope.row.unitPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计费单位" align="center" prop="unit" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" effect="dark">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:feeType:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:feeType:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body draggable>
      <el-form ref="feeTypeRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="类型名称" prop="typeName">
          <el-input v-model="form.typeName" placeholder="如：物业管理费" />
        </el-form-item>
        <el-form-item label="类型编码" prop="typeCode">
          <el-input v-model="form.typeCode" placeholder="如：WYLF" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="计费单位" prop="unit">
          <el-input v-model="form.unit" placeholder="如：元/月、元/㎡/月" />
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

<script setup name="FeeType">
import { listFeeType, getFeeType, addFeeType, updateFeeType, delFeeType } from '@/api/property/feeType';

const { proxy } = getCurrentInstance();
const feeTypeList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, typeName: undefined, typeCode: undefined, status: undefined },
  rules: {
    typeName: [{ required: true, message: '费用类型名称不能为空', trigger: 'blur' }],
    typeCode: [{ required: true, message: '类型编码不能为空', trigger: 'blur' }],
    unitPrice: [{ required: true, message: '单价不能为空', trigger: 'blur' }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listFeeType(queryParams.value).then(res => {
    feeTypeList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { feeTypeId: undefined, typeName: undefined, typeCode: undefined, unitPrice: 0, unit: '', status: '0', remark: undefined };
  proxy.resetForm('feeTypeRef');
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm('queryRef'); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.feeTypeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); open.value = true; title.value = '新增费用类型'; }
function handleUpdate(row) {
  reset();
  const feeTypeId = row.feeTypeId || ids.value;
  getFeeType(feeTypeId).then(res => { form.value = res.data; open.value = true; title.value = '修改费用类型'; });
}
function submitForm() {
  proxy.$refs['feeTypeRef'].validate(valid => {
    if (valid) {
      if (form.value.feeTypeId != undefined) {
        updateFeeType(form.value).then(() => { proxy.$modal.msgSuccess('修改成功'); open.value = false; getList(); });
      } else {
        addFeeType(form.value).then(() => { proxy.$modal.msgSuccess('新增成功'); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const feeTypeIds = row.feeTypeId || ids.value;
  proxy.$modal.confirm('是否确认删除选中的费用类型？').then(() => delFeeType(feeTypeIds)).then(() => { getList(); proxy.$modal.msgSuccess('删除成功'); }).catch(() => {});
}

getList();
</script>

<style scoped>
.price-text { color: #e6a23c; font-weight: 600; }
</style>
