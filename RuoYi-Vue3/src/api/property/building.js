import request from '@/utils/request'

export function listBuilding(query) {
  return request({ url: '/property/building/list', method: 'get', params: query })
}
export function listAllBuilding(query) {
  return request({ url: '/property/building/listAll', method: 'get', params: query })
}
export function getBuilding(buildingId) {
  return request({ url: '/property/building/' + buildingId, method: 'get' })
}
export function addBuilding(data) {
  return request({ url: '/property/building', method: 'post', data: data })
}
export function updateBuilding(data) {
  return request({ url: '/property/building', method: 'put', data: data })
}
export function delBuilding(buildingId) {
  return request({ url: '/property/building/' + buildingId, method: 'delete' })
}

