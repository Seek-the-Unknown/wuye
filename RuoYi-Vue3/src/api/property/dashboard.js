import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/property/dashboard/stats',
    method: 'get'
  })
}

// 获取月度报修趋势
export function getRepairTrend() {
  return request({
    url: '/property/dashboard/repairTrend',
    method: 'get'
  })
}

// 获取物业费收缴统计
export function getFeeCollection() {
  return request({
    url: '/property/dashboard/feeCollection',
    method: 'get'
  })
}

// 获取最近报修工单
export function getRecentRepairs() {
  return request({
    url: '/property/dashboard/recentRepairs',
    method: 'get'
  })
}

// 获取待办事项摘要
export function getTodoSummary() {
  return request({
    url: '/property/dashboard/todoSummary',
    method: 'get'
  })
}
