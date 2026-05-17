<template>
  <el-container class="layout-container">
    <el-header class="header">
      <h1>门店补货预测系统</h1>
    </el-header>
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu router default-active="/inventory">
          <el-menu-item index="/inventory">
            <span>库存管理</span>
          </el-menu-item>
          <el-menu-item index="/sale">
            <span>销售出库</span>
          </el-menu-item>
          <el-menu-item index="/suggestion">
            <span>补货建议</span>
          </el-menu-item>
          <el-menu-item index="/request">
            <span>补货申请</span>
          </el-menu-item>
          <el-menu-item index="/alert">
            <span>缺货预警</span>
          </el-menu-item>
          <el-menu-item index="/holiday">
            <span>节假日配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.header {
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.header h1 {
  margin: 0;
  font-size: 24px;
}
.aside {
  background: #f5f7fa;
}
.main {
  background: #fff;
  padding: 20px;
}
</style>
