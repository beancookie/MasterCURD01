import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/user/list',
    method: 'get',
    params: query
  })
}

export function listAddress(query) {
  return request({
    url: '/address/list',
    method: 'get',
    params: query
  })
}

export function listCollect(query) {
  return request({
    url: '/collect/list',
    method: 'get',
    params: query
  })
}

export function listFeedback(query) {
  return request({
    url: '/feedback/list',
    method: 'get',
    params: query
  })
}

export function listFootprint(query) {
  return request({
    url: '/footprint/list',
    method: 'get',
    params: query
  })
}

export function listHistory(query) {
  return request({
    url: '/history/list',
    method: 'get',
    params: query
  })
}
export function addById(query) {
  return request({
    url: '/user/integral',
    method: 'put',
    params: query
  })
}

export function getType() {
  return request({
    url: '/user/integralAndType',
    method: 'get'
  })
}

export function chanSign(query) {
  return request({
    url: '/user/integralConfig',
    method: 'put',
    params: query
  })
}
