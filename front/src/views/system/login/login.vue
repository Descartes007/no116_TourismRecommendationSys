<template>
  <div class="container">
    <div class="login-card">
      <div class="card-header">
        <div class="logo-section">
          <div class="logo-icon">🧳</div>
          <div class="login-title">旅游推荐管理系统</div>
          <div class="subtitle">探索世界，管理精彩</div>
        </div>
      </div>
      
      <div class="login-content">
        <el-form :model="loginForm" :rules="rules" ref="loginForm" class="login-form">
          <el-form-item prop="loginAccount">
            <el-input 
              v-model="loginAccount" 
              placeholder="请输入登录账号"
              prefix-icon="el-icon-user">
            </el-input>
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input 
              type="password" 
              v-model="password" 
              placeholder="请输入用户密码"
              prefix-icon="el-icon-lock">
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button class="login-btn" type="primary" @click="login" :loading="loginLoading">
              开 启 旅 程
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import {login,getUser} from '../../../api/api'
export default {
  data() {
    return{
      loginAccount: '',
      password: '',
      loginLoading: false,
      loginForm: {
        loginAccount: '',
        password: ''
      },
      rules: {
        loginAccount: [
          { required: true, message: '请输入登录账号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          this.submitLogin();
        } else {
          return false;
        }
      });
    },
    
    submitLogin() {
      this.loginLoading = true;
      var params = {
        loginAccount: this.loginAccount,
        password: this.password,
        clientType: "admin"
      }
      login(params).then(res => {
        this.loginLoading = false;
        if(res.code == 1000) {
          this.$message({
            message: '登陆成功',
            type: 'success'
          });
          var that = this
          var token = res.data.token
          this.$store.commit('user/setToken', token)
          this.getUserInfo()
          setTimeout(function() {
            that.$router.push("/index")
          },500)
        } else {
          this.$message.error(res.message);
        }
      }).catch(() => {
        this.loginLoading = false;
      })
    },
    
    getUserInfo() {
      getUser().then(res => {
        if(res.code == 1000) {
          this.$store.commit('user/setUser', JSON.stringify(res.data))
        }
      })
    }
  },
  created() {

  },
  mounted() {
    this.loginForm.loginAccount = this.loginAccount;
    this.loginForm.password = this.password;
  },
  watch: {
    'loginAccount': function(val) {
      this.loginForm.loginAccount = val;
    },
    'password': function(val) {
      this.loginForm.password = val;
    },
    'loginForm.loginAccount': function(val) {
      this.loginAccount = val;
    },
    'loginForm.password': function(val) {
      this.password = val;
    }
  }
}
</script>

<style scoped>
.container {
  height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 50%, #43e97b 100%);
  background-image: url("../../../assets/image/2.png");
  background-size: cover;
  background-position: center;
  background-blend-mode: overlay;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.login-card {
  width: 430px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.card-header {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.9), rgba(67, 233, 123, 0.9));
  padding: 30px 30px 25px 30px;
  text-align: center;
  color: white;
}

.login-content {
  padding: 40px 30px 25px 30px;
}

.login-form .el-form-item {
  margin-bottom: 20px;
}

.login-form .el-form-item:last-child {
  margin-bottom: 10;
}

.login-form .el-input__inner {
  height: 45px;
  line-height: 45px;
  border-radius: 8px;
  border: 2px solid rgba(79, 172, 254, 0.2);
  background: #ffffff;
  font-size: 15px;
  transition: all 0.3s ease;
}

.login-form .el-input__inner:focus {
  border-color: #4facfe;
  box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.1);
}

.login-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 50%, #43e97b 100%);
  border: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(79, 172, 254, 0.3);
}

.logo-icon {
  font-size: 50px;
  margin-bottom: 15px;
  animation: bounce 2s infinite;
}

.login-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 15px;
  opacity: 0.9;
  font-weight: 300;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    margin: 0 20px;
  }
  
  .card-header,
  .login-content {
    padding: 25px 25px 20px 25px;
  }
  
  .logo-icon {
    font-size: 46px;
  }
  
  .login-title {
    font-size: 22px;
  }
  
  .subtitle {
    font-size: 14px;
  }
}
</style>
