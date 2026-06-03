import request from '@/utils/request'

export function listNotice(query) {
  return request({ url: '/property/notice/list', method: 'get', params: query })
}
export function getNotice(noticeId) {
  return request({ url: '/property/notice/' + noticeId, method: 'get' })
}
export function addNotice(data) {
  return request({ url: '/property/notice', method: 'post', data })
}
export function updateNotice(data) {
  return request({ url: '/property/notice', method: 'put', data })
}
export function delNotice(noticeId) {
  return request({ url: '/property/notice/' + noticeId, method: 'delete' })
}
