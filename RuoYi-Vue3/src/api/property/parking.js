import request from '@/utils/request'

export function listParking(query) {
  return request({ url: '/property/parking/list', method: 'get', params: query })
}
export function getParking(parkingId) {
  return request({ url: '/property/parking/' + parkingId, method: 'get' })
}
export function addParking(data) {
  return request({ url: '/property/parking', method: 'post', data })
}
export function updateParking(data) {
  return request({ url: '/property/parking', method: 'put', data })
}
export function delParking(parkingId) {
  return request({ url: '/property/parking/' + parkingId, method: 'delete' })
}
