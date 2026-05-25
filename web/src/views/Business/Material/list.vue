<script setup lang="tsx">
import { ContentWrap } from '@/components/ContentWrap'
import { Search } from '@/components/Search'
import { Dialog } from '@/components/Dialog'
import { useI18n } from '@/hooks/web/useI18n'
import { Table } from '@/components/Table'
import type { MaterialItem } from '@/api/material/types'
import { useTable } from '@/hooks/web/useTable'
import { ref, unref, reactive } from 'vue'
import Write from './components/Write.vue'
import Detail from './components/Detail.vue'
import { CrudSchema, useCrudSchemas } from '@/hooks/web/useCrudSchemas'
import { BaseButton } from '@/components/Button'
import { saveMaterialApi, delMaterialApi, getMaterialListApi } from '@/api/material'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
const ids = ref<number[]>([])

const { tableRegister, tableState, tableMethods } = useTable({
  fetchDataApi: async () => {
    const { currentPage, pageSize } = tableState
    const res = await getMaterialListApi({
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
const userStore = useUserStore()
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
    field: 'stockNum',
    label: '库存数量',
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
    field: 'unit',
    label: '数量单位',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'description',
    label: '物品描述',
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
    field: 'purpose',
    label: '用途',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'address',
    label: '所在位置',
    search: {
      hidden: true
    },
    form: {
      component: 'Input'
    }
  },
  {
    field: 'locationImage',
    label: '位置图片',
    search: {
      hidden: true
    },
    form: {
      component: 'Upload',
      label: '位置图片',
      colProps: { span: 24 },
      componentProps: {
        action: '/api/common/upload',
        headers: {
          Authorization: userStore.getToken
        },
        limit: 1,
        listType: 'picture-card',
        showFileList: true,
        accept: '.jpg,.jpeg,.png',
        beforeUpload: (rawFile) => {
          if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
            ElMessage.error('只能上传 JPG/PNG 格式的图片!')
            return false
          }
          if (rawFile.size / 1024 / 1024 > 2) {
            ElMessage.error('图片大小不能超过 2MB!')
            return false
          }
          return true
        },
        slots: {
          default: () => (
            <>
              <el-icon size="28" color="#8c939d">
                +
              </el-icon>
            </>
          )
        }
      }
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

const currentRow = ref<MaterialItem | null>(null)
const actionType = ref('')

const AddAction = () => {
  dialogTitle.value = t('exampleDemo.add')
  currentRow.value = null
  dialogVisible.value = true
  actionType.value = ''
}

const delLoading = ref(false)

const delData = async (row: MaterialItem | null) => {
  try {
    await ElMessageBox.confirm(`确定要执行此操作吗？`, {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
      draggable: true
    })
    const elTableExpose = await getElTableExpose()
    ids.value = row
      ? [row.id]
      : elTableExpose?.getSelectionRows().map((v: MaterialItem) => v.id) || []
    delLoading.value = true
    await delMaterialApi(unref(ids.value)).finally(() => {
      delLoading.value = false
      getList()
    })
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.info('已取消删除操作')
  }
}

const action = (row: MaterialItem, type: string) => {
  dialogTitle.value = t(type === 'edit' ? 'exampleDemo.edit' : 'exampleDemo.detail')
  actionType.value = type
  // 1拷贝利用扩展运算符把数据剥离出来
  // 这样无论你怎么改 copyRow，都不会影响到外层列表的 row！
  const copyRow = { ...row }
  // 2逆向组装与防弹衣
  if (type === 'edit' && copyRow.locationImage) {
    // 防御性编程：只有当它是纯字符串时，才去做数组包装！
    // 这样就算由于某种极端情况再次进来，也不会发生数组套娃
    if (typeof copyRow.locationImage === 'string') {
      copyRow.locationImage = [
        {
          name: '库位图',
          url: copyRow.locationImage
        }
      ] as any
    }
  }
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
    // 3. 🌟 核心清洗逻辑：把臃肿的图片数组，精准扒出那条 URL 字符串
    if (Array.isArray(formData.locationImage) && formData.locationImage.length > 0) {
      // 工业级防御：使用可选链 ?. 防止中途哪个字段没拿到导致整个系统崩溃
      const realUrl = formData.locationImage[0]?.response?.data

      if (realUrl) {
        formData.locationImage = realUrl
      } else {
        // 如果还没上传完（没 response）就点保存，或者后端报错了
        ElMessage.error('图片正在上传或上传失败，请稍后再试！')
        return
      }
    } else {
      // 如果用户把图片删了，或者没传图，直接塞个空字符串或者 null 给后端
      formData.locationImage = ''
    }
    console.log('formData', formData)
    const res = await saveMaterialApi(formData)
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
      <BaseButton type="primary" @click="AddAction">{{ t('exampleDemo.add') }}</BaseButton>
      <BaseButton :loading="delLoading" type="danger" @click="delData(null)">
        {{ t('exampleDemo.del') }}
      </BaseButton>
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
