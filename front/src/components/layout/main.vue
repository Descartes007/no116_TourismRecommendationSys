<template>
    <el-main class="app-main">
      <el-dialog
        title="修改密码"
        :visible.sync="passwordDialogVisible"
        width="30%"
        :before-close="handlePasswordClose">
        <div class="password-form">
          <div class="form-item">
            <span class="form-label">旧密码：</span>
            <el-input placeholder="请输入旧密码" show-password v-model="oldPassword" size="small"></el-input>
          </div>
          <div class="form-item">
            <span class="form-label">新密码：</span>
            <el-input placeholder="请输入新密码" show-password v-model="newPassword" size="small"></el-input>
          </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button size="small" @click="handlePasswordClose">取 消</el-button>
          <el-button size="small" type="primary" @click="passwordSubmit">确 定</el-button>
        </span>
      </el-dialog>
      <keep-alive max=10>
        <router-view :key="fullPath"></router-view>
      </keep-alive>
    </el-main>
</template>

<script>
  import {changePassword} from '../../api/api'
  import { mapState,mapGetters } from 'vuex'
  export default {
    name: "AppMain",
    data() {
      return{
        user: {},
        oldPassword: "",
        newPassword: "",
        passwordDialogVisible: false,
      }
    },
    methods: {
      handlePasswordClose() {
        this.$bus.$emit('password', false)
      },
      passwordSubmit() {
        var param = {
          id: this.user.id,
          password: this.oldPassword,
          newPassword: this.newPassword
        }
        changePassword(param).then(res => {
          if(res.code == 1000) {
            this.$notify.success({
              title: '成功',
              message: "密码修改成功"
            });
            this.$bus.$emit('password', false)
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        })
      }
    },
    computed: {
      fullPath() {
        return this.$route.fullPath;
      },
      ...mapGetters('user',['getUser'])
    },
    mounted() {
      // 监听collapse
      this.$bus.$on('password', res=>{
        this.passwordDialogVisible = res
      })
      this.user = JSON.parse(this.getUser)
    }
 }
</script>

<style lang="scss" scoped>
.app-main {
  padding: 20px;
  background-color: #f5f7fa;
  height: calc(100vh - 60px);
  overflow-y: auto;
  box-sizing: border-box;
  
  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }
  
  &::-webkit-scrollbar-thumb {
    background-color: rgba(16, 24, 44, 0.2);
    border-radius: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background-color: transparent;
  }
}

.password-form {
  padding: 15px 0;
  
  .form-item {
    margin-bottom: 20px;
    
    .form-label {
      display: block;
      margin-bottom: 10px;
      font-weight: 500;
      color: #FFFFFF;
      font-size: 14px;
    }
  }
}

::v-deep .el-dialog {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

::v-deep .el-dialog__header {
  padding: 16px 20px;
  border-bottom: 1px solid #eef0f5;
  background-color: #f9fafc;
}

::v-deep .el-dialog__title {
  color: #FFFFFF;
  font-weight: 600;
  font-size: 16px;
}

::v-deep .el-dialog__body {
  padding: 24px;
}

::v-deep .el-dialog__footer {
  padding: 16px 20px;
  border-top: 1px solid #eef0f5;
  background-color: #f9fafc;
}

::v-deep .el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
  color: #ffffff;
  
  &:hover, &:focus {
    background-color: #66b1ff;
    border-color: #66b1ff;
    color: #ffffff;
  }
}
</style>