<template>
  <div class="p-4">
    <el-card shadow="never" class="mb-4">
      <el-form :inline="true">
        <el-form-item label="部门名称">
          <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData">搜索</el-button>
          <el-button type="success" plain>新增部门</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="deptList"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        default-expand-all
        border
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="console.log(scope.row)"
              >修改</el-button
            >
            <el-button link type="primary" size="small">新增子级</el-button>
            <el-button link type="danger" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { getDeptList } from '@/api/department' // 引入 API
import { handleTree } from '@/utils/tree' // 引入算法

const loading = ref(false)
const deptList = ref<any[]>([])
const queryParams = reactive({
  deptName: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    // 1. 调后端接口
    const res = await getDeptList(queryParams)
    // 假设后端返回格式是 { code: 200, data: [...] }，请根据实际情况调整
    const rawData = res.data || res

    // 2. 核心魔法：转成树结构
    // 这里的 'id', 'parentId' 必须和你数据库字段对应！
    deptList.value = handleTree(rawData, 'id', 'parentId')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>
