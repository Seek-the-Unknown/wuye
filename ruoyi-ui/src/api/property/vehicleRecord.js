import request from '@/utils/request'

// 查询车辆进出记录列表
export function listVehicleRecord(query) {
  return request({
    url: '/property/vehicleRecord/list',
    method: 'get',
    params: query
  })
}

// 查询记录详情
export function getVehicleRecord(recordId) {
  return request({
    url: '/property/vehicleRecord/' + recordId,
    method: 'get'
  })
}

// 新增记录（手动）
export function addVehicleRecord(data) {
  return request({
    url: '/property/vehicleRecord',
    method: 'post',
    data: data
  })
}

// 修改记录
export function updateVehicleRecord(data) {
  return request({
    url: '/property/vehicleRecord',
    method: 'put',
    data: data
  })
}

// 删除记录
export function delVehicleRecord(recordIds) {
  return request({
    url: '/property/vehicleRecord/' + recordIds,
    method: 'delete'
  })
}

// 车辆入场 - 上传图片识别车牌
export function vehicleEnter(data) {
  return request({
    url: '/property/vehicleRecord/enter',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 车辆出场 - 上传图片识别车牌 + 计费
export function vehicleExit(data) {
  return request({
    url: '/property/vehicleRecord/exit',
    method: 'post',
    data: data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 获取停车计费单价
export function getHourlyRate() {
  return request({
    url: '/property/vehicleRecord/getHourlyRate',
    method: 'get'
  })
}

// 按车牌号查找活动入场记录
export function searchActiveRecord(plateNumber) {
  return request({
    url: '/property/vehicleRecord/searchActive',
    method: 'get',
    params: { plateNumber }
  })
}
