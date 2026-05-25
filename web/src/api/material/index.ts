import request from '@/axios'

export const saveMaterialApi = (data: any) => {
  return request.post({ url: '/material/save', data })
}
export const delMaterialApi = (data: number[]) => {
  return request.post({ url: '/material/del', data })
}
export const getMaterialListApi = (data: any) => {
  return request.post({ url: '/material/list', data })
}
