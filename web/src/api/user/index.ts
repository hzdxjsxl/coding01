import request from '@/axios'

export const saveUserApi = (data: any) => {
  return request.post({ url: '/user/save', data })
}
export const updateUserApi = (data: any) => {
  return request.post({ url: '/user/update', data })
}
export const delUserApi = (data: number[]) => {
  return request.post({ url: '/user/del', data })
}
export const getListApi = (data: any) => {
  return request.post({ url: '/user/list', data })
}
