<template>
  <div>
    <h2>补货建议</h2>
    <el-alert title="优先级规则说明" type="info" show-icon style="margin-bottom: 20px">
      <template #default>
        <ul style="margin: 0; padding-left: 20px; line-height: 1.8">
          <li><strong>基础优先级</strong> = 建议补货量 × 优先级系数 × 节假日系数</li>
          <li><strong>滞销判定</strong>：近30天日均销量 < 2件，自动降低优先级系数（最低0.3）</li>
          <li><strong>节假日加成</strong>：节假日期间按配置系数（如春节1.5倍、国庆1.3倍）增加补货量</li>
          <li><strong>排序规则</strong>：按最终优先级从高到低排序，优先补货高优先级商品</li>
        </ul>
      </template>
    </el-alert>
    <el-form :inline="true">
      <el-form-item label="选择门店">
        <el-select v-model="selectedStore" placeholder="全部门店" @change="loadSuggestions" clearable>
          <el-option v-for="store in stores" :key="store.id" :label="store.name" :value="store.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadSuggestions">刷新建议</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="suggestions" border style="width: 100%">
      <el-table-column prop="storeName" label="门店" width="150" />
      <el-table-column prop="productName" label="商品" width="150" />
      <el-table-column prop="currentStock" label="当前库存" width="100" align="center" />
      <el-table-column label="销量" width="180" align="center">
        <template #default="scope">
          <div>
            <div>近7日日均: <strong>{{ scope.row.avgDailySales }}</strong> 件</div>
            <div>近30日日均: <strong>{{ scope.row.avg30DaysSales?.toFixed(1) }}</strong> 件</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="suggestedQuantity" label="建议补货量" width="120" align="center">
        <template #default="scope">
          <el-tag type="success" size="small">{{ scope.row.suggestedQuantity }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级系数" width="120" align="center">
        <template #default="scope">
          <el-tag 
            :type="scope.row.priorityFactor < 1 ? 'danger' : scope.row.holidayFactor > 1 ? 'warning' : 'success'" 
            size="small"
          >
            {{ scope.row.priorityFactor?.toFixed(1) }}x
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="最终优先级" width="120" align="center">
        <template #default="scope">
          <span :style="{ fontWeight: 'bold', color: scope.row.isSlowMoving ? '#f56c6c' : '#67c23a' }">
            {{ scope.row.priority?.toFixed(1) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="优先级规则说明" min-width="200">
        <template #default="scope">
          <div>
            <el-tag 
              v-if="scope.row.isSlowMoving" 
              type="danger" 
              size="small" 
              style="margin-bottom: 5px"
            >
              滞销商品
            </el-tag>
            <div style="font-size: 12px; color: #606266">
              {{ scope.row.priorityReason }}
            </div>
            <div v-if="scope.row.holidayFactor > 1" style="font-size: 12px; color: #e6a23c; margin-top: 3px">
              节假日系数: {{ scope.row.holidayFactor }}x
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="scope">
          <el-button 
            size="small" 
            :type="scope.row.isSlowMoving ? 'info' : 'primary'" 
            @click="createRequest(scope.row)"
          >
            申请补货
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api'

const selectedStore = ref('')
const stores = ref([])
const suggestions = ref([])

const loadStores = async () => {
  const res = await api.getStores()
  stores.value = res.data.data
}

const loadSuggestions = async () => {
  const res = await api.getSuggestions(selectedStore.value || null)
  suggestions.value = res.data.data
}

const createRequest = async (item) => {
  try {
    const validateRes = await api.validateRequest(item.productId, item.suggestedQuantity)
    const validate = validateRes.data.data
    
    let content = `<div style="line-height: 1.8">
      <p><strong>商品：</strong>${item.productName}</p>
      <p><strong>申请数量：</strong><span style="color: #409eff; font-size: 18px; font-weight: bold">${item.suggestedQuantity} 件</span></p>
      <el-divider style="margin: 10px 0" />
      <p><strong>仓库库存校验：</strong>
        ${validate.warehouseSufficient 
          ? '<span style="color: #67c23a">✓ 充足</span>' 
          : `<span style="color: #f56c6c">✗ 不足</span>（缺口 ${validate.warehouseShortage} 件）`
        }
        <br/>仓库可用：${validate.warehouseStock} 件
      </p>
      <p><strong>运输容量校验：</strong>
        ${validate.transportSufficient 
          ? '<span style="color: #67c23a">✓ 充足</span>' 
          : `<span style="color: #f56c6c">✗ 不足</span>（缺口 ${validate.transportShortage} 件）`
        }
        <br/>剩余容量：${validate.availableTransportCapacity} / ${validate.maxTransportCapacity} 件
      </p>
    </div>`

    if (!validate.canSubmit) {
      await ElMessageBox.alert(
        content,
        '校验不通过',
        { 
          confirmButtonText: '知道了', 
          type: 'warning',
          dangerouslyUseHTMLString: true
        }
      )
      return
    }

    await ElMessageBox.confirm(
      content,
      '确认补货申请',
      { 
        confirmButtonText: '确认提交', 
        cancelButtonText: '取消', 
        type: 'info',
        dangerouslyUseHTMLString: true
      }
    )
    await api.createRequest({
      storeId: item.storeId,
      productId: item.productId,
      quantity: item.suggestedQuantity
    })
    ElMessage.success('补货申请已提交')
    loadSuggestions()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '申请失败')
    }
  }
}

onMounted(() => {
  loadStores()
  loadSuggestions()
})
</script>
