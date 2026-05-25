import request from '@/axios'

export const saveInventoryFlowApi = (data: any) => {
  return request.post({ url: '/inventory/save', data })
}
export const getInventoryFlowListApi = (data: any) => {
  return request.post({ url: '/inventory/list', data })
}
