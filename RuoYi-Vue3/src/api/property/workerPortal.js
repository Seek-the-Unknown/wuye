import request from '@/utils/request'

// 获取维修工名下的报修工单列表
export function getWorkerRepairs(query) {
  return request({
    url: '/property/portal/worker/list',
    method: 'get',
    params: query
  })
}

// 接单
export function acceptWorkerRepair(repairId) {
  return request({
    url: '/property/portal/worker/accept/' + repairId,
    method: 'put'
  })
}

// 维修完成
export function finishWorkerRepair(repairId) {
  return request({
    url: '/property/portal/worker/finish/' + repairId,
    method: 'put'
  })
}
