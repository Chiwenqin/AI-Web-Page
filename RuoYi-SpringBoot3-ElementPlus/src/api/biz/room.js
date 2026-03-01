import request from '@/utils/request'

// 查询房间档案列表
export function listRoom(query) {
  return request({
    url: '/biz/room/list',
    method: 'get',
    params: query
  })
}

// 查询房间档案详细
export function getRoom(id) {
  return request({
    url: '/biz/room/' + id,
    method: 'get'
  })
}

// 新增房间档案
export function addRoom(data) {
  return request({
    url: '/biz/room',
    method: 'post',
    data: data
  })
}

// 修改房间档案
export function updateRoom(data) {
  return request({
    url: '/biz/room',
    method: 'put',
    data: data
  })
}

// 删除房间档案
export function delRoom(id) {
  return request({
    url: '/biz/room/' + id,
    method: 'delete'
  })
}

// 批量删除房间档案
export function batchDelRoom(ids) {
  return request({
    url: '/biz/room/batch',
    method: 'delete',
    data: ids
  })
}