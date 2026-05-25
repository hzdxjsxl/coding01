import request from '@/axios'

export const saveCustomerApi = (data: any) => {
  return request.post({ url: '/customer/save', data })
}
export const delCustomerApi = (data: number[]) => {
  return request.post({ url: '/customer/del', data })
}
export const getCustomerListApi = (data: any) => {
  return request.post({ url: '/customer/list', data })
}
