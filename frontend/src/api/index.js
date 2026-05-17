import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8002',
  timeout: 10000
})

export default {
  getStores() {
    return api.get('/api/stores')
  },
  getProducts() {
    return api.get('/api/products')
  },
  getHolidays() {
    return api.get('/api/holidays')
  },
  getAlerts() {
    return api.get('/api/alerts')
  },
  getStoreInventory(storeId) {
    return api.get(`/api/inventory/store/${storeId}`)
  },
  getWarehouseInventory() {
    return api.get('/api/inventory/warehouse')
  },
  processSale(data) {
    return api.post('/api/sale', data)
  },
  getSuggestions(storeId) {
    return api.get('/api/replenishment/suggestions', { params: { storeId } })
  },
  createRequest(data) {
    return api.post('/api/replenishment/request', data)
  },
  getRequests(storeId, status) {
    return api.get('/api/replenishment/requests', { params: { storeId, status } })
  },
  approveRequest(requestId, approvedQuantity) {
    return api.put(`/api/replenishment/approve/${requestId}`, { approvedQuantity })
  },
  rejectRequest(requestId, remark) {
    return api.put(`/api/replenishment/reject/${requestId}`, { remark })
  },
  receiveStock(requestId) {
    return api.put(`/api/replenishment/receive/${requestId}`)
  },
  validateRequest(productId, quantity) {
    return api.get('/api/replenishment/validate', { params: { productId, quantity } })
  }
}
