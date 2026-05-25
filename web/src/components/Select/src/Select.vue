<template>
  <ElSelect v-model="selectedValue">
    <ElOption
      v-for="(item, index) in options"
      :key="item[fieldMap.value] ?? index"
      :label="item[fieldMap.label]"
      :value="item[fieldMap.value] ?? index"
    >
      <slot name="option" :item="item">
        <div
          style="display: flex; justify-content: space-between; align-items: center; width: 100%"
        >
          <span style="font-weight: 500; color: #1f2937">
            {{ item[fieldMap.label] }}
          </span>

          <div
            v-if="extraDetails && extraDetails.length > 0"
            style="display: flex; gap: 16px; font-size: 13px"
          >
            <span
              v-for="(detail, i) in extraDetails"
              :key="i"
              :style="{
                color:
                  detail.label === 'stockNum' && item[detail.label] < 10 ? '#ef4444' : '#6b7280'
              }"
            >
              {{ detail.name }}: {{ item[detail.label] ?? '-' }}
            </span>
          </div>
        </div>
      </slot>
    </ElOption>
  </ElSelect>
</template>

<script setup lang="ts" generic="T extends Record<string, any>">
import { computed } from 'vue'
import { ElOption, ElSelect } from 'element-plus'

const props = withDefaults(
  defineProps<{
    modelValue: any
    options?: T[]
    fieldProps?: { label: string; value: string; [key: string]: string }
    // 🌟 新增：接收外部传进来的属性配置数组
    extraDetails?: { label: string; name: string }[]
  }>(),
  {
    options: () => [],
    fieldProps: () => ({ label: 'label', value: 'value' }) as any,
    extraDetails: () => [] // 默认空数组
  }
)

const emit = defineEmits(['update:modelValue'])

defineSlots<{
  option(props: { item: T }): any
}>()

const selectedValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const fieldMap = computed(() => props.fieldProps)
</script>
