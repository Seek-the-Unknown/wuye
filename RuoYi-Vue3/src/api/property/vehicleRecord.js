import request from '@/utils/request'

export function listVehicleRecord(query) {
  return request({ url: '/property/vehicleRecord/list', method: 'get', params: query })
}
export function getVehicleRecord(recordId) {
  return request({ url: '/property/vehicleRecord/' + recordId, method: 'get' })
}
export function addVehicleRecord(data) {
  return request({ url: '/property/vehicleRecord', method: 'post', data })
}
export function updateVehicleRecord(data) {
  return request({ url: '/property/vehicleRecord', method: 'put', data })
}
export function delVehicleRecord(recordIds) {
  return request({ url: '/property/vehicleRecord/' + recordIds, method: 'delete' })
}
export function vehicleEnter(data) {
  return request({
    url: '/property/vehicleRecord/enter',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export function vehicleExit(data) {
  return request({
    url: '/property/vehicleRecord/exit',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
export function getHourlyRate() {
  return request({ url: '/property/vehicleRecord/getHourlyRate', method: 'get' })
}
