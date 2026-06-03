<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>小区公告</span>
        </div>
      </template>

      <el-timeline>
        <el-timeline-item v-for="(item, index) in list" :key="index" :timestamp="parseTime(item.publishTime, '{y}-{m}-{d} {h}:{i}')" placement="top" :type="item.noticeType === '2' ? 'danger' : 'primary'">
          <el-card shadow="hover">
            <h4 style="margin-top: 0; display: flex; align-items: center;">
              <el-tag size="small" :type="item.noticeType === '2' ? 'danger' : 'primary'" style="margin-right: 10px;">
                {{ item.noticeType === '0' ? '通知' : item.noticeType === '1' ? '公告' : '紧急' }}
              </el-tag>
              {{ item.noticeTitle }}
            </h4>
            <div style="color: #606266; line-height: 1.6; white-space: pre-wrap;">{{ item.noticeContent }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="!loading && list.length === 0" description="暂无最新公告" />
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="MyNotice">
import request from '@/utils/request';

const list = ref([]);
const loading = ref(true);
const total = ref(0);
const queryParams = ref({ pageNum: 1, pageSize: 10 });

function getList() {
  loading.value = true;
  request({ url: '/property/portal/myNotice', method: 'get', params: queryParams.value }).then(res => {
    list.value = res.rows;
    total.value = res.total;
    loading.value = false;
  }).catch(() => { loading.value = false; });
}

getList();
</script>
