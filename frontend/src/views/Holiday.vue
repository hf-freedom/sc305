<template>
  <div>
    <h2>节假日配置</h2>
    <el-alert title="提示" type="info" style="margin-bottom: 20px">
      节假日期间，系统会根据配置的系数调整补货建议量，系数越高建议补货量越大
    </el-alert>
    <el-table :data="holidays" border style="width: 100%">
      <el-table-column prop="name" label="节假日名称" width="200" />
      <el-table-column prop="startDate" label="开始日期" width="150" align="center" />
      <el-table-column prop="endDate" label="结束日期" width="150" align="center" />
      <el-table-column prop="factor" label="补货系数" width="120" align="center">
        <template #default="scope">
          <el-tag type="primary" size="small">{{ scope.row.factor }}x</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="isActive(scope.row) ? 'success' : 'info'" size="small">
            {{ isActive(scope.row) ? '生效中' : '未生效' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const holidays = ref([])

const isActive = (holiday) => {
  const today = new Date()
  const start = new Date(holiday.startDate)
  const end = new Date(holiday.endDate)
  return today >= start && today <= end
}

const loadHolidays = async () => {
  const res = await api.getHolidays()
  holidays.value = res.data.data
}

onMounted(() => {
  loadHolidays()
})
</script>
