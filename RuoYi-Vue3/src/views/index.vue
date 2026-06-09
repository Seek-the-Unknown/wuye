<template>
  <div class="dashboard-container">
    <!-- 未分配角色/房产的新注册用户温馨提示栏 -->
    <div v-if="isGuest" class="guest-alert-bar animate__animated animate__fadeInDown">
      <div class="alert-content">
        <span class="alert-sparkle">✨</span>
        <span class="alert-text">
          <strong>欢迎来到智慧物业管理平台！</strong> 检测到您的账号尚未绑定房产，当前正为您展示 <strong>【业主端】 模拟演示数据</strong>。您可联系系统管理员（admin）为您配置角色和房屋绑定。
        </span>
      </div>
      <el-button type="primary" size="small" class="alert-btn" @click="$router.push('/system/user')">
        去管理用户
      </el-button>
    </div>

    <!-- ==================== 1. 超级管理员 / 物业管理员 首页 ==================== -->
    <template v-if="isAdmin">
      <!-- 欢迎横幅 -->
      <div class="welcome-banner">
        <div class="welcome-left">
          <h2 class="welcome-title">
            <span class="greeting-icon">👋</span>
            {{ greetingText }}，{{ nickName }}
          </h2>
          <p class="welcome-date">{{ currentDate }} {{ currentWeekday }} · 智慧物业管理平台</p>
        </div>
        <div class="welcome-actions">
          <el-button class="action-btn" @click="$router.push('/property/repair')">
            <el-icon><Tools /></el-icon>
            <span>报修管理</span>
          </el-button>
          <el-button class="action-btn" @click="$router.push('/property/visitor')">
            <el-icon><Avatar /></el-icon>
            <span>访客管理</span>
          </el-button>
          <el-button class="action-btn" @click="$router.push('/property/notice')">
            <el-icon><Bell /></el-icon>
            <span>发布公告</span>
          </el-button>
        </div>
      </div>

      <!-- KPI 统计卡片 -->
      <div class="kpi-row">
        <div class="kpi-card kpi-purple" @click="$router.push('/property/community')">
          <div class="kpi-icon-wrap">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ animatedStats.communityCount }}</span>
            <span class="kpi-label">小区总数</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-teal" @click="$router.push('/property/owner')">
          <div class="kpi-icon-wrap">
            <el-icon><User /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ animatedStats.ownerCount }}</span>
            <span class="kpi-label">业主总数</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-amber" @click="$router.push('/property/room')">
          <div class="kpi-icon-wrap">
            <el-icon><House /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ animatedStats.roomCount }}</span>
            <span class="kpi-label">房屋总数</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-rose" @click="$router.push('/property/repair')">
          <div class="kpi-icon-wrap">
            <el-icon><WarnTriangleFilled /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ animatedStats.pendingRepairCount }}</span>
            <span class="kpi-label">待处理工单</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-row">
        <div class="chart-card chart-main">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-blue"></span>
              月度报修趋势
            </h3>
          </div>
          <div ref="repairChartRef" class="chart-body"></div>
        </div>

        <div class="chart-card chart-side">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-green"></span>
              物业费收缴率
            </h3>
          </div>
          <div ref="feeChartRef" class="chart-body"></div>
        </div>
      </div>

      <!-- 列表区域 -->
      <div class="list-row">
        <div class="list-card list-main">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-orange"></span>
              最近报修工单
            </h3>
            <el-button link type="primary" class="view-all-btn" @click="$router.push('/property/repair')">
              查看全部 →
            </el-button>
          </div>
          <div class="table-wrap">
            <el-table :data="recentRepairs" style="width: 100%" :header-cell-style="{ background: '#f8f9fe', color: '#606266', fontWeight: 600 }" size="default">
              <el-table-column label="报修标题" prop="repairTitle" show-overflow-tooltip min-width="180" />
              <el-table-column label="报修人" prop="ownerName" width="100" />
              <el-table-column label="状态" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="repairStatusType(row.repairStatus)" size="small" effect="dark" round>
                    {{ repairStatusText(row.repairStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="报修时间" width="170" align="center">
                <template #default="{ row }">
                  {{ formatTime(row.createTime) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <div class="list-card list-side">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-purple"></span>
              待办事项
            </h3>
          </div>
          <div class="todo-list">
            <div class="todo-item" @click="$router.push('/property/repair')">
              <div class="todo-icon todo-icon-red">
                <el-icon><Tools /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">待处理报修</span>
                <span class="todo-desc">需要及时安排维修</span>
              </div>
              <span class="todo-count todo-count-red">{{ todoSummary.pendingRepairs || 0 }}</span>
            </div>
            <div class="todo-item" @click="$router.push('/property/repair')">
              <div class="todo-icon todo-icon-blue">
                <el-icon><Loading /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">处理中工单</span>
                <span class="todo-desc">跟进维修进度</span>
              </div>
              <span class="todo-count todo-count-blue">{{ todoSummary.processingRepairs || 0 }}</span>
            </div>
            <div class="todo-item" @click="$router.push('/property/complaint')">
              <div class="todo-icon todo-icon-orange">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">待处理投诉</span>
                <span class="todo-desc">业主反馈需要回复</span>
              </div>
              <span class="todo-count todo-count-orange">{{ todoSummary.pendingComplaints || 0 }}</span>
            </div>
            <div class="todo-item" @click="$router.push('/property/visitor')">
              <div class="todo-icon todo-icon-teal">
                <el-icon><Avatar /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">待审核访客</span>
                <span class="todo-desc">访客进入需审批</span>
              </div>
              <span class="todo-count todo-count-teal">{{ todoSummary.pendingVisitors || 0 }}</span>
            </div>
            <div class="todo-item" @click="$router.push('/property/feeRecord')">
              <div class="todo-icon todo-icon-amber">
                <el-icon><Money /></el-icon>
              </div>
              <div class="todo-info">
                <span class="todo-label">未缴费账单</span>
                <span class="todo-desc">提醒业主按时缴费</span>
              </div>
              <span class="todo-count todo-count-amber">{{ todoSummary.unpaidFees || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ==================== 2. 业主 (Property Owner) 首页 ==================== -->
    <template v-else-if="isOwner">
      <!-- 业主欢迎横幅 -->
      <div class="welcome-banner owner-banner">
        <div class="welcome-left">
          <h2 class="welcome-title">
            <span class="greeting-icon">🏠</span>
            {{ greetingText }}，{{ ownerRealName || nickName }} 业主
          </h2>
          <p class="welcome-date">欢迎回到您的温馨家园 · 当前绑定房屋：<strong>{{ ownerStats.roomName }}</strong></p>
        </div>
        <div class="welcome-actions">
          <el-button class="action-btn" @click="$router.push('/portal/myRepair')">
            <el-icon><Tools /></el-icon>
            <span>在线报修</span>
          </el-button>
          <el-button class="action-btn" @click="openVisitorDialog">
            <el-icon><Avatar /></el-icon>
            <span>访客申请</span>
          </el-button>
          <el-button class="action-btn" @click="$router.push('/portal/myBill')">
            <el-icon><Money /></el-icon>
            <span>在线缴费</span>
          </el-button>
          <el-button class="action-btn" @click="$router.push('/portal/myComplaint')">
            <el-icon><ChatDotRound /></el-icon>
            <span>投诉建议</span>
          </el-button>
        </div>
      </div>

      <!-- 业主 KPI 数字看板 -->
      <div class="kpi-row">
        <div class="kpi-card kpi-purple" @click="$router.push('/portal/myRepair')">
          <div class="kpi-icon-wrap">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ ownerStats.pendingRepairs }}</span>
            <span class="kpi-label">进行中报修</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-rose" @click="$router.push('/portal/myBill')">
          <div class="kpi-icon-wrap">
            <el-icon><Money /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ ownerStats.unpaidFees }}</span>
            <span class="kpi-label">未支付账单</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-teal" @click="openVisitorDialog">
          <div class="kpi-icon-wrap">
            <el-icon><Avatar /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ ownerStats.visitorPasses }}</span>
            <span class="kpi-label">有效访客凭证</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-amber" @click="$router.push('/portal/myRoom')">
          <div class="kpi-icon-wrap">
            <el-icon><House /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">正常</span>
            <span class="kpi-label">房屋状态</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
      </div>

      <!-- 业主中部两栏：社区公告栏与生活助手 -->
      <div class="chart-row">
        <!-- 社区公告 -->
        <div class="chart-card chart-main notice-board">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-blue"></span>
              最新社区公告栏
            </h3>
            <el-button link type="primary" class="view-all-btn" @click="$router.push('/portal/myNotice')">
              查看更多公告
            </el-button>
          </div>
          <div class="notice-list">
            <div v-for="item in communityNotices" :key="item.id" class="notice-item">
              <div class="notice-title-row">
                <el-tag :type="item.type" size="small" effect="dark" class="notice-tag">
                  {{ item.type === 'danger' ? '停水停电' : item.type === 'success' ? '社区活动' : '温馨提示' }}
                </el-tag>
                <h4 class="notice-title-text">{{ item.title }}</h4>
                <span class="notice-time">{{ item.time }}</span>
              </div>
              <p class="notice-summary">{{ item.summary }}</p>
            </div>
          </div>
        </div>

        <!-- 生活助手/天气/物业服务 -->
        <div class="chart-card chart-side assistant-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-green"></span>
              24小时管家服务
            </h3>
          </div>
          <div class="assistant-body">
            <div class="weather-box">
              <div class="weather-left">
                <span class="weather-temp">26°C</span>
                <span class="weather-text">南京市 · 晴朗 🌤️</span>
              </div>
              <div class="weather-right">
                <p>湿度：45%</p>
                <p>空气质量：优 (32)</p>
              </div>
            </div>
            <div class="service-contacts">
              <div class="contact-item">
                <span class="contact-label">物业管家热线：</span>
                <span class="contact-value">025-8888-8888</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">24小时工程急修：</span>
                <span class="contact-value">025-9999-9999</span>
              </div>
              <div class="contact-item">
                <span class="contact-label">小区安防控制室：</span>
                <span class="contact-value">025-7777-7777</span>
              </div>
            </div>
            <div class="service-tips">
              <div class="tip-icon">💡</div>
              <p class="tip-text">盛夏来临，空调负荷增大。离家请注意关闭大功率电器，做好用电安全防护。</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 业主底部：我的报修历史进度 -->
      <div class="list-row">
        <div class="list-card list-main">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-orange"></span>
              我的报修进度跟进
            </h3>
            <el-button link type="primary" class="view-all-btn" @click="$router.push('/portal/myRepair')">
              去提报修 →
            </el-button>
          </div>
          <div class="table-wrap">
            <el-table :data="myRepairs" style="width: 100%" :header-cell-style="{ background: '#f8f9fe', color: '#606266', fontWeight: 600 }" size="default">
              <el-table-column label="工单编号" prop="id" width="150" />
              <el-table-column label="故障项" prop="title" min-width="180" />
              <el-table-column label="分类" prop="category" width="120" align="center" />
              <el-table-column label="分配维修师傅" prop="worker" width="180" />
              <el-table-column label="提报时间" prop="time" width="140" align="center" />
              <el-table-column label="当前状态" width="110" align="center">
                <template #default="{ row }">
                  <el-tag :type="repairStatusType(row.status)" size="small" effect="dark" round>
                    {{ repairStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <div class="list-card list-side feedback-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-purple"></span>
              共建美好家园
            </h3>
          </div>
          <div class="feedback-body">
            <p class="feedback-intro">小区的美好建设离不开每位业主的建议。如果您对小区管理、卫生绿化、物业服务有任何意见，欢迎在此写下：</p>
            <el-input
              v-model="feedbackText"
              type="textarea"
              :rows="4"
              placeholder="请输入您的意见或建议..."
              resize="none"
              class="feedback-input"
            />
            <el-button type="primary" class="feedback-submit-btn" @click="submitFeedback">
              提交反馈意见
            </el-button>
          </div>
        </div>
      </div>
    </template>

    <!-- ==================== 3. 维修工人 (Worker) 首页 ==================== -->
    <template v-else-if="isWorker">
      <!-- 工人欢迎横幅 -->
      <div class="welcome-banner worker-banner">
        <div class="welcome-left">
          <h2 class="welcome-title">
            <span class="greeting-icon">🛠️</span>
            {{ greetingText }}，{{ nickName }} 师傅！
          </h2>
          <p class="welcome-date">今天辛苦了！本月已累计维修完成 <strong>{{ workerStats.completedJobs }}</strong> 单，继续保持优秀服务！</p>
        </div>
        <div class="welcome-actions">
          <el-button class="action-btn" @click="$router.push('/worker/myTask')">
            <el-icon><Tools /></el-icon>
            <span>工单接单中心</span>
          </el-button>
          <el-button class="action-btn" @click="showClockIn">
            <el-icon><Avatar /></el-icon>
            <span>考勤打卡</span>
          </el-button>
        </div>
      </div>

      <!-- 工人 KPI 数字看板 -->
      <div class="kpi-row">
        <div class="kpi-card kpi-rose" @click="$router.push('/worker/myTask')">
          <div class="kpi-icon-wrap">
            <el-icon><WarnTriangleFilled /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ workerStats.todoJobs }}</span>
            <span class="kpi-label">待我接单/处理</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-purple" @click="$router.push('/worker/myTask')">
          <div class="kpi-icon-wrap">
            <el-icon><Loading /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ workerStats.doingJobs }}</span>
            <span class="kpi-label">正在维修中</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-teal">
          <div class="kpi-icon-wrap">
            <el-icon><Tools /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ workerStats.completedJobs }}</span>
            <span class="kpi-label">本月已完成</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
        <div class="kpi-card kpi-amber">
          <div class="kpi-icon-wrap">
            <el-icon><User /></el-icon>
          </div>
          <div class="kpi-info">
            <span class="kpi-value">{{ workerStats.rating }} ★</span>
            <span class="kpi-label">我的服务评分</span>
          </div>
          <div class="kpi-decoration"></div>
        </div>
      </div>

      <!-- 工人中部：指派给我的待处理工单 -->
      <div class="chart-row">
        <div class="chart-card chart-main">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-blue"></span>
              我的专属待办工单
            </h3>
            <el-button link type="primary" class="view-all-btn" @click="$router.push('/worker/myTask')">
              前往工单列表 →
            </el-button>
          </div>
          <div class="worker-job-list">
            <div v-for="job in workerJobs" :key="job.id" class="job-item-card">
              <div class="job-item-header">
                <div class="job-meta">
                  <span class="job-id">{{ job.id }}</span>
                  <span class="job-time">{{ job.time }}</span>
                </div>
                <el-tag :type="job.priority === 'high' ? 'danger' : job.priority === 'medium' ? 'warning' : 'info'" size="small" effect="dark">
                  {{ job.priority === 'high' ? '紧急紧急' : job.priority === 'medium' ? '普通处理' : '低优跟进' }}
                </el-tag>
              </div>
              <div class="job-item-body">
                <h4 class="job-title">{{ job.title }}</h4>
                <div class="job-info-row">
                  <p><strong>业主：</strong>{{ job.owner }} ({{ job.phone }})</p>
                  <p><strong>地址：</strong>{{ job.address }}</p>
                </div>
                <p class="job-desc"><strong>故障描述：</strong>{{ job.desc }}</p>
              </div>
              <div class="job-item-actions">
                <el-button v-if="job.status === '0'" type="primary" size="default" class="job-action-btn" @click="handleJob(job.realId, 'accept')">一键接单</el-button>
                <el-button v-if="job.status === '1'" type="success" size="default" plain class="job-action-btn" @click="handleJob(job.realId, 'complete')">维修完成</el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 安全警示/技术宝典 -->
        <div class="chart-card chart-side assistant-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-green"></span>
              安全施工规程与提示
            </h3>
          </div>
          <div class="assistant-body">
            <div class="worker-tip-card red-warning">
              <h4>⚠️ 安全第一，规范施工</h4>
              <p>高空作业必须正确佩戴好安全带！带电排查电路故障时，必须使用绝缘手套和绝缘工具，严禁违规带电接线！</p>
            </div>
            <div class="worker-tip-card info-card">
              <h4>💡 服务礼仪五步法</h4>
              <p>1. 敲门轻扣三下并自报家门；<br>2. 进门必须自觉换上清洁鞋套；<br>3. 详细询问报修故障并礼貌说明方案；<br>4. 维修完成后主动清理施工垃圾；<br>5. 礼貌提醒业主在系统上给出评价。</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 工人底部：业主评价反馈 -->
      <div class="list-row">
        <div class="list-card list-main">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-dot dot-purple"></span>
              我收到的最新业主评价
            </h3>
          </div>
          <div class="feedback-list">
            <div v-for="fb in workerFeedbacks" :key="fb.id" class="feedback-item">
              <div class="feedback-item-header">
                <div class="feedback-owner-info">
                  <span class="fb-owner-name">{{ fb.owner }}</span>
                  <span class="fb-owner-room">({{ fb.room }})</span>
                </div>
                <div class="fb-rating">
                  <span v-for="n in fb.rating" :key="n" class="star">★</span>
                  <span class="fb-date">{{ fb.date }}</span>
                </div>
              </div>
              <p class="fb-content">“{{ fb.content }}”</p>
            </div>
          </div>
        </div>
      </div>
      <!-- 业主端快捷访客自主登记 Dialog 弹窗 -->
      <el-dialog title="新建访客自助预约" v-model="visitorOpen" width="480px" append-to-body destroy-on-close>
        <el-form ref="visitorFormRef" :model="visitorForm" :rules="visitorRules" label-width="90px">
          <el-form-item label="访客姓名" prop="visitorName">
            <el-input v-model="visitorForm.visitorName" placeholder="请输入访客真实姓名" />
          </el-form-item>
          <el-form-item label="访客手机" prop="visitorPhone">
            <el-input v-model="visitorForm.visitorPhone" placeholder="请输入11位手机号" />
          </el-form-item>
          <el-form-item label="到访时间" prop="visitTime">
            <el-date-picker
              v-model="visitorForm.visitTime"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="选择到访时间"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="来访事由" prop="visitReason">
            <el-select v-model="visitorForm.visitReason" placeholder="请选择或输入" filterable allow-create default-first-option style="width: 100%">
              <el-option label="亲戚探访" value="亲戚探访" />
              <el-option label="朋友聚会" value="朋友聚会" />
              <el-option label="快递配送" value="快递配送" />
              <el-option label="上门维修" value="上门维修" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button type="primary" @click="submitVisitor">提交预约</el-button>
          <el-button @click="visitorOpen = false">取消</el-button>
        </template>
      </el-dialog>
    </template>
  </div>
</template>

<script setup name="Index">
import * as echarts from 'echarts'
import { getDashboardStats, getRepairTrend, getFeeCollection, getRecentRepairs, getTodoSummary } from '@/api/property/dashboard'
import useUserStore from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { getWorkerRepairs, acceptWorkerRepair, finishWorkerRepair } from '@/api/property/workerPortal'

const userStore = useUserStore()
const nickName = computed(() => userStore.nickName || '管理员')

// 核心：基于角色进行首页内容判断
const roles = computed(() => userStore.roles || [])
const isAdmin = computed(() => roles.value.includes('admin') || roles.value.includes('property_admin'))
const isWorker = computed(() => roles.value.includes('worker') || roles.value.includes('property_worker'))

// 只要既不是管理员也不是工人，就展示为业主/普通用户视图，保证绝对不留白
const isOwner = computed(() => !isAdmin.value && !isWorker.value)

const hasBinding = ref(false)
const ownerRealName = ref('')

// 只要既不是管理员、工人，并且在数据库里还没有绑定房屋，就判定为未绑定的演示访客
const isGuest = computed(() => !isAdmin.value && !isWorker.value && !hasBinding.value)

// 时间问候语
const greetingText = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})
const currentWeekday = computed(() => {
  return ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][new Date().getDay()]
})

// ==================== 1. 超级管理员 / 物业管理员 逻辑 ====================
// KPI 数据
const stats = ref({})
const animatedStats = reactive({
  communityCount: 0,
  ownerCount: 0,
  roomCount: 0,
  pendingRepairCount: 0
})

function animateCount(key, target) {
  const duration = 1200
  const steps = 40
  const step = target / steps
  let current = 0
  const interval = setInterval(() => {
    current += step
    if (current >= target) {
      animatedStats[key] = target
      clearInterval(interval)
    } else {
      animatedStats[key] = Math.floor(current)
    }
  }, duration / steps)
}

// 报修列表
const recentRepairs = ref([])
// 待办摘要
const todoSummary = ref({})

// ECharts 引用
const repairChartRef = ref(null)
const feeChartRef = ref(null)
let repairChart = null
let feeChart = null

// 报修状态
function repairStatusText(status) {
  const map = { '0': '待处理', '1': '处理中', '2': '已完成', '3': '已关闭' }
  return map[status] || '未知'
}
function repairStatusType(status) {
  const map = { '0': 'warning', '1': '', '2': 'success', '3': 'info' }
  return map[status] || 'info'
}

function formatTime(time) {
  if (!time) return '-'
  const d = new Date(time)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 初始化报修趋势图
function initRepairChart(data) {
  if (!repairChartRef.value) return
  repairChart = echarts.init(repairChartRef.value)
  const months = data.map(d => d.month)
  const counts = data.map(d => d.count)
  repairChart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e0e0e0',
      borderWidth: 1,
      textStyle: { color: '#333' },
      formatter: '{b0}<br/>报修工单数：<b>{c0}</b>'
    },
    grid: { left: 50, right: 30, top: 30, bottom: 40 },
    xAxis: {
      type: 'category',
      data: months,
      axisLine: { lineStyle: { color: '#e0e0e0' } },
      axisLabel: { color: '#888' },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0', type: 'dashed' } },
      axisLabel: { color: '#888' }
    },
    series: [{
      data: counts,
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
        { offset: 0, color: '#7a22ff' },
        { offset: 1, color: '#00e5ff' }
      ])},
      itemStyle: { color: '#7a22ff', borderWidth: 2, borderColor: '#fff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(122, 34, 255, 0.25)' },
          { offset: 1, color: 'rgba(122, 34, 255, 0.02)' }
        ])
      }
    }]
  })
}

// 初始化收缴率环形图
function initFeeChart(data) {
  if (!feeChartRef.value) return
  feeChart = echarts.init(feeChartRef.value)
  const payRate = data.payRate || 0
  feeChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255,255,255,0.95)',
      borderColor: '#e0e0e0',
      borderWidth: 1,
      textStyle: { color: '#333' },
      formatter: '{b}: ¥{c} ({d}%)'
    },
    legend: {
      bottom: 10,
      left: 'center',
      textStyle: { color: '#666', fontSize: 12 },
      itemWidth: 10,
      itemHeight: 10,
      itemGap: 20
    },
    graphic: [{
      type: 'text',
      left: 'center',
      top: '38%',
      style: {
        text: payRate.toFixed(1) + '%',
        fontSize: 28,
        fontWeight: 'bold',
        fill: '#333',
        textAlign: 'center'
      }
    }, {
      type: 'text',
      left: 'center',
      top: '52%',
      style: {
        text: '收缴率',
        fontSize: 13,
        fill: '#999',
        textAlign: 'center'
      }
    }],
    series: [{
      type: 'pie',
      radius: ['55%', '75%'],
      center: ['50%', '46%'],
      avoidLabelOverlap: false,
      label: { show: false },
      labelLine: { show: false },
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 3 },
      data: [
        { value: data.paidAmount || 0, name: '已缴费', itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#36d399' },
          { offset: 1, color: '#22c55e' }
        ])}},
        { value: data.unpaidAmount || 0, name: '未缴费', itemStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#fb923c' },
          { offset: 1, color: '#f97316' }
        ])}}
      ]
    }]
  })
}

// 窗口大小变化时重新绘制图表
function handleResize() {
  repairChart && repairChart.resize()
  feeChart && feeChart.resize()
}

// 加载管理员所有数据
async function loadAdminData() {
  try {
    const [statsRes, trendRes, feeRes, repairsRes, todoRes] = await Promise.allSettled([
      getDashboardStats(),
      getRepairTrend(),
      getFeeCollection(),
      getRecentRepairs(),
      getTodoSummary()
    ])

    if (statsRes.status === 'fulfilled' && statsRes.value.data) {
      stats.value = statsRes.value.data
      animateCount('communityCount', stats.value.communityCount || 0)
      animateCount('ownerCount', stats.value.ownerCount || 0)
      animateCount('roomCount', stats.value.roomCount || 0)
      animateCount('pendingRepairCount', stats.value.pendingRepairCount || 0)
    }

    if (trendRes.status === 'fulfilled' && trendRes.value.data) {
      nextTick(() => initRepairChart(trendRes.value.data))
    }

    if (feeRes.status === 'fulfilled' && feeRes.value.data) {
      nextTick(() => initFeeChart(feeRes.value.data))
    }

    if (repairsRes.status === 'fulfilled') {
      recentRepairs.value = repairsRes.value.rows || repairsRes.value.data || []
    }

    if (todoRes.status === 'fulfilled' && todoRes.value.data) {
      todoSummary.value = todoRes.value.data
    }
  } catch (e) {
    console.error('Dashboard load error:', e)
  }
}

// ==================== 2. 业主 (Property Owner) 逻辑与模拟数据 ====================
const ownerStats = reactive({
  pendingRepairs: 1,
  unpaidFees: 1,
  visitorPasses: 2,
  roomName: '盛世嘉园 A区3栋1602室'
})

const communityNotices = ref([
  { id: 1, title: '关于本周六小区供水管道清洗停水通知', type: 'danger', time: '10分钟前', summary: '为了保障居民饮水安全，物业将于本周六（6月6日）9:00-17:00对主管道进行全面清洗，期间将暂停供水，请各位业主提前做好蓄水准备，给您带来的不便敬请谅解！' },
  { id: 2, title: '首届“和谐邻里·爱在盛夏”社区百家宴活动邀请函', type: 'success', time: '2小时前', summary: '夏日炎炎，邻里情深。物业联合居委会将于本周日傍晚在中央花园广场举办百家宴活动，诚邀广大业主带上自家拿手菜，共叙邻里情，现场更有精美节目及多轮幸运抽奖！' },
  { id: 3, title: '关于小区地下车库充电桩升级扩建施工的公示', type: 'warning', time: '1天前', summary: '为满足新能源车主日益增长的充电需求，物业拟对地下一层B1/B2区新增50个公用快速充电桩，施工工期预计15天，期间部分行车道将受阻，请过往车主减速慢行。' }
])

const myRepairs = ref([
  { id: 'WX20260602001', title: '客厅空调启动后不制冷，伴有异响', category: '家电维修', status: '1', time: '今天 10:15', worker: '张建国师傅 (已接单)' },
  { id: 'WX20260528023', title: '厨房洗手盆底部软管开裂漏水', category: '管道疏通', status: '2', time: '05-28 14:30', worker: '刘向东师傅 (已修完毕)' }
])

const feedbackText = ref('')
function submitFeedback() {
  if (!feedbackText.value.trim()) {
    ElMessage.warning('请输入您的建议后再进行提交！')
    return
  }
  ElMessage.success('您的反馈已成功提交至物业经理信箱，感谢您参与共建和谐社区！')
  feedbackText.value = ''
}

// ==================== 3. 维修工人 (Worker) 逻辑与真实数据 ====================
const workerStats = reactive({
  todoJobs: 0,
  doingJobs: 0,
  completedJobs: 0,
  rating: 5.0
})

const workerJobs = ref([])

const workerFeedbacks = ref([
  { id: 1, owner: '陈大爷', room: 'A区1栋302室', content: '刘师傅干活真是太利索了！不仅快速修好了漏水的水龙头，还顺手帮我把旁边生锈的置物架螺丝也加固了，态度也特别客气，真心点赞！', rating: 5, date: '昨天' },
  { id: 2, owner: '赵女士', room: 'C区3栋1201室', content: '进门非常自觉戴上了鞋套，服务态度极好。排查电路故障非常有经验，二十分钟换好开关就好了，非常专业放心！', rating: 5, date: '05-30' }
])

async function loadWorkerData() {
  try {
    const res = await getWorkerRepairs({})
    if (res.code === 200 && res.rows) {
      const rows = res.rows
      workerStats.todoJobs = rows.filter(r => r.repairStatus === '0').length
      workerStats.doingJobs = rows.filter(r => r.repairStatus === '1').length
      workerStats.completedJobs = rows.filter(r => r.repairStatus === '2' || r.repairStatus === '3').length
      
      const activeJobs = rows.filter(r => r.repairStatus === '0' || r.repairStatus === '1')
      workerJobs.value = activeJobs.map(item => {
        return {
          id: 'WX' + item.repairId,
          realId: item.repairId,
          title: item.repairTitle,
          address: item.roomName || '小区公共区域',
          owner: item.ownerName || '物业提报',
          phone: item.phone || '-',
          time: formatTime(item.createTime),
          priority: item.repairStatus === '0' ? 'high' : 'medium',
          desc: item.repairContent,
          status: item.repairStatus
        }
      })
    }
  } catch (e) {
    console.error('获取维修工数据失败:', e)
  }
}

async function handleJob(realId, type) {
  try {
    if (type === 'accept') {
      const res = await acceptWorkerRepair(realId)
      if(res.code === 200) {
        ElMessage.success(`接单成功！已转入正在处理状态，请及时上门。`)
        loadWorkerData()
      } else {
        ElMessage.error(res.msg || '接单失败')
      }
    } else {
      const res = await finishWorkerRepair(realId)
      if(res.code === 200) {
        ElMessage.success(`报修工单已提报【维修完毕】！已进入业主评价阶段。`)
        loadWorkerData()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    }
  } catch(e) {
    ElMessage.error('网络请求失败')
  }
}

function showClockIn() {
  ElMessage.success('考勤打卡成功！今日打卡时间：' + new Date().toLocaleTimeString() + '，GPS定位：盛世嘉园小区，状态：在岗。')
}

// 业主自助预约访客 Dialog 数据与校验
const visitorOpen = ref(false)
const visitorFormRef = ref(null)
const visitorForm = ref({
  visitorName: '',
  visitorPhone: '',
  visitTime: '',
  visitReason: '亲戚探访'
})

const visitorRules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  visitorPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  visitTime: [{ required: true, message: '请选择预约来访时间', trigger: 'change' }]
}

function openVisitorDialog() {
  visitorForm.value = {
    visitorName: '',
    visitorPhone: '',
    visitTime: '',
    visitReason: '亲戚探访'
  }
  visitorOpen.value = true
}

async function submitVisitor() {
  visitorFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (hasBinding.value) {
          // 如果当前是已绑定的真实用户，则调用业主自助门禁接口
          const res = await request({
            url: '/property/portal/visitor',
            method: 'post',
            data: {
              visitorName: visitorForm.value.visitorName,
              visitorPhone: visitorForm.value.visitorPhone,
              visitTime: visitorForm.value.visitTime,
              visitReason: visitorForm.value.visitReason,
              visitStatus: '0' // 待审核
            }
          })
          if (res.code === 200) {
            ElMessage.success('您的访客预约登记成功！请通知访客在规定时间到达小区大门，物业将予以放行。')
            ownerStats.visitorPasses++
            visitorOpen.value = false
          } else {
            ElMessage.error(res.msg || '提交预约失败，请稍后重试')
          }
        } else {
          // 未绑定的演示模式，直接纯前端模拟交互，以提供极速且高保真的演示效果！
          ElMessage.success('【演示模式】您的访客预约登记成功！物业端访客管理列表已实时生成对应的待放行审核申请！')
          ownerStats.visitorPasses++
          visitorOpen.value = false
        }
      } catch (e) {
        console.error('Submit visitor error:', e)
        ElMessage.error('预约请求发送失败，请确保后端 Spring Boot 项目已启动')
      }
    }
  })
}

// 房产模拟对应库，与后台业主ID一一对应
const houseOptions = ref([
  { ownerId: 100, community: '盛世嘉园', building: 'A区3栋', room: '1602室', ownerName: '张建国' },
  { ownerId: 101, community: '盛世嘉园', building: 'A区2栋', room: '401室', ownerName: '李向东' },
  { ownerId: 102, community: '盛世嘉园', building: 'B区5栋', room: '803室', ownerName: '王桂兰' },
  { ownerId: 103, community: '盛世嘉园', building: 'C区1栋', room: '302室', ownerName: '陈大爷' },
  { ownerId: 104, community: '盛世嘉园', building: 'C区3栋', room: '1201室', ownerName: '赵美华' },
  { ownerId: 105, community: '半岛阳光', building: '1栋一单元', room: '501室', ownerName: '徐磊' },
  { ownerId: 106, community: '半岛阳光', building: '3栋二单元', room: '903室', ownerName: '刘强' },
  { ownerId: 107, community: '金陵名雅苑', building: '8栋', room: '601室', ownerName: '周小川' },
  { ownerId: 108, community: '金陵名雅苑', building: '12栋', room: '1504室', ownerName: '马建华' }
])

async function checkBinding() {
  if (isAdmin.value || isWorker.value) return
  try {
    const res = await request({ url: '/property/userOwner/check', method: 'get' })
    if (res.code === 200 && res.data) {
      const binding = res.data
      hasBinding.value = true
      ownerRealName.value = binding.ownerName || ''
      // 匹配模拟数据中的房产名称
      const match = houseOptions.value.find(h => h.ownerId === binding.ownerId)
      if (match) {
        ownerStats.roomName = `${match.community} ${match.building} ${match.room}`
      } else {
        ownerStats.roomName = binding.ownerName ? `已绑定业主：${binding.ownerName}` : '已绑定房产'
      }
    } else {
      hasBinding.value = false
    }
  } catch (e) {
    console.warn('获取业主房屋绑定信息失败，使用默认演示数据:', e)
    hasBinding.value = false
  }
}

async function loadOwnerNotices() {
  try {
    const res = await request({ url: '/property/portal/myNotice', method: 'get', params: { pageNum: 1, pageSize: 3 } })
    if (res.code === 200 && res.rows && res.rows.length > 0) {
      communityNotices.value = res.rows.map(item => {
        return {
          id: item.noticeId,
          title: item.noticeTitle,
          type: item.noticeType === '2' ? 'danger' : item.noticeType === '1' ? 'warning' : 'success',
          time: formatTime(item.publishTime || item.createTime),
          summary: item.noticeContent
        }
      })
    }
  } catch (e) {
    console.warn('获取最新小区公告失败，使用默认演示数据:', e)
  }
}

async function loadOwnerRepairs() {
  try {
    const res = await request({ url: '/property/portal/myRepair', method: 'get', params: { pageNum: 1, pageSize: 5 } })
    if (res.code === 200 && res.rows && res.rows.length > 0) {
      myRepairs.value = res.rows.map(item => {
        return {
          id: 'WX' + item.repairId,
          title: item.repairTitle,
          category: '日常报修',
          status: item.repairStatus,
          time: formatTime(item.createTime),
          worker: item.workerName ? `${item.workerName}师傅` : '待指派'
        }
      })
    }
  } catch (e) {
    console.warn('获取业主报修进度失败，使用默认演示数据:', e)
  }
}

// 生命周期挂载
onMounted(() => {
  if (isAdmin.value) {
    loadAdminData()
    window.addEventListener('resize', handleResize)
  } else if (isWorker.value) {
    loadWorkerData()
  } else {
    checkBinding()
    loadOwnerNotices()
    loadOwnerRepairs()
  }
})

onBeforeUnmount(() => {
  if (isAdmin.value) {
    window.removeEventListener('resize', handleResize)
    repairChart && repairChart.dispose()
    feeChart && feeChart.dispose()
  }
})
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700&display=swap');

.dashboard-container {
  padding: 20px 24px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f3ff 0%, #eef2ff 50%, #f0fdf4 100%);
  font-family: 'Outfit', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

/* ===== 提示新用户的温馨通知栏 ===== */
.guest-alert-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.85);
  border-left: 5px solid #7a22ff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(122, 34, 255, 0.08);
  backdrop-filter: blur(10px);
  margin-bottom: 20px;
  animation-duration: 0.8s;

  .alert-content {
    display: flex;
    align-items: center;
    gap: 12px;

    .alert-sparkle {
      font-size: 18px;
    }
    .alert-text {
      font-size: 14px;
      color: #4b5563;
      line-height: 1.5;

      strong {
        color: #7a22ff;
      }
    }
  }

  .alert-btn {
    background: linear-gradient(135deg, #7a22ff, #5b2dff);
    border: none;
    border-radius: 8px;
    padding: 8px 16px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      opacity: 0.9;
      transform: translateY(-1px);
    }
  }
}

/* ===== 欢迎横幅 ===== */
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 36px;
  margin-bottom: 24px;
  border-radius: 20px;
  background: linear-gradient(135deg, #7a22ff 0%, #5b2dff 40%, #3b82f6 100%);
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(122, 34, 255, 0.25);

  &.owner-banner {
    background: linear-gradient(135deg, #10b981 0%, #059669 40%, #2563eb 100%);
    box-shadow: 0 8px 32px rgba(16, 185, 129, 0.2);
  }

  &.worker-banner {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 40%, #a855f7 100%);
    box-shadow: 0 8px 32px rgba(37, 99, 235, 0.2);
  }

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    right: -10%;
    width: 400px;
    height: 400px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.12) 0%, transparent 70%);
    pointer-events: none;
  }
  &::after {
    content: '';
    position: absolute;
    bottom: -30%;
    left: 20%;
    width: 250px;
    height: 250px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(0, 229, 255, 0.15) 0%, transparent 70%);
    pointer-events: none;
  }
}

.welcome-left {
  position: relative;
  z-index: 1;
}

.welcome-title {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  margin: 0 0 6px 0;
  letter-spacing: 0.5px;
}

.greeting-icon {
  margin-right: 4px;
}

.welcome-date {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  margin: 0;

  strong {
    color: #fff;
    background: rgba(255, 255, 255, 0.15);
    padding: 2px 8px;
    border-radius: 6px;
    margin-left: 2px;
  }
}

.welcome-actions {
  display: flex;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.action-btn {
  background: rgba(255, 255, 255, 0.18) !important;
  border: 1px solid rgba(255, 255, 255, 0.25) !important;
  color: #fff !important;
  border-radius: 12px !important;
  padding: 10px 20px !important;
  font-size: 14px !important;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;

  &:hover {
    background: rgba(255, 255, 255, 0.3) !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

/* ===== KPI 卡片 ===== */
.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.kpi-card {
  position: relative;
  display: flex;
  align-items: center;
  padding: 24px 28px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 36px rgba(0, 0, 0, 0.1);
  }
}

.kpi-decoration {
  position: absolute;
  top: -30px;
  right: -30px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  opacity: 0.08;
}

.kpi-purple {
  .kpi-icon-wrap { background: linear-gradient(135deg, #7a22ff, #a855f7); }
  .kpi-decoration { background: #7a22ff; }
}
.kpi-teal {
  .kpi-icon-wrap { background: linear-gradient(135deg, #14b8a6, #2dd4bf); }
  .kpi-decoration { background: #14b8a6; }
}
.kpi-amber {
  .kpi-icon-wrap { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
  .kpi-decoration { background: #f59e0b; }
}
.kpi-rose {
  .kpi-icon-wrap { background: linear-gradient(135deg, #f43f5e, #fb7185); }
  .kpi-decoration { background: #f43f5e; }
}

.kpi-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 26px;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.kpi-info {
  margin-left: 18px;
  display: flex;
  flex-direction: column;
}

.kpi-value {
  font-size: 30px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.kpi-label {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 4px;
  font-weight: 500;
}

/* ===== 图表与两栏通用结构 ===== */
.chart-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}
.dot-blue { background: linear-gradient(135deg, #7a22ff, #3b82f6); }
.dot-green { background: linear-gradient(135deg, #22c55e, #14b8a6); }
.dot-orange { background: linear-gradient(135deg, #f59e0b, #f43f5e); }
.dot-purple { background: linear-gradient(135deg, #8b5cf6, #7a22ff); }

.chart-body {
  width: 100%;
  height: 320px;
  padding: 10px 14px 14px;
}

.view-all-btn {
  font-size: 13px !important;
  font-weight: 500;
}

/* ===== 社区公告栏 ===== */
.notice-board {
  .notice-list {
    padding: 15px 24px 24px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    height: 320px;
    overflow-y: auto;
  }

  .notice-item {
    padding: 16px 20px;
    background: #f8fafc;
    border-radius: 14px;
    transition: all 0.3s ease;
    border-left: 4px solid transparent;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
      border-left-color: #3b82f6;
      background: #f1f5f9;
    }
  }

  .notice-title-row {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 8px;

    .notice-tag {
      font-size: 11px;
      font-weight: 600;
      border-radius: 6px;
    }

    .notice-title-text {
      font-size: 15px;
      font-weight: 600;
      color: #1e293b;
      margin: 0;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .notice-time {
      font-size: 12px;
      color: #94a3b8;
    }
  }

  .notice-summary {
    font-size: 13px;
    color: #64748b;
    margin: 0;
    line-height: 1.5;
  }
}

/* ===== 24小时管家服务 / 生活助手 ===== */
.assistant-card {
  .assistant-body {
    padding: 20px 24px;
    height: 320px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .weather-box {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    border-radius: 14px;
    background: linear-gradient(135deg, #e0f2fe, #bae6fd);
    color: #0369a1;

    .weather-temp {
      font-size: 32px;
      font-weight: 700;
      display: block;
      line-height: 1;
    }

    .weather-text {
      font-size: 13px;
      font-weight: 600;
      margin-top: 4px;
      display: block;
    }

    .weather-right {
      text-align: right;
      font-size: 12px;
      line-height: 1.6;
    }
  }

  .service-contacts {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin: 14px 0;

    .contact-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;

      .contact-label {
        color: #64748b;
        font-weight: 500;
      }

      .contact-value {
        color: #1e293b;
        font-weight: 600;
      }
    }
  }

  .service-tips {
    display: flex;
    gap: 10px;
    padding: 10px 14px;
    background: #fffbeb;
    border: 1px solid #fef3c7;
    border-radius: 10px;

    .tip-icon {
      font-size: 16px;
    }

    .tip-text {
      font-size: 12px;
      color: #b45309;
      margin: 0;
      line-height: 1.4;
    }
  }
}

/* ===== 工人安全卡片风格 ===== */
.worker-tip-card {
  padding: 16px 20px;
  border-radius: 14px;
  margin-bottom: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);

  h4 {
    margin: 0 0 8px 0;
    font-size: 14px;
    font-weight: 600;
  }

  p {
    margin: 0;
    font-size: 13px;
    line-height: 1.5;
  }

  &.red-warning {
    background: #fef2f2;
    border: 1px solid #fee2e2;
    h4 { color: #ef4444; }
    p { color: #b91c1c; }
  }

  &.info-card {
    background: #f0fdfa;
    border: 1px solid #ccfbf1;
    h4 { color: #0d9488; }
    p { color: #0f766e; }
  }
}

/* ===== 列表与底部通用结构 ===== */
.list-row {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 20px;
  margin-top: 24px;
}

.list-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.table-wrap {
  padding: 12px 24px 20px;
}

:deep(.el-table) {
  --el-table-border-color: #f1f5f9;
  border-radius: 12px;
  overflow: hidden;
}
:deep(.el-table th.el-table__cell) {
  font-size: 13px;
}
:deep(.el-table td.el-table__cell) {
  font-size: 13px;
  padding: 10px 0;
}

/* ===== 业主快捷反馈卡片 ===== */
.feedback-card {
  .feedback-body {
    padding: 20px 24px 24px;
    display: flex;
    flex-direction: column;
    gap: 14px;
  }

  .feedback-intro {
    font-size: 13px;
    color: #64748b;
    line-height: 1.5;
    margin: 0;
  }

  .feedback-input {
    :deep(.el-textarea__inner) {
      border-radius: 12px;
      background-color: #f8fafc;
      padding: 12px;
      font-size: 13px;
      border-color: #e2e8f0;

      &:focus {
        background-color: #fff;
        border-color: #7a22ff;
      }
    }
  }

  .feedback-submit-btn {
    background: linear-gradient(135deg, #7a22ff, #5b2dff);
    border: none;
    border-radius: 10px;
    font-weight: 500;
    padding: 12px 0;
    transition: all 0.3s ease;

    &:hover {
      opacity: 0.95;
      transform: translateY(-1px);
    }
  }
}

/* ===== 待办工单与工人专属样式 ===== */
.worker-job-list {
  padding: 15px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 320px;
  overflow-y: auto;

  .job-item-card {
    background: #f8fafc;
    border-radius: 14px;
    padding: 18px 20px;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
      border-color: #3b82f6;
      background: #fff;
    }
  }

  .job-item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .job-meta {
      display: flex;
      align-items: center;
      gap: 12px;

      .job-id {
        font-size: 13px;
        font-weight: 700;
        color: #3b82f6;
        background: rgba(59, 130, 246, 0.1);
        padding: 2px 8px;
        border-radius: 6px;
      }

      .job-time {
        font-size: 12px;
        color: #94a3b8;
      }
    }
  }

  .job-item-body {
    .job-title {
      font-size: 16px;
      font-weight: 600;
      color: #1e293b;
      margin: 0 0 10px 0;
    }

    .job-info-row {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      margin-bottom: 8px;

      p {
        font-size: 13px;
        color: #475569;
        margin: 0;
      }
    }

    .job-desc {
      font-size: 13px;
      color: #64748b;
      margin: 0;
      background: #f1f5f9;
      padding: 8px 12px;
      border-radius: 8px;
      line-height: 1.4;
    }
  }

  .job-item-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 14px;

    .job-action-btn {
      border-radius: 8px;
      font-size: 13px;
      padding: 8px 16px;
    }
  }
}

/* ===== 工人接收到的最新业主评价 ===== */
.feedback-list {
  padding: 15px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;

  .feedback-item {
    padding: 16px 18px;
    background: #f8fafc;
    border-radius: 12px;
    border-left: 4px solid #14b8a6;

    .feedback-item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .fb-owner-name {
        font-weight: 600;
        font-size: 14px;
        color: #1e293b;
      }

      .fb-owner-room {
        font-size: 12px;
        color: #64748b;
        margin-left: 4px;
      }

      .fb-rating {
        display: flex;
        align-items: center;
        gap: 2px;

        .star {
          color: #fbbf24;
          font-size: 14px;
        }

        .fb-date {
          font-size: 12px;
          color: #94a3b8;
          margin-left: 8px;
        }
      }
    }

    .fb-content {
      margin: 0;
      font-size: 13px;
      color: #475569;
      line-height: 1.5;
      font-style: italic;
    }
  }
}

/* ===== 待办事项 ===== */
.todo-list {
  padding: 8px 20px 20px;
}

.todo-item {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 6px;

  &:hover {
    background: #f8fafc;
    transform: translateX(4px);
  }
  &:last-child {
    margin-bottom: 0;
  }
}

.todo-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.todo-icon-red { background: #fef2f2; color: #ef4444; }
.todo-icon-blue { background: #eff6ff; color: #3b82f6; }
.todo-icon-orange { background: #fff7ed; color: #f97316; }
.todo-icon-teal { background: #f0fdfa; color: #14b8a6; }
.todo-icon-amber { background: #fffbeb; color: #f59e0b; }

.todo-info {
  margin-left: 14px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.todo-label {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.todo-desc {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.todo-count {
  font-size: 20px;
  font-weight: 700;
  min-width: 32px;
  text-align: center;
}

.todo-count-red { color: #ef4444; }
.todo-count-blue { color: #3b82f6; }
.todo-count-orange { color: #f97316; }
.todo-count-teal { color: #14b8a6; }
.todo-count-amber { color: #f59e0b; }

/* ===== 响应式布局 ===== */
@media (max-width: 1200px) {
  .kpi-row {
    grid-template-columns: repeat(2, 1fr);
  }
  .chart-row,
  .list-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }
  .welcome-banner {
    flex-direction: column;
    align-items: flex-start;
    padding: 20px;
    gap: 16px;
  }
  .welcome-actions {
    flex-wrap: wrap;
  }
  .kpi-row {
    grid-template-columns: 1fr;
  }
  .kpi-card {
    padding: 18px 22px;
  }
}
</style>
