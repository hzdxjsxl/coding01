<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Select } from '@/components/Select'
// import { Dialog } from '@/components/Dialog'
import { ElDrawer, ElTable, ElInputNumber, ElTableColumn, ElDivider, ElButton } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { Table } from '@/components/Table'
import type { OrderItem } from '@/api/order/types'
import { useTable } from '@/hooks/web/useTable'
import { getMaterialListApi } from '@/api/material'
import type { MaterialItem } from '@/api/material/types'
import { ref, unref, reactive, onMounted } from 'vue'
import Write from './components/Write.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { BaseButton } from '@/components/Button'
import {
  saveOrderApi,
  delOrderApi,
  getOrderListApi,
  updateStatusApi,
  exportOrderApi
} from '@/api/order'
import { getCustomerListApi } from '@/api/customer'
import { ElTag, ElMessageBox, ElMessage } from 'element-plus'
const ids = ref<number[]>([])

const { tableRegister, tableState, tableMethods } = useTable({
  fetchDataApi: async () => {
    const { currentPage, pageSize } = tableState
    const res = await getOrderListApi({
      pageIndex: unref(currentPage),
      pageSize: unref(pageSize),
      ...unref(searchParams)
    })
    return {
      list: res.data.list,
      total: res.data.total
    }
  }
})
const { loading, dataList, total, currentPage, pageSize } = tableState
const { getList, getElTableExpose } = tableMethods

const searchParams = ref({})
const setSearchParams = (params: any) => {
  searchParams.value = params
  getList()
}

const { t } = useI18n()
const customerOptions = reactive<any[]>([])
const remoteLoading = ref(false)

const fetchCustomers = async (keyword = '') => {
  remoteLoading.value = true
  try {
    const res = await getCustomerListApi({ keyword, pageIndex: 1, pageSize: 20 })

    // 🌟 核心：清空原数组，用 push 塞入新数据，绝不改变内存地址！
    customerOptions.length = 0
    res.data.list.forEach((item) => {
      customerOptions.push({
        label: `${item.companyName} - ${item.contactName}`,
        value: item.id
      })
    })
  } finally {
    remoteLoading.value = false
  }
}

onMounted(() => fetchCustomers(''))
const crudSchemas = reactive<CrudSchema[]>([
  {
    field: 'selection',
    search: {
      hidden: true
    },
    form: {
      hidden: true
    },
    detail: {
      hidden: true
    },
    table: {
      type: 'selection'
    }
  },
  {
    field: 'index',
    label: '序号',
    form: {
      hidden: true
    },
    search: {
      hidden: true
    },
    detail: {
      hidden: true
    },
    table: {
      type: 'index'
    }
  },
  {
    field: 'orderName',
    label: '订单说明',
    search: {
      hidden: false
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'customerId',
    label: '选择客户',
    form: {
      component: 'Select',
      componentProps: {
        filterable: true,
        remote: true,
        reserveKeyword: true,
        remoteShowSuffix: true,
        placeholder: '请输入关键字检索...',
        options: customerOptions,
        remoteMethod: fetchCustomers
      }
    },
    search: {
      hidden: true
    },
    table: {
      hidden: true
    },
    detail: {
      hidden: true
    }
  },
  {
    field: 'contactName',
    label: '客户姓名',
    search: {
      hidden: false
    },
    form: {
      remove: true
    }
  },
  {
    field: 'companyName',
    label: '客户公司',
    search: {
      hidden: false
    },
    form: {
      remove: true
    }
  },
  {
    field: 'phone',
    label: '联系方式',
    search: {
      hidden: true
    },
    form: {
      remove: true
    }
  },
  {
    field: 'totalAmount',
    label: '总金额',
    search: {
      hidden: true
    },
    form: {
      component: 'InputNumber',
      componentProps: {
        precision: 2,
        step: 100
      },
      value: 0
    }
  },
  {
    field: 'paidAmount',
    label: '已支付金额',
    search: {
      hidden: true
    },
    form: {
      component: 'InputNumber',
      componentProps: {
        precision: 2,
        step: 100
      },
      value: 0
    }
  },
  {
    field: 'unpaidAmount',
    label: '未支付金额',
    search: {
      hidden: true
    },
    form: {
      remove: true,
      value: 0
    }
  },
  {
    field: 'orderTime',
    label: '下单时间',
    search: {
      hidden: false,
      component: 'DatePicker',
      componentProps: {
        type: 'datetimerange', // 开启“日期时间范围”模式
        valueFormat: 'YYYY-MM-DD HH:mm:ss', // 极其重要：直接格式化好传给后端，省去前端再转时间的麻烦
        rangeSeparator: '-', // 中间的分隔符
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间'
      }
    },
    form: {
      component: 'DatePicker',
      componentProps: {
        type: 'datetime',
        valueFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    }
  },
  {
    field: 'deliveryTime',
    label: '交付时间',
    search: {
      hidden: false,
      component: 'DatePicker',
      componentProps: {
        type: 'datetimerange', // 开启“日期时间范围”模式
        valueFormat: 'YYYY-MM-DD HH:mm:ss', // 极其重要：直接格式化好传给后端，省去前端再转时间的麻烦
        rangeSeparator: '-', // 中间的分隔符
        startPlaceholder: '开始时间',
        endPlaceholder: '结束时间'
      }
    },
    form: {
      component: 'DatePicker',
      componentProps: {
        type: 'datetime',
        valueFormat: 'YYYY-MM-DD HH:mm:ss'
      }
    }
  },
  {
    field: 'status',
    label: '完成状态',
    search: {
      hidden: false,
      component: 'Select',
      componentProps: {
        options: [
          {
            value: '1',
            label: '已完成'
          },
          {
            value: '0',
            label: '未完成'
          },
          {
            value: '2',
            label: '进行中'
          }
        ]
      }
    },
    form: {
      hidden: true,
      value: 0
    },
    table: {
      slots: {
        default: (data: any) => {
          const status = data.row.status
          if (status === 0) {
            return (
              <ElTag type="danger" effect="light">
                未开始
              </ElTag>
            )
          }
          if (status === 1) {
            return (
              <ElTag type="success" effect="light">
                已完成
              </ElTag>
            )
          }
          if (status === 2) {
            return (
              <ElTag type="primary" effect="light">
                进行中
              </ElTag>
            )
          }
          return <ElTag type="info">未知状态</ElTag>
        }
      }
    }
  },
  {
    field: 'inventoryFlowList',
    label: '',
    search: { hidden: true },
    table: { hidden: true },
    form: {
      colProps: { span: 24 },
      value: [{ materialId: '', changeNum: 1 }]
    }
  },
  {
    field: 'action',
    width: '260px',
    label: t('tableDemo.action'),
    search: {
      hidden: true
    },
    form: {
      hidden: true
    },
    detail: {
      hidden: true
    },
    table: {
      slots: {
        default: (data: any) => {
          return (
            <>
              <BaseButton type="primary" onClick={() => action(data.row, 'edit')}>
                {t('exampleDemo.edit')}
              </BaseButton>
              <BaseButton type="success" onClick={() => action(data.row, 'detail')}>
                {t('exampleDemo.detail')}
              </BaseButton>
              <BaseButton type="danger" onClick={() => delData(data.row)}>
                {t('exampleDemo.del')}
              </BaseButton>
            </>
          )
        }
      }
    }
  }
])

// @ts-ignore
const { allSchemas } = useCrudSchemas(crudSchemas)

const dialogVisible = ref(false)
const dialogTitle = ref('')

const currentRow = ref<OrderItem | null>(null)
const actionType = ref('')

const AddAction = () => {
  dialogTitle.value = t('exampleDemo.add')
  currentRow.value = null
  dialogVisible.value = true
  actionType.value = ''
}

const delLoading = ref(false)
const delData = async (row: OrderItem | null) => {
  try {
    await ElMessageBox.confirm(`确定要执行此操作吗？`, {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    })
    const elTableExpose = await getElTableExpose()
    ids.value = row ? [row.id] : elTableExpose?.getSelectionRows().map((v: OrderItem) => v.id) || []
    delLoading.value = true
    await delOrderApi(unref(ids.value)).finally(() => {
      delLoading.value = false
      getList()
    })
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.info('已取消删除操作')
  }
}
const updateOrderStatus = async (row: OrderItem | null, status: number | null) => {
  const elTableExpose = await getElTableExpose()
  ids.value = row ? [row.id] : elTableExpose?.getSelectionRows().map((v: OrderItem) => v.id) || []
  await updateStatusApi({ ids: unref(ids.value), status: status }).finally(() => {
    getList()
  })
}
const exportOrder = async () => {
  await exportOrderApi({
    ...unref(searchParams),
    noPage: 1
  })
}
const action = (row: OrderItem, type: string) => {
  dialogTitle.value = t(type === 'edit' ? 'exampleDemo.edit' : 'exampleDemo.detail')
  if (type === 'edit') {
    const exist = customerOptions.find((opt) => opt.value === row.customerId)
    if (!exist && row.customerId) {
      // 如果没有，直接把订单自带的公司名和联系人塞进去
      customerOptions.push({
        label: `${row.companyName} - ${row.contactName}`,
        value: row.customerId
      })
    }
  }
  actionType.value = type
  console.log('rowsss', row)
  currentRow.value = row
  dialogVisible.value = true
}

const writeRef = ref<ComponentRef<typeof Write>>()

const saveLoading = ref(false)

const save = async () => {
  const write = unref(writeRef)
  console.log('1. write实例是否存在：', !!write)
  const formData = await write?.submit()
  console.log('2. submit返回的数据：', formData)
  if (formData) {
    const info = {
      ...formData
    }
    saveLoading.value = true
    const res = await saveOrderApi(info)
      .catch(() => {})
      .finally(() => {
        saveLoading.value = false
      })
    if (res) {
      dialogVisible.value = false
      currentPage.value = 1
      getList()
    }
  }
}
const materialOptions = ref<MaterialItem[]>([])

// 🌟 2. 依然是这个外部逻辑函数，负责去后端拿数据
const getRemoteMaterialList = async (query: string) => {
  if (query) {
    const res = await getMaterialListApi({
      noPage: 1,
      materialName: query
    })
    materialOptions.value = res.data.list
  } else {
    materialOptions.value = []
  }
}
</script>

<template>
  <ContentWrap>
    <Search :schema="allSchemas.searchSchema" @search="setSearchParams" @reset="setSearchParams" />

    <div class="mb-10px">
      <BaseButton type="primary" @click="AddAction">{{ t('exampleDemo.add') }}</BaseButton>
      <BaseButton :loading="delLoading" type="danger" @click="delData(null)">
        {{ t('exampleDemo.del') }}
      </BaseButton>
      <BaseButton type="success" @click="updateOrderStatus(null, 1)"> 已完成 </BaseButton>
      <BaseButton type="danger" @click="updateOrderStatus(null, 2)"> 进行中 </BaseButton>
      <BaseButton type="warning" @click="exportOrder()"> 导出 </BaseButton>
    </div>

    <Table
      v-model:pageSize="pageSize"
      v-model:currentPage="currentPage"
      :columns="allSchemas.tableColumns"
      :data="dataList"
      :loading="loading"
      :pagination="{
        total: total
      }"
      @register="tableRegister"
    />
  </ContentWrap>
  <ElDrawer
    v-model="dialogVisible"
    :title="dialogTitle"
    size="900px"
    class="premium-drawer"
    :destroy-on-close="true"
  >
    <div class="drawer-body-bg">
      <div class="premium-card">
        <div class="card-title">订单基础信息</div>

        <Write
          v-if="actionType !== 'detail'"
          ref="writeRef"
          :form-schema="allSchemas.formSchema"
          :current-row="currentRow"
        >
          <template #inventoryFlowList="{ formModel }">
            <ElDivider style="margin: 30px 0 20px" />

            <div
              style="
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 16px;
              "
            >
              <div class="card-title" style="margin-bottom: 0">物品明细</div>
              <ElButton
                type="success"
                @click="formModel.inventoryFlowList.push({ materialId: '', quantity: 1 })"
                >添加库存</ElButton
              >
            </div>

            <ElTable :data="formModel.inventoryFlowList" stripe class="premium-table">
              <ElTableColumn type="index" label="序号" width="60" align="center" />
              <ElTableColumn label="选择物品">
                <template #default="{ row }">
                  <Select
                    v-model="row.materialId"
                    filterable
                    remote
                    reserve-keyword
                    placeholder="输入关键词检索..."
                    :remote-method="getRemoteMaterialList"
                    :options="materialOptions"
                    :fieldProps="{ label: 'materialName', value: 'id' }"
                    :extra-details="[
                      { label: 'materialType', name: '类型' },
                      { label: 'stockNum', name: '库存' },
                      { label: 'unit', name: '单位' }
                    ]"
                    class="borderless-input w-full"
                  />
                </template>
              </ElTableColumn>

              <ElTableColumn label="需求数量" width="180">
                <template #default="{ row }">
                  <ElInputNumber
                    v-model="row.changeNum"
                    :precision="2"
                    :step="0.1"
                    style="width: 100%"
                  />
                </template>
              </ElTableColumn>

              <ElTableColumn label="操作" width="90" align="center">
                <template #default="{ $index }">
                  <ElButton type="danger" @click="formModel.inventoryFlowList.splice($index, 1)"
                    >删除</ElButton
                  >
                </template>
              </ElTableColumn>
            </ElTable>
          </template>
        </Write>

        <Detail
          v-if="actionType === 'detail'"
          :detail-schema="allSchemas.detailSchema"
          :current-row="currentRow"
        />
      </div>
    </div>

    <template #footer>
      <div class="drawer-footer">
        <BaseButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</BaseButton>
        <BaseButton v-if="actionType !== 'detail'" type="primary" @click="save">
          {{ t('exampleDemo.save') }}
        </BaseButton>
      </div>
    </template>
  </ElDrawer>
</template>
<style scoped>
/* 1. 抽屉重置：干掉默认 padding，使用浅灰背景 */
:deep(.premium-drawer .el-drawer__body) {
  padding: 0 !important;
  background-color: #f4f6f8;
}

:deep(.premium-drawer .el-drawer__header) {
  padding: 20px 24px;
  margin-bottom: 0;
  font-weight: bold;
  color: #111827;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
}

:deep(.premium-drawer .el-drawer__footer) {
  padding: 0; /* 底部 footer 接管布局 */
}

/* 2. 灰色背景容器 */
.drawer-body-bg {
  min-height: 100%;
  box-sizing: border-box;
}

/* 3. 纯白高级卡片（关键所在：微妙的阴影） */
.premium-card {
  padding: 24px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  box-shadow:
    0 1px 3px 0 rgb(0 0 0 / 5%),
    0 1px 2px -1px rgb(0 0 0 / 5%);
}

/* 4. 带主题色左划线的标题 */
.card-title {
  position: relative;
  padding-left: 10px;
  margin-bottom: 24px;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.2;
  color: #1f2937;
}

.card-title::before {
  position: absolute;
  top: 1px;
  bottom: 1px;
  left: 0;
  width: 4px;
  background-color: var(--el-color-primary);
  border-radius: 2px;
  content: '';
}

/* 5. 极致去线化表格（杀掉竖线，只留横向视线引导） */
.premium-table {
  border: 1px solid #f3f4f6; /* 极其微弱的外边框 */
  border-radius: 6px;
}

.premium-table :deep(th.el-table__cell) {
  font-weight: 600;
  color: #4b5563;
  background-color: #f9fafb !important;
  border-bottom: 1px solid #e5e7eb !important;
}

.premium-table :deep(td.el-table__cell) {
  border-right: none !important; /* 杀掉纵向线 */
  border-bottom: 1px solid #f3f4f6 !important;
  border-left: none !important;
}

/* 6. 表格内的无边框输入组件（平时无形，Hover/Focus 时才显现边框） */
.borderless-input :deep(.el-input__wrapper) {
  background-color: transparent;
  box-shadow: none !important;
  transition: all 0.2s;
}

.borderless-input :deep(.el-input__wrapper:hover),
.borderless-input :deep(.el-input__wrapper.is-focus) {
  background-color: #fff;
  box-shadow: 0 0 0 1px var(--el-color-primary) inset !important;
}

/* 7. 底部固定操作台 */
.drawer-footer {
  display: flex;
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid #e5e7eb;
  justify-content: flex-end;
}

.drawer-footer .el-button {
  min-width: 80px;
}
</style>
