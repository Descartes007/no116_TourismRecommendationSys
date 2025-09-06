<template>
  <div class="app-wrapper">
    <el-asside></el-asside>
    <div class="main-container">
      <el-header></el-header>
      <el-main></el-main>
    </div>
  </div>
</template>

<script>
  import { mapState } from 'vuex'
  import { verPassword } from '@/api/api'
  import elAsside from "../../components/layout/aside"
  import elMain from "../../components/layout/main"
  import elHeader from "../../components/layout/header"

  export default {
    name: "LayoutIndex",
    data() {
      return{
        pass: "",
      }
    },
    components:{
      elAsside,
      elMain,
      elHeader
    },
    computed: {
      ...mapState({
          activeMenu: state => state.menu.activeMenu
      })
    },
    mounted() {
      // console.clear()
    }
 }
</script>

<style lang="scss" scoped>
.app-wrapper {
  position: relative;
  height: 100%;
  width: 100%;
  display: flex;
  background-color: #f5f7fa;
}

.main-container {
  min-height: 100%;
  margin-left: 260px;
  position: relative;
  width: calc(100% - 260px);
  overflow: hidden;
}

::v-deep .el-menu-item {
  min-width: 0;
}

::v-deep .sidebar-container.el-aside {
  transition: width 0.28s;
}

::v-deep .hideSidebar {
  .main-container {
    margin-left: 64px;
    width: calc(100% - 64px);
  }
}

// 响应式适配
@media screen and (max-width: 768px) {
  .main-container {
    margin-left: 0;
    width: 100%;
  }
}
</style>