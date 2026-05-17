import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/inventory'
  },
  {
    path: '/inventory',
    component: () => import('../views/Inventory.vue')
  },
  {
    path: '/sale',
    component: () => import('../views/Sale.vue')
  },
  {
    path: '/suggestion',
    component: () => import('../views/Suggestion.vue')
  },
  {
    path: '/request',
    component: () => import('../views/Request.vue')
  },
  {
    path: '/alert',
    component: () => import('../views/Alert.vue')
  },
  {
    path: '/holiday',
    component: () => import('../views/Holiday.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
