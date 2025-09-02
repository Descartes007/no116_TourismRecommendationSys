import axios from 'axios'
import store from '@/store'
import { Message, MessageBox } from 'element-ui'  //导入element-ui组件库

// 创建axios的对象
const instance = axios.create({
    baseURL: "http://localhost:8080",  //配置固定域名
    timeout: 5000
})

// 请求拦截
// 所有的网络请求都会走这个方法,可以在请求添加自定义内容
instance.interceptors.request.use(
    function (config) {
        console.log('发送请求:', config.url, config.method, config.data || config.params);

        if (config.method === 'post') {
            // 检查POST请求的数据结构
            console.log('POST请求数据类型:', typeof config.data, Array.isArray(config.data) ? 'Array' : 'Object');
            config.data = {
                ...config.data,
                _t: Date.parse(new Date()) / 1000 // 时间戳
            }
        } else if (config.method === 'get') {
            config.params = {
                random: Math.random(), // 随机数
                ...config.params
            }
        }
        config.headers.x_access_token = window.localStorage.getItem("token") // 请求头添加token值
        console.log('完整请求配置:', JSON.stringify({
            url: config.url,
            method: config.method,
            headers: config.headers,
            data: config.data,
            params: config.params
        }));
        return config
    },
    function (err) {
        console.error('请求错误:', err);
        return Promise.reject(err)
    }
)

// 响应拦截
// 此处可以根据服务器返回的状态码做相应的数据
instance.interceptors.response.use(
    function (response) {
        console.log('接收响应:', response.config.url, response.status, response.data);
        // 检查响应数据结构
        if (response.data) {
            console.log('响应数据code:', response.data.code);
            if (response.data.data) {
                const dataType = typeof response.data.data;
                console.log('响应数据类型:', dataType);

                // 如果是分页数据，检查records字段
                if (dataType === 'object' && response.data.data.records) {
                    console.log('分页数据records长度:', response.data.data.records.length);
                }
            }
        }

        const res = response
        if (res.status === 1011 || res.data.code == 1008 || res.data.code == 1006) {
            MessageBox.alert('系统登陆已过期，请重新登录', '错误', {
                confirmButtonText: '确定',
                type: 'error'
            }).then(() => {
                store.dispatch('user/logout').then(() => {
                    store.commit('menu/setActiveMenuArrary', [])
                    store.commit('menu/setActiveMenu', "/index")
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else if (res.data.code == 1009) {
            MessageBox.alert('该账号已被锁定', '错误', {
                confirmButtonText: '确定',
                type: 'error'
            }).then(() => {
                store.dispatch('user/logout').then(() => {
                    store.commit('menu/setActiveMenuArrary', [])
                    store.commit('menu/setActiveMenu', "/index")
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else if (res.status != 200) {
            MessageBox.alert('系统内部错误，请联系管理员维护', '错误', {
                confirmButtonText: '确定',
                type: 'error'
            })
            return Promise.reject('error')
        } else {
            return res.data
        }
    },
    function (err) {
        console.error('响应错误:', err);

        if (err.response && err.response.status == 1011) {
            MessageBox.alert('系统登陆已过期，请重新登录', '错误', {
                confirmButtonText: '确定',
                type: 'error'
            }).then(() => {
                store.dispatch('user/logout').then(() => {
                    store.dispatch('user/logout').then(() => {
                        store.commit('menu/setActiveMenuArrary', [])
                        store.commit('menu/setActiveMenu', "/index")
                        location.reload()
                    })
                    location.reload()
                })
            })
            return Promise.reject('error')
        } else {
            MessageBox.alert('系统内部错误，请联系管理员维护', '错误', {
                confirmButtonText: '确定',
                type: 'error'
            })
            return Promise.reject('error')
        }
    }
)

// 封装get和post请求
export function get(url, params) {
    return instance.get(url, { params })
}

export function post(url, data) {
    return instance.post(url, data)
}

// 以表单数据方式提交
export function postForm(url, data) {
    // 将对象转换为URLSearchParams格式
    const formData = new URLSearchParams()
    for (const key in data) {
        if (data.hasOwnProperty(key)) {
            formData.append(key, data[key])
        }
    }

    console.log('提交表单数据:', url, '参数:', Object.fromEntries(formData));

    return instance.post(url, formData, {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
}

export default instance;