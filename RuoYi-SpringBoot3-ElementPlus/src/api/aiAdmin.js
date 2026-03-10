import request from '@/utils/request'

// AI Admin 登录方法
export function aiAdminLogin(username, password) {
  const data = {
    username,
    password
  }
  return request({
    url: '/auth/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// AI Admin 退出方法
export function aiAdminLogout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
