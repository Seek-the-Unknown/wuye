<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="车牌号" prop="plateNumber">
        <el-input
          v-model="queryParams.plateNumber"
          placeholder="请输入车牌号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付状态" prop="payStatus">
        <el-select v-model="queryParams.payStatus" placeholder="支付状态" clearable>
          <el-option label="未支付" value="0" />
          <el-option label="已支付" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="车辆类型" prop="vehicleType">
        <el-select v-model="queryParams.vehicleType" placeholder="车辆类型" clearable>
          <el-option label="临时车" value="0" />
          <el-option label="月租车" value="1" />
          <el-option label="业主车" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['property:vehicleRecord:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="recordId" width="80" />
      <el-table-column label="车牌号" align="center" prop="plateNumber" width="120" />
      <el-table-column label="小区" align="center" prop="communityName" width="120" show-overflow-tooltip />
      <el-table-column label="车辆类型" align="center" prop="vehicleType" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.vehicleType === '0'" type="info" size="small">临时车</el-tag>
          <el-tag v-else-if="scope.row.vehicleType === '1'" size="small">月租车</el-tag>
          <el-tag v-else-if="scope.row.vehicleType === '2'" type="success" size="small">业主车</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入场时间" align="center" prop="entryTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.entryTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出场时间" align="center" prop="exitTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.exitTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="停车时长(h)" align="center" prop="parkingDuration" width="100" />
      <el-table-column label="应收金额" align="center" prop="feeAmount" width="100" />
      <el-table-column label="实收金额" align="center" prop="paidAmount" width="100" />
      <el-table-column label="支付状态" align="center" prop="payStatus" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.payStatus === '1' ? 'success' : 'danger'" size="small">
            {{ scope.row.payStatus === '1' ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="140">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['property:vehicleRecord:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 详情对话框 -->
    <el-dialog title="车辆进出记录详情" :visible.sync="open" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ detail.recordId }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">
          <span style="font-weight: bold; color: #1a56db;">{{ detail.plateNumber }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="小区">{{ detail.communityName }}</el-descriptions-item>
        <el-descriptions-item label="车辆类型">
          <el-tag v-if="detail.vehicleType === '0'" type="info" size="small">临时车</el-tag>
          <el-tag v-else-if="detail.vehicleType === '1'" size="small">月租车</el-tag>
          <el-tag v-else-if="detail.vehicleType === '2'" type="success" size="small">业主车</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入场时间">{{ parseTime(detail.entryTime) }}</el-descriptions-item>
        <el-descriptions-item label="出场时间">{{ parseTime(detail.exitTime) }}</el-descriptions-item>
        <el-descriptions-item label="停车时长">{{ detail.parkingDuration }} 小时</el-descriptions-item>
        <el-descriptions-item label="计费单价">{{ detail.unitPrice }} 元/小时</el-descriptions-item>
        <el-descriptions-item label="应收金额">{{ detail.feeAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="实收金额">{{ detail.paidAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <el-tag :type="detail.payStatus === '1' ? 'success' : 'danger'">
            {{ detail.payStatus === '1' ? '已支付' : '未支付' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ parseTime(detail.payTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listVehicleRecord, getVehicleRecord, delVehicleRecord } from "@/api/property/vehicleRecord"

export default {
  name: "VehicleRecord",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      recordList: [],
      open: false,
      detail: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        plateNumber: undefined,
        payStatus: undefined,
        vehicleType: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listVehicleRecord(this.queryParams).then(response => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleDetail(row) {
      getVehicleRecord(row.recordId).then(response => {
        this.detail = response.data
        this.open = true
      })
    },
    handleDelete(row) {
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除所选记录？').then(function() {
        return delVehicleRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
