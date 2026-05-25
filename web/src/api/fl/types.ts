export interface flInfo {
  outerDiameter: number
  rootDiameter: number
  mouthDiameter?: number
  innerDiameter?: number
  thickness?: number
  height?: number
}
export interface flTypeInfo {
  name: string
}
// 外径D   outerDiameter
// 根径N   rootDiameter
// 咀径A   mouthDiameter
// 内径n   innerDiameter
// 片厚C   thickness
// 总高H   height
export interface flListResponse {
  list: flInfo[]
}
export interface flTypeListResponse {
  code: number
  data: flTypeInfo[] // 真正的数组在这里
  message?: string
}
export interface DepartmentUserItem {
  id: string
  username: string
  account: string
  email: string
  createTime: string
  role: string
  department: flInfo
}
