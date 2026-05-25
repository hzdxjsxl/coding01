<template>
  <Form :schema="schema" @register="formRegister" />
  <Button @click="computeNum">计算</Button>
</template>

<script setup lang="tsx">
import { computed, onMounted, reactive, ref } from 'vue'
import { Form, FormSchema } from '@/components/Form'
import { getflList, getFlTypeList } from '@/api/fl'
import { useForm } from '@/hooks/web/useForm'
import Button from '@/components/Button/src/Button.vue'
import { flTypeInfo } from '@/api/fl/types'

const { formRegister, formMethods } = useForm()
console.log('useForm()', useForm())
const { setValues } = formMethods // 获取表单数据的方法

const formData = reactive({
  flType: '',
  sealingSurface: '',
  nominalPressure: '',
  nominalPipeSize: ''
})
//下拉选项
const optionType = [
  {
    label: 'WN 带颈对焊法兰',
    value: 'WN'
  }
]
const optionType1 = [
  {
    label: 'RF 突面',
    value: 'RF'
  }
]
const optionType2 = [
  {
    label: 'Class 150',
    value: '150LB'
  }
]
const optionType3 = [
  {
    label: '1/2" DN15',
    value: '15'
  }
]
const allFlanges = ref<flTypeInfo[]>([])
const searchQuery = ref('')
onMounted(async () => {
  const res = await getFlTypeList()
  allFlanges.value = res.data as unknown as flTypeInfo[]
})

const filteredOptions = computed(() => {
  const query = searchQuery.value.toLowerCase()
  return allFlanges.value
    .filter((item) => item.name.toLowerCase().includes(query))
    .map(({ name }) => ({ value: name }))
})
const createFilter = async (queryString: string) => {
  searchQuery.value = queryString
  return filteredOptions.value
}
let timeout: NodeJS.Timeout
const querySearchAsync = async (queryString: string, cb: (arg: any) => void) => {
  let results: any = []
  if (queryString) {
    results = await createFilter(queryString)
  }
  clearTimeout(timeout)
  timeout = setTimeout(() => {
    cb(results)
  }, 3000 * Math.random())
}
const handleSelect = (item: Recordable) => {
  formData.flType = item.value.substring(0, 2)
}
//表单设置reactive<FormSchema[]>([
const schema = reactive<FormSchema[]>([
  {
    field: 'flType',
    label: '法兰类型',
    component: 'Autocomplete',
    componentProps: {
      options: optionType,
      // 挪到这里！并且建议直接用 onChange
      onChange: (val) => {
        formData.flType = val
        setValues({ flType: val })
      },
      fetchSuggestions: querySearchAsync,
      on: {
        select: handleSelect
      }
    }
  },
  {
    field: 'sealingSurface',
    label: '密封面',
    component: 'Select',
    componentProps: {
      options: optionType1,
      onChange: (val) => {
        formData.sealingSurface = val
        setValues({ sealingSurface: val })
      }
    }
  },
  {
    field: 'nominalPressure',
    label: '公称压力',
    component: 'Select',
    componentProps: {
      options: optionType2,
      onChange: (val) => {
        formData.nominalPressure = val
        setValues({ nominalPressure: val })
      }
    }
  },
  {
    field: 'nominalPipeSize',
    label: '公称直径',
    component: 'Select',
    componentProps: {
      options: optionType3,
      onChange: (val) => {
        formData.nominalPipeSize = val
        setValues({ nominalPipeSize: val })
      }
    }
  }
])
/**
 * 计算 WN 法兰理论重量 (kg)
 * @param params 包含 D, N, A, n, C, H 的对象
 * @param density 密度，默认 7.85 (碳钢)
 */
function calculateWNWeight(params, density = 6.17) {
  //4规定（150LB WN RF 1/2"）
  //flType: 法兰类型
  //sealingSurface: 密封面
  //nominalPressure: 公称压力 150LB（磅）相当于  150class (级)
  //nominalPipeSize: 公称管径  1/2" 代表 DF为15
  //6参数
  // 外径D   outerDiameter
  // 根径N   rootDiameter
  // 咀径A   mouthDiameter
  // 内径n   innerDiameter
  // 片厚C   thickness
  // 总高H   height
  const {
    outerDiameter: D, // 外径
    rootDiameter: N, // 根径 (颈部大端)
    mouthDiameter: A, // 咀径 (颈部小端)
    innerDiameter: n, // 内径
    thickness: C, // 盘厚
    height: H // 总高
  } = params

  //几何半径单位为mm
  const R_out = D / 2 // 盘外半径
  const R_root = N / 2 // 颈部根半径
  const r_mouth = A / 2 // 颈部咀半径
  const r_in = n / 2 // 内孔半径
  // --- 第一部分：底盘 (圆柱) ---
  // V = π * R² * Thickness
  const v_base = Math.PI * Math.pow(R_out, 2) * C

  // --- 第二部分：颈部 (圆台) ---
  // 正确公式: V = (1/3) * π * h * (R² + R*r + r²)
  const h_hub = H - C // 颈部高度
  const v_hub =
    (1 / 3) * Math.PI * h_hub * (Math.pow(R_root, 2) + R_root * r_mouth + Math.pow(r_mouth, 2))

  // --- 第三部分：内孔 (贯穿圆柱) ---
  // V = π * r² * Height (贯穿整体高度 H)
  const v_bore = Math.PI * Math.pow(r_in, 2) * H
  // 3. 计算实体总体积 (mm³)
  const total_volume_mm3 = v_base + v_hub - v_bore
  // 4. 重量换算 (核心关键)
  // 密度单位是 g/cm³，体积是 mm³
  // 1 g/cm³ = 0.001 g/mm³
  // Weight(g) = Volume(mm³) * Density(g/cm³) * 0.001
  // Weight(kg) = Weight(g) / 1000
  // 综合系数: 1e-6

  const weight_kg = total_volume_mm3 * density * 0.000001

  return weight_kg.toFixed(3) // 保留3位小数
}
async function queryParams(type, surface, pressure, size) {
  const key = `${type}_${surface}_${pressure}_${size}`
  console.log('拼接字符串', key)
  const res = await getflList()
  console.log('接口返回数据：', res)
  const result = res.data[key]

  if (result) {
    // 成功获取 6 参数，自动填入表单进行下一步重量计算
    console.log('result', result)
    const weight = calculateWNWeight(result, 6.17)
    console.warn('重量结果', weight)
  } else {
    console.warn('暂无此 HG/T 20615 规格数据')
  }
}
async function computeNum() {
  console.log('formData', formData)
  if (
    formData.flType &&
    formData.sealingSurface &&
    formData.nominalPressure &&
    formData.nominalPipeSize
  ) {
    console.log('集齐 4 规定，准备查表计算...')
    await queryParams(
      formData.flType,
      formData.sealingSurface,
      formData.nominalPressure,
      formData.nominalPipeSize
    )
  }
}

//4规定（150LB WN RF 1/2"）
//flType: 法兰类型
//sealingSurface: 密封面
//nominalPressure: 公称压力 150LB（磅）相当于  150class (级)
//nominalPipeSize: 公称管径  1/2" 代表 DF为15
//6参数
// 外径D   outerDiameter
// 根径N   rootDiameter
// 咀径A   mouthDiameter
// 内径n   innerDiameter
// 片厚C   thickness
// 总高H   height
</script>

<style scoped></style>
