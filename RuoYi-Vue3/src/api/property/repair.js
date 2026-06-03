import request from '@/utils/request'

export function listRepair(query) {
  return request({ url: '/property/repair/list', method: 'get', params: query })
}
export function getRepair(repairId) {
  return request({ url: '/property/repair/' + repairId, method: 'get' })
}
export function addRepair(data) {
  return request({ url: '/property/repair', method: 'post', data: data })
}
export function updateRepair(data) {
  return request({ url: '/property/repair', method: 'put', data: data })
}
export function delRepair(repairId) {
  return request({ url: '/property/repair/' + repairId, method: 'delete' })
}
