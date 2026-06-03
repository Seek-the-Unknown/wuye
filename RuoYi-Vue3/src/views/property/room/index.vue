<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属楼宇" prop="buildingId">
        <el-select v-model="queryParams.buildingId" placeholder="请选择楼宇" clearable filterable style="width:150px">
          <el-option v-for="item in buildingOptions" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
        </el-select>
      </el-form-item>
      <el-form-item label="房屋名称" prop="roomName">
        <el-input v-model="queryParams.roomName" placeholder="请输入房屋名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="房屋编号" prop="roomCode">
        <el-input v-model="queryParams.roomCode" placeholder="请输入房屋编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="房屋状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width:120px">
          <el-option label="未售" value="0" />
          <el-option label="已售未入住" value="1" />
          <el-option label="已入住" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['property:room:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate" v-hasPermi="['property:room:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['property:room:remove']">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roomList" @selection-change="handleSelectionChange" stripe border>
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="房屋ID" align="center" prop="roomId" width="80" />
      <el-table-column label="房屋名称" align="center" prop="roomName" min-width="110">
        <template #default="scope">
          <el-tag type="primary" effect="plain">{{ scope.row.roomName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="房屋编号" align="center" prop="roomCode" width="110" />
      <el-table-column label="所在楼层" align="center" prop="floorNum" width="90">
        <template #default="scope"><span>{{ scope.row.floorNum }}F</span></template>
      </el-table-column>
      <el-table-column label="建筑面积" align="center" prop="constructionArea" width="110">
        <template #default="scope"><span>{{ scope.row.constructionArea }} ㎡</span></template>
      </el-table-column>
      <el-table-column label="使用面积" align="center" prop="useArea" width="110">
        <template #default="scope"><span>{{ scope.row.useArea || '-' }} ㎡</span></template>
      </el-table-column>
      <el-table-column label="房屋状态" align="center" prop="status" width="120">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'info' : scope.row.status === '1' ? 'success' : 'warning'" effect="dark">
            {{ scope.row.status === '0' ? '未售' : scope.row.status === '1' ? '已售未入住' : '已入住' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="关联业主" align="center" prop="ownerName" width="130">
        <template #default="scope">
          <el-tag v-if="scope.row.ownerName" type="success" effect="plain">{{ scope.row.ownerName }}</el-tag>
          <span v-else class="text-muted" style="color: #909399;">无</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['property:room:edit']">修改</el-button>
          <el-button link type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['property:room:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" v-model="open" width="560px" append-to-body draggable>
      <el-form ref="roomRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属楼宇" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择楼宇" filterable style="width:100%">
                <el-option v-for="item in buildingOptions" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋名称" prop="roomName">
              <el-input v-model="form.roomName" placeholder="如：301" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋编号" prop="roomCode">
              <el-input v-model="form.roomCode" placeholder="如：B001-301" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在楼层" prop="floorNum">
              <el-input-number v-model="form.floorNum" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="constructionArea">
              <el-input-number v-model="form.constructionArea" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用面积" prop="useArea">
              <el-input-number v-model="form.useArea" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="房屋状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
                <el-option label="未售" value="0" />
                <el-option label="已售未入住" value="1" />
                <el-option label="已入住" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联业主" prop="ownerId">
              <el-select v-model="form.ownerId" placeholder="选择或搜索关联业主 (选填)" filterable clearable style="width:100%">
                <el-option v-for="item in ownerOptions" :key="item.ownerId" :label="item.ownerName" :value="item.ownerId" />
              </el-select>
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

<script setup name="Room">
import { listRoom, getRoom, delRoom, addRoom, updateRoom } from "@/api/property/room";
import { listAllBuilding } from "@/api/property/building";
import { listAllOwner } from "@/api/property/owner";

const { proxy } = getCurrentInstance();
const roomList = ref([]);
const open = ref(false);
const loading = ref(false);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const buildingOptions = ref([]);
const ownerOptions = ref([]);

const data = reactive({
  form: {},
  queryParams: { pageNum: 1, pageSize: 10, roomName: undefined, roomCode: undefined, buildingId: undefined, status: undefined, ownerId: undefined },
  rules: {
    roomName: [{ required: true, message: "房屋名称不能为空", trigger: "blur" }],
    roomCode: [{ required: true, message: "房屋编号不能为空", trigger: "blur" }],
    buildingId: [{ required: true, message: "请选择所属楼宇", trigger: "change" }]
  }
});
const { queryParams, form, rules } = toRefs(data);

function getList() {
  loading.value = true;
  listRoom(queryParams.value).then(res => {
    roomList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}
function loadOptions() {
  listAllBuilding().then(res => { buildingOptions.value = res.data || []; });
  listAllOwner().then(res => { ownerOptions.value = res.rows || res.data || []; });
}
function cancel() { open.value = false; reset(); }
function reset() {
  form.value = { roomId: undefined, buildingId: undefined, roomName: undefined, roomCode: undefined, floorNum: 1, constructionArea: 0, useArea: 0, status: "0", remark: undefined, ownerId: undefined };
  proxy.resetForm("roomRef");
}
function handleQuery() { queryParams.value.pageNum = 1; getList(); }
function resetQuery() { proxy.resetForm("queryRef"); handleQuery(); }
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.roomId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
function handleAdd() { reset(); loadOptions(); open.value = true; title.value = "添加房屋"; }
function handleUpdate(row) {
  reset();
  loadOptions();
  const roomId = row.roomId || ids.value;
  getRoom(roomId).then(res => { form.value = res.data; open.value = true; title.value = "修改房屋信息"; });
}
function submitForm() {
  proxy.$refs["roomRef"].validate(valid => {
    if (valid) {
      if (form.value.roomId != undefined) {
        updateRoom(form.value).then(res => { proxy.$modal.msgSuccess("修改成功"); open.value = false; getList(); });
      } else {
        addRoom(form.value).then(res => { proxy.$modal.msgSuccess("新增成功"); open.value = false; getList(); });
      }
    }
  });
}
function handleDelete(row) {
  const roomIds = row.roomId || ids.value;
  proxy.$modal.confirm('确认删除？').then(() => delRoom(roomIds)).then(() => { getList(); proxy.$modal.msgSuccess("删除成功"); }).catch(() => {});
}

loadOptions();
getList();
</script>
