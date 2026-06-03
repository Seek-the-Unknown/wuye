import request from '@/utils/request'

export function listFeeType(query) {
  return request({ url: '/property/feeType/list', method: 'get', params: query })
}
export function listAllFeeType() {
  return request({ url: '/property/feeType/listAll', method: 'get' })
}
export function getFeeType(feeTypeId) {
  return request({ url: '/property/feeType/' + feeTypeId, method: 'get' })
}
export function addFeeType(data) {
  return request({ url: '/property/feeType', method: 'post', data })
}
export function updateFeeType(data) {
  return request({ url: '/property/feeType', method: 'put', data })
}
export function delFeeType(feeTypeId) {
  return request({ url: '/property/feeType/' + feeTypeId, method: 'delete' })
}
