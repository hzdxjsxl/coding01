<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { Table } from '@/components/Table'
import type { InventoryItem } from '@/api/inventoryFlow/types'
import { useTable } from '@/hooks/web/useTable'
import { ref, unref, reactive } from 'vue'
import Write from './components/Write.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { BaseButton } from '@/components/Button'
import { getInventoryFlowListApi, saveInventoryFlowApi } from '@/api/inventoryFlow'

const { tableRegister, tableState, tableMethods } = useTable({
  fetchDataApi: async () => {
    const { currentPage, pageSize } = tableState
    const res = await getInventoryFlowListApi({
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
const { getList } = tableMethods
const searchParams = ref({})
const setSearchParams = (params: any) => {
  searchParams.value = params
  getList()
}

const { t } = useI18n()

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
    field: 'createUserName',
    label: '发起人',
    search: {
      hidden: false
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'materialName',
    label: '物品名称',
    search: {
      hidden: false
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'materialType',
    label: '物品类型',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'changeType',
    label: '变动类型',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'changeNum',
    label: '变动数量',
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
    field: 'beforeNum',
    label: '变动前总数',
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
    field: 'afterNum',
    label: '变动后总数 (预计算值)',
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
    field: 'relationOrderNo',
    label: '关联订单号',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'status',
    label: '状态',
    search: {
      hidden: true
    },
    table: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'remark',
    label: '备注',
    search: {
      hidden: true
    },
    table: {
      hidden: true
    },
    form: {
      component: 'Input'
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

const currentRow = ref<InventoryItem | null>(null)
const actionType = ref('')

// const AddAction = () => {
//   dialogTitle.value = t('exampleDemo.add')
//   currentRow.value = null
//   dialogVisible.value = true
//   actionType.value = ''
// }

const action = (row: InventoryItem, type: string) => {
  dialogTitle.value = t(type === 'edit' ? 'exampleDemo.edit' : 'exampleDemo.detail')
  actionType.value = type
  const copyRow = { ...row }
  currentRow.value = copyRow
  dialogVisible.value = true
}

const writeRef = ref<ComponentRef<typeof Write>>()

const saveLoading = ref(false)

const save = async () => {
  const write = unref(writeRef)
  const formData = await write?.submit()
  if (formData) {
    saveLoading.value = true
    const res = await saveInventoryFlowApi(formData)
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
</script>

<template>
  <ContentWrap>
    <Search :schema="allSchemas.searchSchema" @search="setSearchParams" @reset="setSearchParams" />

    <div class="mb-10px">
      <BaseButton type="danger">驳回</BaseButton>
      <BaseButton type="success">通过</BaseButton>
    </div>

    <Table
      v-model:pageSize="pageSize"
      v-model:currentPage="currentPage"
      :columns="allSchemas.tableColumns"
      :data="dataList"
      :loading="loading"
      :image-preview="['locationImage']"
      :pagination="{
        total: total
      }"
      @register="tableRegister"
    />
  </ContentWrap>

  <Dialog v-model="dialogVisible" :title="dialogTitle" destroy-on-close>
    <Write
      v-if="actionType !== 'detail'"
      ref="writeRef"
      :form-schema="allSchemas.formSchema"
      :current-row="currentRow"
    />

    <Detail
      v-if="actionType === 'detail'"
      :detail-schema="allSchemas.detailSchema"
      :current-row="currentRow"
    />

    <template #footer>
      <BaseButton
        v-if="actionType !== 'detail'"
        type="primary"
        :loading="saveLoading"
        @click="save"
      >
        {{ t('exampleDemo.save') }}
      </BaseButton>
      <BaseButton @click="dialogVisible = false">{{ t('dialogDemo.close') }}</BaseButton>
    </template>
  </Dialog>
</template>
