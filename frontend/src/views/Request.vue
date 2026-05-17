<template>
  <div>
    <h2>补货申请管理</h2>
    <el-form :inline="true">
      <el-form-item label="状态筛选">
        <el-select v-model="statusFilter" placeholder="全部状态" clearable @change="loadRequests">
          <el-option label="待审批" value="PENDING" />
          <el-option label="已批准" value="APPROVED" />
          <el-option label="已拒绝" value="REJECTED" />
          <el-option label="已收货" value="RECEIVED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadRequests">刷新</el-button>
      </el-form-item>
    </el-form>
    <el-alert
      v-if="warehouseInfo"
      :title="`仓库总览 - 可用库存总量: ${warehouseInfo.totalAvailable}, 锁定库存总量: ${warehouseInfo.totalLocked}`"
      type="info"
      show-icon
      style="margin-bottom: 20px"
    />
    <el-table :data="requests" border style="width: 100%">
      <el-table-column prop="storeName" label="门店" width="150" />
      <el-table-column prop="productName" label="商品" width="150" />
      <el-table-column prop="requestedQuantity" label="申请数量" width="100" align="center" />
      <el-table-column prop="approvedQuantity" label="批准数量" width="100" align="center" />
      <el-table-column prop="status" label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="仓库库存锁定" width="200" align="center">
        <template #default="scope">
          <template v-if="scope.row.status === 'APPROVED'">
            <el-tag type="warning" size="small">
              已锁定 {{ scope.row.approvedQuantity }} 件
            </el-tag>
          </template>
          <template v-else-if="scope.row.status === 'RECEIVED'">
            <el-tag type="info" size="small">
              已释放 {{ scope.row.approvedQuantity }} 件
            </el-tag>
          </template>
          <template v-else>
            <span style="color: #909399">-</span>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="requestTime" label="申请时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="scope">
          <template v-if="scope.row.status === 'PENDING'">
            <el-button size="small" type="success" @click="approve(scope.row)">批准</el-button>
            <el-button size="small" type="danger" @click="reject(scope.row)">拒绝</el-button>
          </template>
          <template v-else-if="scope.row.status === 'APPROVED'">
            <el-button size="small" type="primary" @click="receive(scope.row)">确认收货</el-button>
          </template>
          <template v-else>
            <span>-</span>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElInput } from 'element-plus'
import api from '../api'

const statusFilter = ref('')
const requests = ref([])
const stores = ref([])
const products = ref([])
const warehouseInfo = ref(null)

const getStatusType = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    RECEIVED: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    PENDING: '待审批',
    APPROVED: '已批准',
    REJECTED: '已拒绝',
    RECEIVED: '已收货'
  }
  return map[status] || status
}

const loadWarehouseInfo = async () => {
  const res = await api.getWarehouseInventory()
  const list = res.data.data
  warehouseInfo.value = {
    totalAvailable: list.reduce((sum, item) => sum + item.availableStock, 0),
    totalLocked: list.reduce((sum, item) => sum + item.lockedStock, 0)
  }
}

const loadRequests = async () => {
  const res = await api.getRequests(null, statusFilter.value || null)
  requests.value = res.data.data.map(item => {
    const store = stores.value.find(s => s.id === item.storeId)
    const product = products.value.find(p => p.id === item.productId)
    return {
      ...item,
      storeName: store ? store.name : item.storeId,
      productName: product ? product.name : item.productId
    }
  })
  await loadWarehouseInfo()
}

const approve = async (item) => {
  try {
    const validateRes = await api.validateRequest(item.productId, item.requestedQuantity)
    const validate = validateRes.data.data
    
    const invRes = await api.getStoreInventory(item.storeId)
    const currentInv = invRes.data.data.find(inv => inv.productId === item.productId)
    const currentInTransit = currentInv ? currentInv.inTransitStock : 0
    const currentAvailable = currentInv ? currentInv.availableStock : 0

    let content = `<div style="line-height: 1.8">
      <p><strong>门店：</strong>${item.storeName}</p>
      <p><strong>商品：</strong>${item.productName}</p>
      <p><strong>申请数量：</strong>${item.requestedQuantity} 件</p>
      <el-divider style="margin: 10px 0" />
      <p><strong>仓库库存变化：</strong></p>
      <p style="padding-left: 20px">
        可用库存：<span style="color: #67c23a">${validate.warehouseStock} 件</span><br/>
        本次锁定：<span style="color: #e6a23c">${item.requestedQuantity} 件</span><br/>
        锁定后可用：<span style="color: #f56c6c">${validate.warehouseStock - item.requestedQuantity} 件</span>
      </p>
      <p><strong>门店库存变化：</strong></p>
      <p style="padding-left: 20px">
        当前在途：<span style="color: #e6a23c">${currentInTransit} 件</span><br/>
        新增在途：<span style="color: #409eff">+ ${item.requestedQuantity} 件</span><br/>
        批准后在途：<span style="color: #409eff">${currentInTransit + item.requestedQuantity} 件</span><br/>
        当前可用：<span style="color: #67c23a">${currentAvailable} 件</span>
      </p>
      <p><strong>运输容量：</strong></p>
      <p style="padding-left: 20px">
        剩余容量：${validate.availableTransportCapacity} / ${validate.maxTransportCapacity} 件
      </p>
    </div>`

    const { value } = await ElMessageBox.prompt(content, '审批补货申请', {
      confirmButtonText: '确认批准',
      cancelButtonText: '取消',
      inputValue: item.requestedQuantity,
      dangerouslyUseHTMLString: true,
      inputValidator: (inputVal) => {
        const qty = parseInt(inputVal)
        if (!inputVal || isNaN(qty) || qty <= 0) {
          return '请输入有效的数量'
        }
        if (qty > validate.warehouseStock) {
          return '批准数量不能超过仓库可用库存'
        }
        return true
      }
    })
    await api.approveRequest(item.id, parseInt(value))
    ElMessage.success('审批成功，仓库库存已锁定，门店在途库存已更新')
    loadRequests()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '审批失败')
    }
  }
}

const reject = async (item) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await api.rejectRequest(item.id, value)
    ElMessage.success('已拒绝')
    loadRequests()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '操作失败')
    }
  }
}

const receive = async (item) => {
  try {
    const validateRes = await api.validateRequest(item.productId, 0)
    const validate = validateRes.data.data
    
    const invRes = await api.getStoreInventory(item.storeId)
    const currentInv = invRes.data.data.find(inv => inv.productId === item.productId)
    const currentInTransit = currentInv ? currentInv.inTransitStock : 0
    const currentAvailable = currentInv ? currentInv.availableStock : 0

    let content = `<div style="line-height: 1.8">
      <p><strong>门店：</strong>${item.storeName}</p>
      <p><strong>商品：</strong>${item.productName}</p>
      <p><strong>收货数量：</strong><span style="color: #409eff; font-size: 18px; font-weight: bold">${item.approvedQuantity} 件</span></p>
      <el-divider style="margin: 10px 0" />
      <p><strong>仓库库存变化：</strong></p>
      <p style="padding-left: 20px">
        当前锁定：<span style="color: #e6a23c">${validate.warehouseLocked} 件</span><br/>
        可用库存：<span style="color: #67c23a">${validate.warehouseStock} 件</span><br/>
        释放锁定：<span style="color: #f56c6c">- ${item.approvedQuantity} 件</span>
      </p>
      <p><strong>门店库存变化：</strong></p>
      <p style="padding-left: 20px">
        在途库存：<span style="color: #e6a23c">${currentInTransit} 件</span> → <span style="color: #67c23a">${Math.max(0, currentInTransit - item.approvedQuantity)} 件</span><br/>
        可用库存：<span style="color: #67c23a">${currentAvailable} 件</span> → <span style="color: #409eff; font-weight: bold">${currentAvailable + item.approvedQuantity} 件</span>
      </p>
    </div>`

    await ElMessageBox.confirm(
      content,
      '确认收货',
      { 
        confirmButtonText: '确认收货', 
        cancelButtonText: '取消', 
        type: 'info',
        dangerouslyUseHTMLString: true
      }
    )
    await api.receiveStock(item.id)
    ElMessage.success('收货成功，在途库存已减少，可售库存已增加')
    loadRequests()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '操作失败')
    }
  }
}

onMounted(async () => {
  const [storesRes, productsRes] = await Promise.all([
    api.getStores(),
    api.getProducts()
  ])
  stores.value = storesRes.data.data
  products.value = productsRes.data.data
  loadRequests()
})
</script>
