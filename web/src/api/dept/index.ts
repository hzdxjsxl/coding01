import request from '@/axios'

export const getDeptListPage = () => {
  return request.post({ url: '/sysDept/test' })
}
