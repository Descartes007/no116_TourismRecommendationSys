<template>
    <el-aside width="260px" class="sidebar-container">
        <div class="sidebar-logo-container gradient-bg" style="width: 260px">
            <div class="logo-wrapper">
                <img class="logo-img" src="../../assets/image/new-logo.svg"/>
                <h1 class="logo-title">旅游信息推荐管理系统</h1>
            </div>
            <div class="logo-divider"></div>
        </div>
        <el-scrollbar wrap-class="scrollbar-wrapper">
            <el-menu
                @select="handleSelect"
                :collapse="false"
                :collapse-transition="false"
                class="el-menu-vertical"
                :unique-opened="true"
                background-color="#FFFFFF"
                text-color="#a6b0cf"
                active-text-color="#ffffff"
                :default-active="this.$route.path + '@' + this.$route.name"
                >
                    <el-menu-item index="/index@首页" class="menu-item">
                        <i class="el-icon-location"></i>
                        <span>首页</span>
                    </el-menu-item>
                    <!-- 景点管理 -->
                    <el-menu-item index="/attractions@景点管理" class="menu-item">
                        <i class="el-icon-s-flag"></i>
                        <span>景点管理</span>
                    </el-menu-item>
                    <!-- 线路管理 -->
                    <el-menu-item index="/line@线路管理" class="menu-item">
                        <i class="el-icon-s-promotion"></i>
                        <span>线路管理</span>
                    </el-menu-item>
                    <!-- 酒店管理 -->
                    <el-menu-item index="/hotel@酒店管理" class="menu-item">
                        <i class="el-icon-suitcase"></i>
                        <span>酒店管理</span>
                    </el-menu-item>
                    <el-submenu index="2" class="submenu-item">
                        <template slot="title">
                            <i class="el-icon-ice-cream-round"></i>
                            <span>美食管理</span>
                        </template>
                        <el-menu-item index="/restaurant@餐厅管理" class="submenu-child">
                            <i class="el-icon-ice-cream"></i>
                            <span>餐厅管理</span>
                        </el-menu-item>
                        <el-menu-item index="/dish@菜品管理" class="submenu-child">
                            <i class="el-icon-coffee"></i>
                            <span>菜品管理</span>
                        </el-menu-item>
                    </el-submenu>
                    <el-menu-item index="/forum@资讯管理" class="menu-item">
                        <i class="el-icon-chat-dot-square"></i>
                        <span>资讯管理</span>
                    </el-menu-item>
                    <el-menu-item index="/order@景点预约" class="menu-item">
                        <i class="el-icon-tickets"></i>
                        <span>景点预约</span>
                    </el-menu-item>
                    <el-menu-item index="/orderHotel@酒店预约" class="menu-item">
                        <i class="el-icon-s-ticket"></i>
                        <span>酒店预约</span>
                    </el-menu-item>
                    <el-menu-item index="/rotations@轮播图管理" class="menu-item">
                        <i class="el-icon-picture"></i>
                        <span>轮播图管理</span>
                    </el-menu-item>
                    <el-menu-item index="/lineOrder@线路订单管理" class="menu-item">
                        <i class="el-icon-truck"></i>
                        <span>线路订单管理</span>
                    </el-menu-item>
                    <el-menu-item index="/center@个人中心" class="menu-item">
                        <i class="el-icon-user"></i>
                        <span>个人中心</span>
                    </el-menu-item>
                    <el-submenu index="1" class="submenu-item">
                        <template slot="title">
                            <i class="el-icon-user-solid"></i>
                            <span>用户管理</span>
                        </template>
                        <el-menu-item index="/admin@管理员管理" class="submenu-child">
                            <i class="el-icon-s-custom"></i>
                            <span>管理员管理</span>
                        </el-menu-item>
                        <el-menu-item index="/user@普通用户管理" class="submenu-child">
                            <i class="el-icon-user"></i>
                            <span>普通用户管理</span>
                        </el-menu-item>
                    </el-submenu>
            </el-menu>
        </el-scrollbar>
    </el-aside>
</template>

<script>
  import { mapState } from 'vuex'
  export default {
    name: "asside",
    data() {
      return{
        textColor: "#a6b0cf"
      }
    },
    computed: {
      ...mapState({
          activeMenu: state => state.menu.activeMenu
      })
    },
    methods: {
        handleSelect(key) {
            var menu = key.split("@")
            this.$router.push({
                path: menu[0],
            })
            this.$store.commit('menu/setActiveMenu', menu[0])
        }
    },
    mounted() {
        this.$store.commit('menu/setActiveMenu', this.$route.path)
    },
 }
</script>

<style scoped lang="scss">
.sidebar-container {
    background-color: #FFFFFF;
    height: 100%;
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    z-index: 1001;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.4);
    overflow: hidden;
    width: 260px !important;
}

.sidebar-logo-container {
    position: relative;
    height: 64px;
    padding: 0 16px;
    overflow: hidden;
    background-color: #FFFFFF;
    transition: all 0.3s;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    .logo-wrapper {
        display: flex;
        align-items: center;
        height: 100%;
        
        .logo-img {
            width: 36px;
            height: 36px;
            border-radius: 10px;
            margin-right: 12px;
            transition: all 0.3s ease;
            filter: drop-shadow(0 2px 8px rgba(59, 130, 246, 0.3));
            
            &:hover {
                transform: scale(1.05) rotate(5deg);
                filter: drop-shadow(0 4px 12px rgba(59, 130, 246, 0.5));
            }
        }
        
        .logo-title {
            display: inline-block;
            margin: 0;
            color: #fff;
            font-weight: 600;
            font-size: 16px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
    }
}

.scrollbar-wrapper {
    height: calc(100% - 64px) !important;
    overflow-x: hidden !important;
}

.el-scrollbar__view {
    height: 100%;
}

.el-menu {
    border: none;
    height: 100%;
    width: 100% !important;
    background-color: #FFFFFF !important;
}

.el-menu-vertical {
    width: 100% !important;
    
    &:not(.el-menu--collapse) {
        width: 260px;
    }
}

.menu-item, .submenu-item {
    margin: 6px 0;
    border-radius: 6px;
    margin-left: 10px;
    margin-right: 10px;
    
    &:hover {
        background-color: rgba(64, 158, 255, 0.1) !important;
    }
}

/* 修复图标与文字对齐问题 */
.el-menu-item, .el-submenu__title {
    display: flex !important;
    align-items: center !important;
    height: 50px !important;
    line-height: 50px !important;
    padding: 0 16px !important;
    
    i {
        margin-right: 10px;
        font-size: 18px;
        width: 24px;
        text-align: center;
        vertical-align: middle;
        line-height: 18px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        
        i {
            margin-right: 10px;
            font-size: 18px;
            width: 24px;
            text-align: center;
            vertical-align: middle;
            line-height: 18px;
        }
        
        span {
            vertical-align: middle;
            line-height: normal;
            font-size: 14px;
            letter-spacing: 0.3px;
        }
        
        &:hover {
            background-color: rgba(64, 158, 255, 0.1) !important;
            
            i {
                transform: scale(1.1);
                color: #60a5fa !important;
            }
            
            span {
                color: #ffffff !important;
            }
        }
    }
}

/* 特别处理美食管理菜单，确保图标对齐 */
.food-submenu {
    .el-submenu__title {
        padding: 0 16px !important;
        
        .menu-icon-wrapper {
            display: inline-flex;
            width: 24px;
            justify-content: center;
            margin-right: 10px;
        }
    }
}

.submenu-child {
    padding-left: 48px !important;
    
    &:hover {
        background-color: rgba(64, 158, 255, 0.1) !important;
    }
    
    i {
        margin-right: 10px;
        width: 24px;
        text-align: center;
        line-height: 18px;
    }
}

.el-menu--collapse .el-submenu__title span {
    height: 0;
    width: 0;
    overflow: hidden;
    visibility: hidden;
    display: inline-block;
}

.el-menu-item.is-active {
    background: linear-gradient(90deg, #3ec6e0 0%, #409EFF 100%) !important;
    color: #fff !important;
    box-shadow: 0 0 12px rgba(64, 158, 255, 0.25);
}

.menu-item:hover, .submenu-item:hover, .el-menu-item:hover, .el-submenu__title:hover {
    background: linear-gradient(90deg, #3ec6e0 0%, #6ee7b7 100%) !important;
    color: #fff !important;
}

.el-submenu.is-active {
    .el-submenu__title {
        color: #409EFF !important;
        
        i {
            color: #409EFF !important;
        }
    }
}

.el-submenu .el-menu-item {
    min-width: 0 !important;
    background-color: #FFFFFF !important;
}

/* 修复折叠状态下的图标居中问题 */
.el-menu--collapse {
    .el-menu-item, .el-submenu__title {
        padding: 0 !important;
        text-align: center;
        justify-content: center;
        
        i {
            margin: 0 !important;
            width: 64px !important;
            display: flex;
            justify-content: center;
        }
    }
}

/* 自定义滚动条样式 */
::-webkit-scrollbar {
    width: 6px;
    height: 6px;
}

::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 20px;
}

::-webkit-scrollbar-track {
    background-color: #FFFFFF !important;
}

/* 强制修复美食管理菜单对齐问题 */
::v-deep .el-submenu.submenu-item > .el-submenu__title {
    padding: 0 16px !important;
}

::v-deep .el-submenu.submenu-item > .el-submenu__title > i {
    margin-left: 0 !important;
}

/* 确保所有菜单项和子菜单使用相同的背景色 */
::v-deep .el-menu,
::v-deep .el-menu--vertical,
::v-deep .el-menu-item,
::v-deep .el-submenu,
::v-deep .el-submenu__title,
::v-deep .el-menu--popup {
    background-color: #FFFFFF !important;
}

::v-deep .el-menu--popup-right-start {
    background-color: #FFFFFF !important;
    
    .el-menu-item {
        background-color: #FFFFFF !important;
        
        &:hover {
            background-color: rgba(64, 158, 255, 0.1) !important;
        }
    }
}

/* 确保滚动区域也使用相同的背景色 */
::v-deep .scrollbar-wrapper {
    background-color: #FFFFFF !important;
}

::v-deep .el-scrollbar {
    background-color: #FFFFFF !important;
}

/* 修改滚动条轨道的背景色，使其与侧边栏背景色一致 */
::-webkit-scrollbar-track {
    background-color: #FFFFFF !important;
}

.gradient-bg {
    background: linear-gradient(90deg, #3ec6e0 0%, #6ee7b7 100%);
}
.logo-divider {
    height: 2px;
    width: 80%;
    margin: 0 auto;
    background: linear-gradient(90deg, #3ec6e0 0%, #6ee7b7 100%);
    opacity: 0.3;
    border-radius: 1px;
}

.global-footer {
  width: 100%;
  text-align: center;
  position: fixed;
  left: 0;
  bottom: 0;
  background: linear-gradient(90deg, #3ec6e0 0%, #6ee7b7 100%);
  color: #fff;
  font-size: 16px;
  font-style: italic;
  font-weight: bold;
  letter-spacing: 1.5px;
  padding: 12px 0 10px 0;
  z-index: 999;
  box-shadow: 0 -2px 12px rgba(59, 130, 246, 0.08);
}
</style>