import request from '@/utils/request'

// 查询投诉建议列表
export function listComplaint(query) {
  return request({
    url: '/property/complaint/list',
    method: 'get',
    params: query
  })
}

// 查询投诉建议详细
export function getComplaint(complaintId) {
  return request({
    url: '/property/complaint/' + complaintId,
    method: 'get'
  })
}

// 新增投诉建议
export function addComplaint(data) {
  return request({
    url: '/property/complaint',
    method: 'post',
    data: data
  })
}

// 修改投诉建议
export function updateComplaint(data) {
  return request({
    url: '/property/complaint',
    method: 'put',
    data: data
  })
}

// 删除投诉建议
export function delComplaint(complaintId) {
  return request({
    url: '/property/complaint/' + complaintId,
    method: 'delete'
  })
}
