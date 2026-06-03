import request from '@/utils/request'

export function listFeeRecord(query) {
  return request({ url: '/property/feeRecord/list', method: 'get', params: query })
}
export function getFeeRecord(recordId) {
  return request({ url: '/property/feeRecord/' + recordId, method: 'get' })
}
export function addFeeRecord(data) {
  return request({ url: '/property/feeRecord', method: 'post', data })
}
export function updateFeeRecord(data) {
  return request({ url: '/property/feeRecord', method: 'put', data })
}
export function delFeeRecord(recordId) {
  return request({ url: '/property/feeRecord/' + recordId, method: 'delete' })
}
