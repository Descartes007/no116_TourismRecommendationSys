<template>
    <el-header class="app-header">
      <div class="left-section">
        <div class="breadcrumb-container">
          <span class="page-title">{{ $route.name || '首页' }}</span>
        </div>
      </div>
      <div class="right-section">
        <div class="action-item" @click="full">
          <el-tooltip content="全屏" placement="bottom">
            <i class="el-icon-full-screen"></i>
          </el-tooltip>
        </div>
        <div class="action-item user-dropdown">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-container">
              <img class="user-avatar" :src="$store.state.configure.HOST + user.avatar" alt="用户头像">
              <span class="user-name">{{user.userName}}</span>
              <i class="el-icon-caret-bottom"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="password">
                <i class="el-icon-lock"></i>
                <span>修改密码</span>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>
</template>

<script>
  import { removeToken } from '@/utils/token'
  import { mapState,mapGetters } from 'vuex'
  import { logout } from '@/api/api'
  export default {
    name: "AppHeader",
    data() {
      return{
        user: {
          avatar: "/img/avatar.jpg"
        },
        search: "",
      }
    },
    computed: {
      ...mapGetters('user',['getUser'])
    },
    methods: {
      full() {
        let de = document.documentElement
        if (de.requestFullscreen) {
          de.requestFullscreen()
        } else if (de.mozRequestFullScreen) {
          de.mozRequestFullScreen()
        } else if (de.webkitRequestFullScreen) {
          de.webkitRequestFullScreen()
        }
      },
      handleCommand(command) {
        if(command == "center") {

        } else if(command == "password") {
          //修改密码
          this.$bus.$emit('password', true)
        } else if(command == "setting") {

        }else if(command == "logout") {
          //退出登陆
          logout().then(res => {
            if(res.code == 1000) {
              this.$store.commit('user/setToken', "")
              this.$store.commit('user/setUser', "")
              this.$store.commit('menu/setMenus', [])
              this.$store.commit('menu/setRoutes', [])
              this.$store.commit('menu/setDisplayMenus', [])
              this.$store.commit('menu/setBtnMenus', [])
              this.$store.commit("menu/setFlag",false)
              this.$store.commit('menu/setActiveMenu', "/index")
              removeToken()
              this.$router.push("/login")
              this.$message({
                  message: "退出登录成功",
                  type: 'success'
              });
              setTimeout(function(){
                window.location.reload()
              },1000)
            } else {
              this.$message({
                  message: res.message,
                  type: 'warning'
              });
            }
          })
        }
      },
      
    },
    mounted() {
      this.user = JSON.parse(this.getUser)
    }
 }
</script>

<style scoped lang="scss">
.app-header {
  height: 60px !important;
  padding: 0;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  z-index: 100;
  
  .left-section {
    display: flex;
    align-items: center;
    
    .breadcrumb-container {
      padding-left: 20px;
      
      .page-title {
        font-size: 18px;
        font-weight: 600;
        color: #FFFFFF;
      }
    }
  }
  
  .right-section {
    display: flex;
    align-items: center;
    height: 100%;
    
    .action-item {
      padding: 0 12px;
      height: 100%;
      display: flex;
      align-items: center;
      cursor: pointer;
      transition: background 0.3s;
      
      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }
      
      i {
        font-size: 18px;
        color: #FFFFFF;
      }
    }
    
    .user-dropdown {
      height: 100%;
      padding: 0 15px 0 10px;
      
      .avatar-container {
        display: flex;
        align-items: center;
        
        .user-avatar {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          object-fit: cover;
          margin-right: 10px;
          border: 2px solid #f0f2f5;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        
        .user-name {
          font-size: 14px;
          font-weight: 500;
          color: #FFFFFF;
          margin-right: 6px;
        }
        
        i {
          font-size: 12px;
          color: #FFFFFF;
        }
      }
    }
  }
}

::v-deep .el-dropdown-menu {
  padding: 5px 0;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  .el-dropdown-item {
    padding: 10px 16px;
    font-size: 14px;
    line-height: 1.5;
    
    i {
      margin-right: 8px;
      font-size: 16px;
      color: #FFFFFF;
    }
    
    &:hover {
      background-color: #f0f2f5;
    }
  }
}
</style>