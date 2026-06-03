import request from '@/utils/request'

export function listVisitor(query) {
  return request({ url: '/property/visitor/list', method: 'get', params: query })
}
export function getVisitor(visitorId) {
  return request({ url: '/property/visitor/' + visitorId, method: 'get' })
}
export function addVisitor(data) {
  return request({ url: '/property/visitor', method: 'post', data })
}
export function updateVisitor(data) {
  return request({ url: '/property/visitor', method: 'put', data })
}
export function delVisitor(visitorId) {
  return request({ url: '/property/visitor/' + visitorId, method: 'delete' })
}
