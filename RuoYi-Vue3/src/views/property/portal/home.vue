<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>业主信息</span>
            </div>
          </template>
          <div class="info-body">
            <el-avatar :size="80" icon="UserFilled" />
            <div class="info-name">{{ user.nickName || '未登录' }}</div>
            <div class="info-desc">欢迎使用物业自助服务门户</div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 快捷入口卡片 -->
      <el-col :span="16">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>快捷服务</span>
            </div>
          </template>
          <el-row :gutter="20" class="quick-links">
            <el-col :span="6" v-for="item in quickLinks" :key="item.path">
              <div class="quick-item" @click="$router.push(item.path)">
                <el-icon :class="item.color"><component :is="item.icon" /></el-icon>
                <span>{{ item.name }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近公告和提醒 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最新公告</span>
              <el-button link @click="$router.push('/portal/myNotice')">更多</el-button>
            </div>
          </template>
          <el-empty v-if="!notices.length" description="暂无公告" />
          <ul v-else class="notice-list">
            <li v-for="notice in notices" :key="notice.noticeId">
              <span class="notice-tag" :class="notice.noticeType === '2' ? 'danger' : 'primary'">
                {{ notice.noticeType === '0' ? '通知' : notice.noticeType === '1' ? '公告' : '紧急' }}
              </span>
              <span class="notice-title">{{ notice.noticeTitle }}</span>
              <span class="notice-time">{{ parseTime(notice.publishTime, '{y}-{m}-{d}') }}</span>
            </li>
          </ul>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="box-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>待办提醒</span>
            </div>
          </template>
          <div style="padding: 20px; color: #909399; text-align: center;">
            <el-empty description="暂无待办事项" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="PortalHome">
/**
 * 业主门户首页组件
 * 展示业主的个人信息、快捷服务入口、最新公告和待办提醒。
 * 数据从后端API实时获取。
 *
 * TODO: 待办提醒目前还是静态的，后续可以对接后端待办接口
 */
import request from '@/utils/request';
import useUserStore from '@/store/modules/user';

const userStore = useUserStore();

// 如果用户已登录就用真实信息，否则显示"访客"
const user = computed(() => {
  console.log('【PortalHome】当前用户信息:', userStore.name);
  return userStore.name ? userStore : { nickName: '访客' };
});

// 快捷导航菜单，点击跳转到对应页面
const quickLinks = [
  { name: '我的账单', icon: 'Money', path: '/portal/myBill', color: 'text-warning' },
  { name: '在线报修', icon: 'Service', path: '/portal/myRepair', color: 'text-danger' },
  { name: '访客登记', icon: 'Guide', path: '/portal/myVisitor', color: 'text-primary' },
  { name: '投诉建议', icon: 'Comment', path: '/portal/myComplaint', color: 'text-success' },
];

const notices = ref([]);

/**
 * 获取最新公告，限制显示前5条
 */
function getNotices() {
  console.log('【PortalHome】正在获取最新公告...');
  request({ url: '/property/portal/myNotice', method: 'get', params: { pageNum: 1, pageSize: 5 } }).then(res => {
    notices.value = res.rows || [];
    console.log('【PortalHome】公告获取成功，共' + notices.value.length + '条');
  });
}

getNotices();
</script>

<style scoped>
.box-card { min-height: 250px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-body { display: flex; flex-direction: column; align-items: center; padding: 20px 0; }
.info-name { margin-top: 15px; font-size: 20px; font-weight: bold; color: #303133; }
.info-desc { margin-top: 10px; font-size: 14px; color: #909399; }

.quick-links { text-align: center; padding: 20px 0; }
.quick-item { cursor: pointer; padding: 15px; border-radius: 8px; transition: all 0.3s; }
.quick-item:hover { background-color: #f5f7fa; transform: translateY(-3px); }
.quick-item .el-icon { font-size: 32px; display: block; margin: 0 auto 10px; }
.quick-item span { font-size: 14px; color: #606266; }

.text-warning { color: #E6A23C; }
.text-danger { color: #F56C6C; }
.text-primary { color: #409EFF; }
.text-success { color: #67C23A; }

.notice-list { list-style: none; padding: 0; margin: 0; }
.notice-list li { display: flex; align-items: center; padding: 12px 0; border-bottom: 1px solid #ebeef5; }
.notice-list li:last-child { border-bottom: none; }
.notice-tag { font-size: 12px; padding: 2px 6px; border-radius: 4px; margin-right: 10px; }
.notice-tag.primary { background: #ecf5ff; color: #409eff; }
.notice-tag.danger { background: #fef0f0; color: #f56c6c; }
.notice-title { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; font-size: 14px; color: #303133; cursor: pointer; }
.notice-title:hover { color: #409EFF; }
.notice-time { font-size: 13px; color: #909399; margin-left: 15px; }
</style>
