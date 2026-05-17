<template>
  <div>
    <h2>销售出库</h2>
    <el-card>
      <el-form :model="saleForm" label-width="100px">
        <el-form-item label="门店">
          <el-select v-model="saleForm.storeId" placeholder="请选择门店">
            <el-option v-for="store in stores" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="saleForm.productId" placeholder="请选择商品">
            <el-option v-for="product in products" :key="product.id" :label="product.name" :value="product.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="销售数量">
          <el-input-number v-model="saleForm.quantity" :min="1" :max="100" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitSale">确认销售</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-divider />
    <el-alert title="提示" type="info">
      销售后将实时扣减门店库存，并更新销量统计
    </el-alert>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const stores = ref([])
const products = ref([])
const saleForm = reactive({
  storeId: '',
  productId: '',
  quantity: 1
})

const loadStores = async () => {
  const res = await api.getStores()
  stores.value = res.data.data
}

const loadProducts = async () => {
  const res = await api.getProducts()
  products.value = res.data.data
}

const submitSale = async () => {
  if (!saleForm.storeId || !saleForm.productId) {
    ElMessage.warning('请选择门店和商品')
    return
  }
  try {
    await api.processSale(saleForm)
    ElMessage.success('销售成功，库存已更新')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '销售失败')
  }
}

onMounted(() => {
  loadStores()
  loadProducts()
})
</script>
