import request from '@/utils/request'

// 查询楼栋信息列表
export function listArchiveBuildingList(query) {
  return request({
    url: '/biz/ArchiveBuildingList/list',
    method: 'get',
    params: query
  })
}

// 查询楼栋信息详细
export function getArchiveBuildingList(id) {
  return request({
    url: '/biz/ArchiveBuildingList/' + id,
    method: 'get'
  })
}

// 新增楼栋信息
export function addArchiveBuildingList(data) {
  return request({
    url: '/biz/ArchiveBuildingList',
    method: 'post',
    data: data
  })
}

// 修改楼栋信息
export function updateArchiveBuildingList(data) {
  return request({
    url: '/biz/ArchiveBuildingList',
    method: 'put',
    data: data
  })
}

// 删除楼栋信息
export function delArchiveBuildingList(id) {
  return request({
    url: '/biz/ArchiveBuildingList/' + id,
    method: 'delete'
  })
}
