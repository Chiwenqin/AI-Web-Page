import request from '@/utils/request'

// 查询楼门档案列表
export function listEntrance(query) {
  return request({
    url: '/biz/entrance/list',
    method: 'get',
    params: query
  })
}

// 查询楼门档案详细
export function getEntrance(id) {
  return request({
    url: '/biz/entrance/' + id,
    method: 'get'
  })
}

// 新增楼门档案
export function addEntrance(data) {
  return request({
    url: '/biz/entrance',
    method: 'post',
    data: data
  })
}

// 修改楼门档案
export function updateEntrance(data) {
  return request({
    url: '/biz/entrance',
    method: 'put',
    data: data
  })
}

// 删除楼门档案
export function delEntrance(id) {
  return request({
    url: '/biz/entrance/' + id,
    method: 'delete'
  })
}

// 批量删除楼门档案
export function batchDelEntrance(ids) {
  return request({
    url: '/biz/entrance/batch',
    method: 'delete',
    data: ids
  })
}
