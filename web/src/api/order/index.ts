import request from '@/axios'

export const saveOrderApi = (data: any) => {
  return request.post({ url: '/order/save', data })
}
export const delOrderApi = (data: number[]) => {
  return request.post({ url: '/order/del', data })
}
export const getOrderListApi = (data: any) => {
  return request.post({ url: '/order/list', data })
}
export const updateStatusApi = (data: any) => {
  return request.post({ url: '/order/updateStatus', data })
}
export const exportOrderApi = (data: any) => {
  return request.post({ url: '/order/export', data })
}
