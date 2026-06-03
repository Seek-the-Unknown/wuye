import request from '@/utils/request'

export function listRoom(query) {
  return request({ url: '/property/room/list', method: 'get', params: query })
}
export function listAllRoom(query) {
  return request({ url: '/property/room/listAll', method: 'get', params: query })
}
export function getRoom(roomId) {
  return request({ url: '/property/room/' + roomId, method: 'get' })
}
export function addRoom(data) {
  return request({ url: '/property/room', method: 'post', data: data })
}
export function updateRoom(data) {
  return request({ url: '/property/room', method: 'put', data: data })
}
export function delRoom(roomId) {
  return request({ url: '/property/room/' + roomId, method: 'delete' })
}
