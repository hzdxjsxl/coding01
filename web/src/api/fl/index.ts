import request from '@/axios'
import { flListResponse, flTypeListResponse } from './types'

export const getflList = () => {
  return request.get<flListResponse>({ url: '/mock/fl/list' })
}
export const getFlTypeList = () => {
  console.log('res', request.get<flTypeListResponse>({ url: '/mock/flType/list' }))
  return request.get<flTypeListResponse>({ url: '/mock/flType/list' })
}
