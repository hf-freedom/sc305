<template>
  <div>
    <h2>缺货预警</h2>
    <el-alert
      :title="`共 ${alerts.length} 条缺货预警，其中严重预警 ${criticalCount} 条，警告 ${warningCount} 条`"
      type="warning"
      show-icon
      style="margin-bottom: 20px"
    />
    <el-button type="primary" @click="loadAlerts">刷新预警</el-button>
    <el-divider />
    <el-table :data="alerts" border style="width: 100%">
      <el-table-column prop="level" label="级别" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getLevelType(scope.row.level)" size="small">
            {{ getLevelText(scope.row.level) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="storeName" label="门店" width="150" />
      <el-table-column prop="productName" label="商品" width="150" />
      <el-table-column prop="currentStock" label="当前库存" width="100" align="center">
        <template #default="scope">
          <span style="color: red">{{ scope.row.currentStock }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="safetyStock" label="安全库存" width="100" align="center" />
      <el-table-column prop="shortageQuantity" label="缺口数量" width="100" align="center">
        <template #default="scope">
          <el-tag type="danger" size="small">{{ scope.row.shortageQuantity }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="goToSuggestion">
            去补货
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'
import { useRouter } from 'vue-router'

const router = useRouter()
const alerts = ref([])

const criticalCount = computed(() => alerts.value.filter(a => a.level === 'CRITICAL').length)
const warningCount = computed(() => alerts.value.filter(a => a.level === 'WARNING').length)

const getLevelType = (level) => {
  const map = { CRITICAL: 'danger', WARNING: 'warning', INFO: 'info' }
  return map[level] || 'info'
}

const getLevelText = (level) => {
  const map = { CRITICAL: '严重', WARNING: '警告', INFO: '提示' }
  return map[level] || level
}

const loadAlerts = async () => {
  const res = await api.getAlerts()
  alerts.value = res.data.data
}

const goToSuggestion = () => {
  router.push('/suggestion')
}

onMounted(() => {
  loadAlerts()
})
</script>
