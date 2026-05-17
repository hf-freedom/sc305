<template>
  <div>
    <h2>库存管理</h2>
    <el-form :inline="true">
      <el-form-item label="选择门店">
        <el-select v-model="selectedStore" placeholder="请选择门店" @change="loadInventory">
          <el-option v-for="store in stores" :key="store.id" :label="store.name" :value="store.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="门店库存" name="store">
        <el-table :data="storeInventory" border style="width: 100%">
          <el-table-column prop="productId" label="商品ID" width="100" />
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="availableStock" label="可用库存" width="120">
            <template #default="scope">
              <span :style="{ color: scope.row.availableStock < 20 ? 'red' : 'green' }">
                {{ scope.row.availableStock }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="inTransitStock" label="在途库存" width="120" />
          <el-table-column prop="last7DaysSales" label="近7天销量" width="120" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="仓库库存" name="warehouse">
        <el-table :data="warehouseInventory" border style="width: 100%">
          <el-table-column prop="productId" label="商品ID" width="100" />
          <el-table-column prop="productName" label="商品名称" />
          <el-table-column prop="availableStock" label="可用库存" width="120" />
          <el-table-column prop="lockedStock" label="锁定库存" width="120" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const selectedStore = ref('')
const stores = ref([])
const storeInventory = ref([])
const warehouseInventory = ref([])
const activeTab = ref('store')
const products = ref([])

const loadStores = async () => {
  const res = await api.getStores()
  stores.value = res.data.data
  if (stores.value.length > 0) {
    selectedStore.value = stores.value[0].id
    loadInventory()
  }
}

const loadProducts = async () => {
  const res = await api.getProducts()
  products.value = res.data.data
}

const loadInventory = async () => {
  if (!selectedStore.value) return
  const res = await api.getStoreInventory(selectedStore.value)
  storeInventory.value = res.data.data.map(item => {
    const product = products.value.find(p => p.id === item.productId)
    return {
      ...item,
      productName: product ? product.name : item.productId
    }
  })
}

const loadWarehouseInventory = async () => {
  const res = await api.getWarehouseInventory()
  warehouseInventory.value = res.data.data.map(item => {
    const product = products.value.find(p => p.id === item.productId)
    return {
      ...item,
      productName: product ? product.name : item.productId
    }
  })
}

onMounted(async () => {
  await loadProducts()
  await loadStores()
  await loadWarehouseInventory()
})
</script>
