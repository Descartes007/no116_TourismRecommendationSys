<template>
  <div class="user-center">
    <!-- 页面标题区域 -->
    <div class="page-header">
      <div class="header-title">
        <i class="el-icon-user"></i>
        <span>个人中心</span>
      </div>
      <div class="header-desc">管理您的个人信息，包括基本资料、头像和密码设置</div>
    </div>
    
    <div class="profile-container">
      <el-card class="profile-card" shadow="hover">
        <div class="profile-content">
          <!-- 左侧表单区域 -->
          <div class="profile-form">
            <h3 class="section-title">基本资料</h3>
            <el-form :model="user" :rules="rules" ref="ruleForm" label-width="140px">
              <el-form-item label="登录账号" prop="loginAccount">
                <el-input size="small" disabled v-model="user.loginAccount" prefix-icon="el-icon-user-solid"></el-input>
                </el-form-item>
                <el-form-item label="用户名" prop="userName">
                <el-input size="small" v-model="user.userName" prefix-icon="el-icon-edit"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                <el-input size="small" v-model="user.email" prefix-icon="el-icon-message"></el-input>
                </el-form-item>
                <el-form-item label="联系电话" prop="tel">
                <el-input size="small" v-model="user.tel" prefix-icon="el-icon-phone"></el-input>
                </el-form-item>
              <el-form-item label="性别" prop="sex">
                  <el-radio-group v-model="user.sex">
                    <el-radio label="0">男</el-radio>
                    <el-radio label="1">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              
              <div class="system-info">
                <h3 class="section-title">系统信息</h3>
                <el-form-item label="登录IP" prop="loginIp">
                  <el-input size="small" disabled v-model="user.loginIp" prefix-icon="el-icon-position"></el-input>
                </el-form-item>
                <el-form-item label="登录时间" prop="loginDate">
                  <el-input size="small" disabled v-model="user.loginDate" prefix-icon="el-icon-time"></el-input>
                </el-form-item>
                <el-form-item label="上次修改密码时间" prop="pwdUpdateDate">
                  <el-input size="small" disabled v-model="user.pwdUpdateDate" prefix-icon="el-icon-lock"></el-input>
                </el-form-item>
              </div>
              
              <div class="form-actions">
                <el-button type="primary" icon="el-icon-check" @click="submit">保存更改</el-button>
              </div>
            </el-form>
            </div>
          
          <!-- 右侧头像区域 -->
          <div class="profile-avatar">
            <div class="avatar-container">
                  <el-image 
                class="avatar-image"
                    :src="$store.state.configure.HOST + user.avatar" 
                    :preview-src-list="avatar">
                  </el-image>
              <div class="user-name">{{ user.userName || '用户名' }}</div>
              <div class="user-role">{{ user.loginAccount || '管理员' }}</div>
                </div>
            <div class="avatar-actions">
                    <el-upload
                      ref="upload"
                      :action="uploadAvatarUrl()+ '/'+ this.user.id"
                      :show-file-list="false"
                      :before-upload="beforeAvatorUpload"
                      :on-success="handleAvatorSuccess"
                      accept="image/*"
                      >
                <el-button type="primary" plain icon="el-icon-picture-outline-round">修改头像</el-button>
                    </el-upload>
              <el-button type="warning" plain icon="el-icon-key" @click="changePassword">修改密码</el-button>
                </div>
            </div>
        </div>
    </el-card>
    </div>
  </div>
</template>

<script>
  import {mixin} from "../../../minix";
  import {getUser,setUserInfo,setUserAvatar} from '../../../api/api' 
  export default {
    mixins: [mixin],
    data() {
      var checkPhone = (rule, value, callback) => {
          if (!value) {
              return callback(new Error('请输入联系电话'));
          } else {
              const reg = /^1[3|4|5|7|8][0-9]\d{8}$/
              if (reg.test(value)) {
                  callback();
              } else {
                  return callback(new Error('请输入正确的联系电话'));
              }
          }
      };
      return{
        user: {
          avatar: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Fb4a87154-18b6-4163-ac80-f4dc4bf58d09%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1693364028&t=c518c6c4a6ee2009b961f712a57c211f"
        },
        avatar: ["https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Fb4a87154-18b6-4163-ac80-f4dc4bf58d09%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1693364028&t=c518c6c4a6ee2009b961f712a57c211f"],
        rules: {
          userName: [
            { required: true, message: '请输入用户名', trigger: 'blur' },
          ],
          email: [
            { required: true, message: '请输入邮箱地址', trigger: 'blur' },
            { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
          ],
          tel: [
            { required: true, validator:checkPhone, message: '请输入正确的联系电话', trigger: 'blur' },
          ],
        },
      }
    },
    methods: {
      submit() {
        this.$refs["ruleForm"].validate((valid) => {
          if (valid) {
            setUserInfo(this.user).then(res => {
              if(res.code == 1000) {
                this.$message({
                  type: 'success',
                  message: '保存成功!'
                });
                this.getUserInfo()
              } else {
                this.$notify.error({
                  title: '错误',
                  message: res.message
                });
              }
            })
          } else {
            return false;
          }
        });
      },
      getUserInfo() {
        getUser().then(res => {
            if(res.code == 1000) {
              this.user = res.data
              this.user.sex = res.data.sex + ""
              this.avatar[0] = this.$store.state.configure.HOST + this.user.avatar
              this.setUser()
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message
              });
            }
        })
      },
      setUser() {
        this.$store.commit('user/setUser', JSON.stringify(this.user))
      },
      changePassword() {
        //修改密码
        this.$bus.$emit('password', true)
      },
      handleAvatorSuccess(res){
        let _this = this;
        if(res.code == 1000){
            _this.$message({
              type: 'success',
              message: '上传成功!'
            });
            this.getUserInfo()
        }else{
          _this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      },
    },
    created() {
     
    },
    mounted() {
      this.getUserInfo()
    }
 }
</script>

<style lang="scss" scoped>
.user-center {
      width: 100%;
  height: 100%;
  
  /* 页面标题区域 */
  .page-header {
    margin-bottom: 20px;
    background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
    color: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .header-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 5px;
      display: flex;
    align-items: center;
    
    i {
      margin-right: 8px;
      font-size: 20px;
  }
  }
  
  .header-desc {
    font-size: 14px;
    opacity: 0.9;
  }
  
  .profile-container {
    width: 100%;
  }
  
  .profile-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    
    ::v-deep .el-card__body {
      padding: 0;
    }
  }
  
  .profile-content {
    display: flex;
    min-height: 600px;
    
    @media (max-width: 992px) {
      flex-direction: column-reverse;
    }
  }
  
  .profile-form {
    flex: 1;
    padding: 24px;
    border-right: 1px solid #f0f0f0;
    
    @media (max-width: 992px) {
      border-right: none;
      border-top: 1px solid #f0f0f0;
    }
    
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 20px 0;
      padding-bottom: 10px;
      border-bottom: 1px solid #ebeef5;
  }
    
    .system-info {
      margin-top: 30px;
      
      .section-title {
        margin-bottom: 15px;
      }
    }
    
    .form-actions {
      margin-top: 30px;
      text-align: center;
      
      .el-button {
        padding: 12px 25px;
      }
    }
  }
  
  .profile-avatar {
    width: 350px;
    padding: 30px;
    background: #f9fafc;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    @media (max-width: 992px) {
      width: 100%;
    }
    
    .avatar-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-bottom: 30px;
      width: 100%;
    }
    
    .avatar-image {
      width: 180px;
      height: 180px;
      border-radius: 50%;
      object-fit: cover;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
      border: 4px solid white;
      margin-bottom: 20px;
      transition: transform 0.3s;
      
      &:hover {
        transform: scale(1.05);
      }
    }
    
    .user-name {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 5px;
    }
    
    .user-role {
      font-size: 14px;
      color: #909399;
      margin-bottom: 20px;
    }
    
    .avatar-actions {
      display: flex;
      flex-direction: column;
      width: 100%;
      
      .el-button {
        margin-bottom: 15px;
        width: 100%;
      }
    }
  }
}

/* Element UI 样式覆盖 */
::v-deep .el-form-item {
  margin-bottom: 22px;
}

::v-deep .el-input__inner {
  padding-left: 35px;
}

::v-deep .el-form-item__label {
  font-weight: 500;
}

::v-deep .el-input__prefix {
  left: 10px;
}

::v-deep .el-radio {
  margin-right: 15px;
  }
</style>