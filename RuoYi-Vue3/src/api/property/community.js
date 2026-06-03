import request from '@/utils/request'

// 查询小区列表
export function listCommunity(query) {
  return request({
    url: '/property/community/list',
    method: 'get',
    params: query
  })
}

// 查询小区详细
export function getCommunity(communityId) {
  return request({
    url: '/property/community/' + communityId,
    method: 'get'
  })
}

// 新增小区
export function addCommunity(data) {
  return request({
    url: '/property/community',
    method: 'post',
    data: data
  })
}

// 修改小区
export function updateCommunity(data) {
  return request({
    url: '/property/community',
    method: 'put',
    data: data
  })
}

// 删除小区
export function delCommunity(communityId) {
  return request({
    url: '/property/community/' + communityId,
    method: 'delete'
  })
}

// 获取全部小区列表（用于下拉选择）
export function listAllCommunity() {
  return request({ url: '/property/community/listAll', method: 'get' })
}
