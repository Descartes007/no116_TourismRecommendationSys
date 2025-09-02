import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './permission'
import store from './store'
import elementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import VueQuillEditor from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'
import axios from 'axios'

// 导入自定义组件
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'

// 注册富文本编辑器
Vue.use(VueQuillEditor)

Vue.config.productionTip = false
Vue.use(elementUI)

// 全局注册组件
Vue.component('RightToolbar', RightToolbar)
Vue.component('Pagination', Pagination)

// 注册axios
Vue.prototype.$axios = axios
axios.defaults.baseURL = 'http://localhost:8080'

// 添加请求拦截器
axios.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token');
    if (token) {
      // 后端可能接受不同的header名称，这里增加常见的token header
      config.headers['Authorization'] = token;
      config.headers['x_access_token'] = token;
      config.headers['token'] = token;
    }
    return config;
  },
  error => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 添加响应拦截器
axios.interceptors.response.use(
  response => {
    console.log('API响应:', response);
    // 当API返回的数据不符合预期格式时进行适配
    const res = response.data;
    if (res) {
      // 处理返回结果格式
      if (res.code !== undefined && res.success === undefined) {
        // 后端返回的是{code: xxx, message: xxx, data: xxx}格式
        return {
          success: res.code === 1000, // 假设1000是成功码
          code: res.code,
          message: res.message,
          data: res.data
        };
      }
      return res;
    }
    return {
      success: false,
      message: '返回数据格式错误',
      data: null
    };
  },
  error => {
    console.error('响应拦截器错误:', error);

    // 处理登录过期问题 (状态码1011)
    if (error.response && error.response.status === 1011) {
      Vue.prototype.$message.error('登录已过期，请重新登录');
      // 清除token和用户信息
      store.dispatch('user/logout').then(() => {
        store.commit('menu/setActiveMenuArrary', []);
        store.commit('menu/setActiveMenu', "/index");
        router.push('/login');
      });
      return Promise.reject(error);
    }

    Vue.prototype.$message.error('网络或服务器错误，请稍后重试');
    return Promise.reject(error);
  }
);

//EventBus事件中转
Vue.prototype.$bus = new Vue()

// 添加全局错误处理
Vue.prototype.$modal = {
  msgSuccess: function (message) {
    Vue.prototype.$message.success(message);
  },
  msgError: function (message) {
    Vue.prototype.$message.error(message);
  },
  confirm: function (message) {
    return Vue.prototype.$confirm(message, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
  }
};

// 添加全局上传URL配置
Vue.prototype.$uploadUrl = 'http://localhost:8080/common/uploadImg';

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
