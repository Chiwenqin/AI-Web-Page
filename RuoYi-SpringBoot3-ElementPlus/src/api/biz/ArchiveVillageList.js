import request from '@/utils/request'

// 查询小区列列表
export function listArchiveVillageList(query) {
  return request({
    url: '/biz/ArchiveVillageList/list',
    method: 'get',
    params: query
  })
}

// 查询小区列详细
export function getArchiveVillageList(id) {
  return request({
    url: '/biz/ArchiveVillageList/' + id,
    method: 'get'
  })
}

// 新增小区列
export function addArchiveVillageList(data) {
  return request({
    url: '/biz/ArchiveVillageList',
    method: 'post',
    data: data
  })
}

// 修改小区列
export function updateArchiveVillageList(data) {
  return request({
    url: '/biz/ArchiveVillageList',
    method: 'put',
    data: data
  })
}

// 删除小区列
export function delArchiveVillageList(id) {
  return request({
    url: '/biz/ArchiveVillageList/' + id,
    method: 'delete'
  })
}
