import request from '@/axios'

// 获取所有字典
export const getDictApi = () => {
  return request.get({ url: '/mock/dict/list' })
}

// 模拟获取某个字典
export const getDictOneApi = async () => {
  return request.get({ url: '/mock/dict/one' })
}

export const commonUploadApi = (data: FormData) => {
  // 方式 B：如果没有 upload 方法，直接用 post，但必须带上 multipart/form-data 头
  return request.post({
    url: '/common/upload', // 这里的 /api 是触发代理的前缀，结合你实际的配
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
