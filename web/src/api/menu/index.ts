import request from '@/axios'

export const getMenuListApi = () => {
  return request.post({ url: '/sysMenu/list' })
}
export const delMenuApi = () => {
  return request.post({ url: '/sysMenu/del' })
}
export const submitMenuApi = (data: any) => {
  return request.post({ url: '/sysMenu/save', data })
}
export const oneMenuByIdApi = () => {
  return request.post({ url: '/sysMenu/oneById' })
}
