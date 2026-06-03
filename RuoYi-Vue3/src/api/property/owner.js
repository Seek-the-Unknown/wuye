import request from '@/utils/request'

// 查询业主列表
export function listOwner(query) {
  return request({
    url: '/property/owner/list',
    method: 'get',
    params: query
  })
}

export function listAllOwner(query) {
  return request({
    url: '/property/owner/listAll',
    method: 'get',
    params: query
  })
}

// 查询业主详细
export function getOwner(ownerId) {
  return request({
    url: '/property/owner/' + ownerId,
    method: 'get'
  })
}

// 新增业主
export function addOwner(data) {
  return request({
    url: '/property/owner',
    method: 'post',
    data: data
  })
}

// 修改业主
export function updateOwner(data) {
  return request({
    url: '/property/owner',
    method: 'put',
    data: data
  })
}

// 删除业主
export function delOwner(ownerId) {
  return request({
    url: '/property/owner/' + ownerId,
    method: 'delete'
  })
}
