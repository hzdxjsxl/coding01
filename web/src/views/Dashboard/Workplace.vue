<template>
  <ContentWrap title="法兰锻造下料计算 (Blanking & Punching)">
    <div class="mb-4">
      <el-form :inline="true" :model="queryForm" class="demo-form-inline">
        <el-form-item label="类型" class="itemForm">
          <el-select v-model="queryForm.type" placeholder="法兰类型" class="w-32">
            <el-option label="WN 带颈对焊" value="WN" />
          </el-select>
        </el-form-item>
        <el-form-item label="密封面" class="itemForm">
          <el-select v-model="queryForm.face" placeholder="密封面" class="w-32">
            <el-option label="RF 突面" value="RF" />
          </el-select>
        </el-form-item>
        <el-form-item label="压力" class="itemForm">
          <el-select v-model="queryForm.pressure" placeholder="压力等级" class="w-32">
            <el-option label="Class 150" value="150LB" />
            <el-option label="Class 300" value="300LB" />
          </el-select>
        </el-form-item>
        <el-form-item label="尺寸" class="itemForm">
          <el-select v-model="queryForm.size" placeholder="公称直径" class="w-32">
            <el-option label='1/2 "' value="1/2" />
            <el-option label='1 "' value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="材料密度" class="itemForm">
          <el-input-number
            v-model="queryForm.density"
            :precision="2"
            :step="0.01"
            controls-position="right"
            class="w-32"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">
            <Icon icon="ep:search" class="mr-1" /> 检索规格
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-card shadow="never" class="mb-4">
      <template #header>
        <div class="flex justify-between items-center">
          <span>规格匹配结果 (请点击选择一行作为基准)</span>
          <el-tag v-if="currentRow" type="success">已选中: {{ currentRow.specName }}</el-tag>
          <el-tag v-else type="info">未选择</el-tag>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        highlight-current-row
        @current-change="handleCurrentChange"
        border
        v-loading="loading"
        height="250"
      >
        <el-table-column type="index" width="50" label="#" />
        <el-table-column prop="D" label="外径 (D)" width="100" />
        <el-table-column prop="N" label="根径 (N)" width="100" />
        <el-table-column prop="A" label="咀径 (A)" width="100" />
        <el-table-column prop="C" label="标准片厚 (C)" width="120">
          <template #default="scope">
            <span v-if="scope && scope.row" class="font-bold text-blue-600">{{ scope.row.C }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="H" label="标准总高 (H)" width="120">
          <template #default="scope">
            <span v-if="scope && scope.row" class="font-bold text-blue-600">{{ scope.row.H }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="n" label="标准内径 (n)" width="120" />
        <el-table-column prop="source" label="数据来源" />
      </el-table>
    </el-card>

    <transition name="el-fade-in">
      <div v-if="currentRow" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <el-card shadow="hover" header="工艺余量修正 (Process Margins)">
          <el-form label-position="top">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="片厚余量 (Thickness Margin)">
                  <el-input-number v-model="margins.C_add" :min="0" :step="1" class="w-full" />
                  <div class="text-xs text-gray-400 mt-1">
                    下料片厚: {{ (Number(currentRow.C) + margins.C_add).toFixed(1) }} mm
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="总高余量 (Height Margin)">
                  <el-input-number v-model="margins.H_add" :min="0" :step="1" class="w-full" />
                  <div class="text-xs text-gray-400 mt-1">
                    下料总高: {{ (Number(currentRow.H) + margins.H_add).toFixed(1) }} mm
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-divider content-position="left">冲孔参数</el-divider>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="实际冲头直径 (Punch Diameter)">
                  <el-input-number v-model="margins.n_val" :min="0" :step="0.5" class="w-full" />
                  <div class="text-xs text-gray-400 mt-1">
                    * 默认载入标准内径，可手动修改为现有冲头尺寸
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>

        <el-card shadow="hover" class="bg-gray-50">
          <template #header>
            <div class="flex items-center text-lg font-bold">
              <Icon icon="ep:cpu" class="mr-2" /> 实时计算结果
            </div>
          </template>

          <div class="flex flex-col h-full justify-center space-y-6 py-4">
            <div class="text-center">
              <div class="text-sm text-gray-500 uppercase font-bold tracking-wider"
                >下料重量 (Blank Weight)</div
              >
              <div class="text-5xl font-mono font-bold text-green-600 my-2">
                {{ calculatedResult.blank }}
              </div>
              <div class="text-sm text-gray-400">kg (实心毛坯)</div>
            </div>

            <el-divider border-style="dashed" />

            <div class="text-center">
              <div class="text-sm text-gray-500 uppercase font-bold tracking-wider"
                >冲芯重量 (Punch Weight)</div
              >
              <div class="text-3xl font-mono font-bold text-red-500 my-2">
                {{ calculatedResult.punch }}
              </div>
              <div class="text-sm text-gray-400">kg (废料扣除)</div>
            </div>
          </div>
        </el-card>
      </div>
    </transition>
  </ContentWrap>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { ContentWrap } from '@/components/ContentWrap'
import { getDeptListPage } from '@/api/dept'
import { ElMessage } from 'element-plus'
import { Icon } from '@/components/Icon' // 确保引入图标组件

// --- ⚡️ 强制显式引入 Element Plus 组件 (防止自动导入失效) ---
import {
  ElForm,
  ElFormItem,
  ElSelect,
  ElOption,
  ElInputNumber,
  ElButton,
  ElCard,
  ElTable,
  ElTableColumn,
  ElTag,
  ElDivider,
  ElRow,
  ElCol
} from 'element-plus'

// --- 1. 类型定义 ---
interface FlangeData {
  id: string
  specName: string
  D: number
  N: number
  A: number
  C: number
  H: number
  n: number
  source: string
}

// --- 2. 状态数据 ---
const loading = ref(false)
const queryForm = reactive({
  type: 'WN',
  face: 'RF',
  pressure: '150LB',
  size: '1/2',
  density: 6.17
})

// 表格数据
const tableData = ref<FlangeData[]>([]) // 初始化为空数组
const currentRow = ref<FlangeData | null>(null)

// 工艺余量 (Margins)
const margins = reactive({
  C_add: 2,
  H_add: 3,
  n_val: 0
})

// --- 3. 模拟数据 (Mock Data) ---
const mockDatabase: FlangeData[] = [
  {
    id: '101',
    specName: 'WN 150LB 1/2" (DN15) - SCH40',
    D: 89,
    N: 30,
    A: 21.3,
    C: 11.2,
    H: 48,
    n: 15.7,
    source: 'ASME B16.5'
  },
  {
    id: '102',
    specName: 'WN 150LB 4" (DN100) - SCH40',
    D: 229,
    N: 135,
    A: 114.3,
    C: 23.9,
    H: 76,
    n: 102.3,
    source: 'ASME B16.5'
  }
]

// --- 4. 业务逻辑 ---
const handleQuery = () => {
  console.log('开始检索...')
  loading.value = true
  currentRow.value = null

  setTimeout(() => {
    // 强制赋值模拟数据
    tableData.value = mockDatabase
    loading.value = false
    ElMessage.success(`检索成功: 获取到 ${tableData.value.length} 条记录`)
  }, 500)
}

const handleCurrentChange = (val: FlangeData | undefined) => {
  if (!val) return
  currentRow.value = val
  // 选中时重置余量
  margins.C_add = 2
  margins.H_add = 3
  margins.n_val = val.n
}

// --- 5. 核心计算 ---
const calculatedResult = computed(() => {
  if (!currentRow.value) return { blank: '0.000', punch: '0.000' }

  const row = currentRow.value
  const d_rho = queryForm.density

  const raw_D = row.D
  const raw_N = row.N
  const raw_A = row.A
  const raw_C = Number(row.C) + margins.C_add
  const raw_H = Number(row.H) + margins.H_add
  const punch_d = margins.n_val

  if (raw_H <= raw_C) return { blank: 'ERR', punch: '0.000' }

  // 下料重量 (实心)
  const R_base = raw_D / 2
  const v_base = Math.PI * Math.pow(R_base, 2) * raw_C
  const h_hub = raw_H - raw_C
  const R_root = raw_N / 2
  const r_mouth = raw_A / 2
  const v_hub =
    (1 / 3) * Math.PI * h_hub * (Math.pow(R_root, 2) + R_root * r_mouth + Math.pow(r_mouth, 2))

  const weight_blank = (v_base + v_hub) * d_rho * 0.000001

  // 冲芯重量
  const weight_punch = Math.PI * Math.pow(punch_d / 2, 2) * raw_H * d_rho * 0.000001

  return {
    blank: weight_blank.toFixed(3),
    punch: weight_punch.toFixed(3)
  }
})
onMounted(async () => {
  await getDeptListPage().then((res: any) => {
    console.log('res', res)
  })
})
</script>

<style scoped>
/* 稍微调整数字显示字体，增加专业感 */
.font-mono {
  font-family: Consolas, Monaco, monospace;
}

.w-32 {
  width: 100%;
}

.itemForm {
  width: 15%;
}
</style>
