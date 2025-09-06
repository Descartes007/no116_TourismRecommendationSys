<template>
  <div class="dish-management">
    <!-- 页面标题区域已删除 -->

    <el-card class="info-card" v-if="restaurantId">
      <div slot="header" class="clearfix">
        <div class="restaurant-header">
          <span class="restaurant-title">当前餐厅：{{ restaurantName }}</span>
          <el-button type="primary" size="small" plain icon="el-icon-back" @click="goBack" class="back-btn">返回餐厅列表</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="warning-card" v-else>
      <div slot="header" class="clearfix">
        <span class="warning-title"><i class="el-icon-warning-outline"></i> 所有餐厅的菜品</span>
      </div>
      <div class="warning-message">
        注意：当前显示所有餐厅的菜品，建议从餐厅管理页面进入本页面获得更好的使用体验。
      </div>
    </el-card>

    <!-- 搜索区域 -->
    <div class="search-panel" v-show="showSearch">
      <el-form :model="queryParams" ref="queryForm" class="search-form" label-width="80px">
        <el-row :gutter="20" class="search-row">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">菜品名称:</span>
              <el-input 
                v-model="queryParams.name" 
                placeholder="请输入菜品名称" 
                clearable 
                prefix-icon="el-icon-dish"
                size="small"
                @keyup.enter.native="handleQuery" />
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">菜品类型:</span>
              <el-input 
                v-model="queryParams.dishType" 
                placeholder="请输入菜品类型" 
                clearable 
                prefix-icon="el-icon-food"
                size="small"
                @keyup.enter.native="handleQuery" />
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">是否特色:</span>
              <el-select 
                v-model="queryParams.isSpecial" 
                placeholder="是否特色菜" 
                clearable 
                size="small"
                class="status-select">
                <el-option v-for="dict in specialOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6" v-if="!restaurantId">
            <div class="search-item">
              <span class="search-label">所属餐厅:</span>
              <el-select 
                v-model="queryParams.restaurantId" 
                placeholder="所属餐厅" 
                clearable 
                size="small"
                class="status-select">
                <el-option
                  v-for="item in restaurantOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </div>
          </el-col>
        </el-row>
        <div class="search-buttons">
          <el-button size="small" type="primary" icon="el-icon-search" @click="handleQuery">查 询</el-button>
          <el-button size="small" icon="el-icon-refresh" @click="resetQuery">重 置</el-button>
        </div>
      </el-form>
    </div>

    <!-- 操作工具栏 -->
    <div class="table-panel">
      <div class="table-toolbar">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增菜品</el-button>
        <el-button type="success" size="small" icon="el-icon-edit" :disabled="single" @click="handleUpdate">编辑菜品</el-button>
        <el-button type="danger" size="small" icon="el-icon-delete" :disabled="multiple" @click="handleDelete">删除菜品</el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <div class="table-panel">
      <el-table 
        v-loading="loading" 
        :data="dishList" 
        @selection-change="handleSelectionChange"
        class="data-table"
        border
        highlight-current-row
        stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="菜品名称" align="center" prop="name" min-width="120" show-overflow-tooltip />
        <el-table-column label="菜品图片" align="center" width="120">
          <template slot-scope="scope">
            <el-image 
              v-if="scope.row.images" 
              :src="getFirstImage(scope.row.images)" 
              style="width: 80px; height: 60px; object-fit: cover;border-radius: 4px;"
              :preview-src-list="getImageList(scope.row.images)"
              fit="cover">
              <div slot="error" class="image-placeholder">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-else class="image-placeholder">
              <i class="el-icon-picture-outline"></i>
              <span>无图片</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="所属餐厅" align="center" prop="restaurantName" v-if="!restaurantId" min-width="120" show-overflow-tooltip />
        <el-table-column label="价格" align="center" prop="price" width="100">
          <template slot-scope="scope">
            <span class="price-tag">{{ scope.row.price }} 元</span>
          </template>
        </el-table-column>
        <el-table-column label="菜品类型" align="center" prop="dishType" width="120" />
        <el-table-column label="是否特色菜" align="center" prop="isSpecial" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isSpecial === 1 ? 'success' : 'info'" effect="dark">
              {{ scope.row.isSpecial === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="180" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button size="mini" type="primary" plain icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-empty 
        v-if="!loading && dishList.length === 0" 
        description="暂无菜品数据" 
        :image-size="200">
        <el-button type="primary" @click="handleAdd">添加菜品</el-button>
      </el-empty>

      <div class="pagination-container">
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </div>

    <!-- 添加或修改菜品对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body class="dish-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜品名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入菜品名称" prefix-icon="el-icon-food" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格(元)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜品类型" prop="dishType">
              <el-input v-model="form.dishType" placeholder="请输入菜品类型" prefix-icon="el-icon-menu" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否特色菜" prop="isSpecial">
              <el-radio-group v-model="form.isSpecial">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属餐厅" prop="restaurantId" v-if="!restaurantId">
          <el-select v-model="form.restaurantId" placeholder="请选择餐厅" style="width: 100%">
            <el-option
              v-for="item in restaurantOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品介绍" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入菜品介绍" :rows="3" />
        </el-form-item>
        
        <el-form-item label="菜品图片" prop="images">
          <div class="upload-container">
            <el-upload
              class="upload-demo"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :with-credentials="true"
              :on-preview="handlePictureCardPreview"
              :on-remove="handleRemove"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
              :file-list="fileList"
              list-type="picture-card"
              multiple
              :before-upload="beforeUpload"
              name="file"
              accept=".jpg,.jpeg,.png,.gif"
              :data="uploadParams"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <div class="el-upload__tip">
              <span>只能上传jpg/png等图片文件，且不超过5MB</span>
              <el-button size="mini" type="text" @click="addImageUrl">手动输入图片链接</el-button>
            </div>
          </div>
          <el-dialog :visible.sync="dialogVisible" append-to-body class="image-preview-dialog">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Dish",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 菜品表格数据
      dishList: [],
      // 保存所有菜品数据(用于前端分页)
      allDishList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 特色菜选项
      specialOptions: [
        { value: 1, label: "是" },
        { value: 0, label: "否" }
      ],
      // 餐厅选项列表
      restaurantOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: undefined,
        dishType: undefined,
        isSpecial: undefined,
        restaurantId: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "菜品名称不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "价格不能为空", trigger: "blur" }
        ],
        dishType: [
          { required: true, message: "菜品类型不能为空", trigger: "blur" }
        ],
        restaurantId: [
          { required: true, message: "所属餐厅不能为空", trigger: "change" }
        ]
      },
      // 上传图片相关
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: [],
      // 当前选择的餐厅ID（从路由参数中获取）
      restaurantId: undefined,
      restaurantName: undefined,
      uploadUrl: '',
      uploadHeaders: {},
      uploadParams: {},
    };
  },
  created() {
    console.log('菜品管理组件created钩子执行');
    console.log('路由参数:', this.$route.query);
    
    // 从路由参数中获取餐厅ID
    try {
      if (this.$route.query && this.$route.query.restaurantId) {
        this.restaurantId = this.$route.query.restaurantId;
        this.restaurantName = this.$route.query.restaurantName || '未命名餐厅';
        this.queryParams.restaurantId = this.restaurantId;
        
        console.log('设置餐厅参数:', {
          id: this.restaurantId,
          name: this.restaurantName
        });
        
        // 动态设置页面标题
        document.title = `${this.restaurantName} - 菜品管理`;
      } else {
        console.warn('未获取到餐厅ID参数');
        this.restaurantId = null;
        this.restaurantName = '所有餐厅';
        this.queryParams.restaurantId = null;
      }
    } catch (error) {
      console.error('解析路由参数错误:', error);
      this.$message.error('页面参数错误，可能无法正确显示数据');
    }
    
    // 初始化上传配置
    this.initUploadConfig();
    
    // 先获取餐厅列表，再获取菜品列表
    this.getRestaurantList().then(() => {
      this.getList();
    }).catch(err => {
      console.error('初始化数据失败:', err);
      this.getList(); // 即使餐厅列表获取失败，也尝试获取菜品列表
    });
  },
  methods: {
    /** 初始化上传配置 */
    initUploadConfig() {
      // 尝试使用全局配置的上传URL
      this.uploadUrl = this.$uploadUrl || 'http://localhost:8080/common/uploadImg';
      
      // 配置上传请求头
      const token = localStorage.getItem('token');
      this.uploadHeaders = {
        'Authorization': token,
        'x_access_token': token,
        'token': token,
        // 禁用缓存，避免某些浏览器的缓存问题
        'Cache-Control': 'no-cache',
        'Pragma': 'no-cache'
      };
      
      // 上传附加参数
      this.uploadParams = {
        type: 'dish', // 图片类型标识
        token: token // 某些后端可能需要通过请求参数获取token
      };
      
      console.log('图片上传配置已初始化:', {
        URL: this.uploadUrl,
        headers: this.uploadHeaders,
        params: this.uploadParams
      });
    },
    /** 获取餐厅列表 */
    getRestaurantList() {
      console.log('开始获取餐厅列表...');
      return new Promise((resolve, reject) => {
        this.$axios.get('/restaurant/list')
          .then(res => {
            console.log('餐厅列表返回原始数据:', res);
            
            // 兼容处理API返回数据格式
            let success = false;
            let restaurants = [];
            
            // 处理不同的返回格式
            if (res && typeof res === 'object') {
              if (res.success !== undefined) {
                // 格式1: {success: true, data: [...]}
                success = res.success;
                restaurants = res.data || [];
              } else if (res.code !== undefined) {
                // 格式2: {code: 1000, data: [...]}
                success = res.code === 1000;
                restaurants = res.data || [];
              } else if (Array.isArray(res)) {
                // 格式3: 直接返回数组
                success = true;
                restaurants = res;
              } else if (res.data !== undefined) {
                // 格式4: {data: [...]}
                success = true;
                if (Array.isArray(res.data)) {
                  restaurants = res.data;
                } else if (res.data && res.data.data && Array.isArray(res.data.data)) {
                  // 处理 {data: {data: [...]}} 的情况
                  restaurants = res.data.data;
                }
              }
            }
            
            // 确保restaurants始终是一个数组
            if (!Array.isArray(restaurants)) {
              console.warn('API返回的餐厅数据格式异常，已处理为空数组', restaurants);
              restaurants = [];
            }
            
            // 过滤掉没有id或者name的餐厅，并且确保只显示状态为1(启用)的餐厅
            this.restaurantOptions = restaurants
              .filter(item => item && item.id)
              .map(item => ({
                id: item.id,
                name: item.name || '未命名餐厅'
              }));
            
            console.log('处理后的餐厅选项:', this.restaurantOptions);
            
            if (this.restaurantOptions.length === 0) {
              this.$message.warning('没有找到可用的餐厅，请先在餐厅管理中添加餐厅');
            }
            
            resolve(this.restaurantOptions);
          })
          .catch(err => {
            console.error('获取餐厅列表错误:', err);
            this.$message.error('获取餐厅列表失败，将无法选择餐厅');
            this.restaurantOptions = [];
            reject(err);
          });
      });
    },
    /** 查询菜品列表 */
    getList(params) {
      this.loading = true;
      
      // 确保有餐厅ID参数
      if (this.restaurantId) {
        this.queryParams.restaurantId = this.restaurantId;
      }
      
      // 处理分页回调参数
      if (params) {
        if (params.page) this.queryParams.pageNum = params.page;
        if (params.limit) this.queryParams.pageSize = params.limit;
      }
      
      console.log('开始获取菜品列表，查询参数:', this.queryParams);
      
      this.$axios.get('/dish/list', { params: this.queryParams })
        .then(res => {
          console.log('菜品数据返回原始数据:', res);
          // 兼容处理API返回数据格式
          let success = false;
          let data = [];
          let totalCount = 0;
          
          // 处理不同的返回格式
          if (res && typeof res === 'object') {
            if (res.success !== undefined) {
              // 格式1: {success: true, data: [...], total: 100}
              success = res.success;
              // 检查是否有分页结构
              if (res.data && res.data.records) {
                data = res.data.records || [];
                totalCount = res.data.total || 0;
              } else {
                data = res.data || [];
                totalCount = res.total || data.length;
              }
            } else if (res.code !== undefined) {
              // 格式2: {code: 1000, data: [...], total: 100}
              success = res.code === 1000;
              // 检查是否有分页结构
              if (res.data && res.data.records) {
                data = res.data.records || [];
                totalCount = res.data.total || 0;
              } else {
                data = res.data || [];
                totalCount = res.total || data.length;
              }
            } else if (Array.isArray(res)) {
              // 格式3: 直接返回数组
              success = true;
              data = res;
              totalCount = res.length;
            } else if (res.data !== undefined) {
              // 格式4: {data: [...], total: 100} 或 {data: {records: [...], total: 100}}
              success = true;
              if (res.data && res.data.records) {
                // 处理标准分页格式: {data: {records: [...], total: 100}}
                data = res.data.records || [];
                totalCount = res.data.total || 0;
              } else if (Array.isArray(res.data)) {
                // 直接是数组
                data = res.data;
                totalCount = res.total || data.length;
              } else if (res.data && res.data.data && Array.isArray(res.data.data)) {
                // 处理 {data: {data: [...], total: 100}} 的情况
                data = res.data.data;
                totalCount = res.data.total || data.length;
              }
            }
          }
          
          // 确保data始终是一个数组
          if (!Array.isArray(data)) {
            console.warn('API返回的数据格式异常，已处理为空数组', data);
            data = [];
            totalCount = 0;
          }
          
          // 如果有餐厅ID，且后端不支持按餐厅ID过滤，在前端进行过滤
          // 注意：这会破坏分页，应尽量使用后端过滤
          if (this.restaurantId && !this.queryParams.restaurantId) {
            console.warn('使用前端过滤可能会影响分页效果，建议后端支持按餐厅ID过滤');
            data = data.filter(item => item.restaurantId === this.restaurantId);
            totalCount = data.length; // 重新计算总数
            console.log(`已根据餐厅ID: ${this.restaurantId} 过滤菜品，结果数量: ${data.length}`);
          }
          
          if (success) {
            // 保存完整数据用于前端分页
            this.allDishList = data;
            totalCount = data.length;
            
            // 前端分页处理
            const pageNum = this.queryParams.pageNum || 1;
            const pageSize = this.queryParams.pageSize || 10;
            const startIndex = (pageNum - 1) * pageSize;
            const endIndex = startIndex + pageSize;
            
            // 切片获取当前页数据
            this.dishList = data.slice(startIndex, endIndex);
            this.total = totalCount;
            
            // 增加菜品餐厅名称处理
            if (this.dishList.length > 0 && this.restaurantOptions.length > 0) {
              this.dishList.forEach(dish => {
                const restaurant = this.restaurantOptions.find(r => r.id === dish.restaurantId);
                dish.restaurantName = restaurant ? restaurant.name : '未知餐厅';
              });
            }
            
            console.log('处理后的菜品列表:', this.dishList);
            console.log('总记录数:', this.total);
            console.log('当前页:', pageNum, '每页条数:', pageSize);
          } else {
            this.$message.error('获取菜品列表失败: ' + (res.message || '未知错误'));
            this.dishList = [];
            this.total = 0;
          }
          this.loading = false;
        })
        .catch(err => {
          console.error('获取菜品列表错误:', err);
          this.$message.error('获取菜品列表失败，请检查网络或服务器状态');
          this.dishList = [];
          this.total = 0;
          this.loading = false;
        });
    },
    // 返回餐厅列表
    goBack() {
      console.log('返回餐厅列表');
      this.$router.push({ path: '/restaurant' }).then(() => {
        console.log('返回成功');
      }).catch(err => {
        console.error('返回路由错误:', err);
        if (err.name !== 'NavigationDuplicated') {
          this.$message.error('页面跳转失败');
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      // 确保表单中所有属性都有默认值，避免undefined错误
      this.form = {
        id: undefined,
        name: '',
        price: 0,
        dishType: '',
        description: '',
        images: '',
        isSpecial: 0,
        restaurantId: this.restaurantId || undefined,
        createTime: undefined,
        updateTime: undefined,
        createBy: undefined,
        updateBy: undefined
      };
      
      console.log('重置后的表单:', this.form);
      
      // 重置文件列表
      this.fileList = [];
      // 重置表单验证
      if (this.$refs["form"]) {
        this.$refs["form"].resetFields();
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      // 如果是从餐厅详情进来，重置时保留餐厅ID
      if (this.restaurantId) {
        this.queryParams.restaurantId = this.restaurantId;
      }
      this.handleQuery();
    },
    // 重置表单
    resetForm(formRef) {
      if (this.$refs[formRef]) {
        this.$refs[formRef].resetFields();
        // 重置查询参数，但保留restaurantId（如果是从餐厅详情进来）
        const restaurantId = this.restaurantId || undefined;
        this.queryParams = {
          pageNum: 1,
          pageSize: 10,
          name: undefined,
          dishType: undefined,
          isSpecial: undefined,
          restaurantId: restaurantId
        };
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加菜品";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0];
      this.$axios.get('/dish/' + id)
        .then(res => {
          console.log('获取菜品详情返回:', res);
          // 处理不同的返回数据格式
          let responseData = null;
          
          if (res && typeof res === 'object') {
            if (res.data && res.data.data) {
              // 格式1: {data: {data: ...}}
              responseData = res.data.data;
            } else if (res.data) {
              // 格式2: {data: ...}
              responseData = res.data;
            } else {
              // 格式3: 直接就是数据
              responseData = res;
            }
          }
          
          if (!responseData) {
            this.$message.error('获取菜品数据失败');
            return;
          }
          
          // 确保form有所有必要的属性，避免undefined错误
          this.form = {
            id: responseData.id || '',
            name: responseData.name || '',
            price: responseData.price || 0,
            dishType: responseData.dishType || '',
            description: responseData.description || '',
            images: responseData.images || '',
            isSpecial: responseData.isSpecial !== undefined ? responseData.isSpecial : 0,
            restaurantId: responseData.restaurantId || this.restaurantId || ''
          };
          
          // 处理图片展示
          if (this.form.images) {
            try {
              const images = this.form.images.split(',');
              this.fileList = images.map((img, index) => {
                // 确保URL格式正确
                let imgUrl = img;
                if (!imgUrl) return null; // 跳过空值
                
                if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
                  imgUrl = '/' + imgUrl;
                }
                if (!imgUrl.startsWith('http')) {
                  imgUrl = 'http://localhost:8080' + imgUrl;
                }
                
                return {
                  name: 'image-' + (index + 1),
                  url: imgUrl,
                  status: 'success'
                };
              }).filter(item => item !== null); // 过滤掉空值
            } catch (error) {
              console.error('处理图片展示错误:', error);
              this.fileList = [];
            }
          } else {
            this.fileList = [];
          }
          
          this.open = true;
          this.title = "修改菜品";
        })
        .catch(err => {
          console.error('获取菜品详情错误:', err);
          this.$message.error('获取菜品详情失败，请检查网络或服务器状态');
        });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          try {
            // 处理图片数据
            if (this.fileList && this.fileList.length > 0) {
              const imageUrls = [];
              this.fileList.forEach(file => {
                if (file.url) {
                  // 如果是完整URL，直接使用
                  imageUrls.push(file.url);
                } else if (file.response) {
                  // 如果有响应对象，尝试从中获取URL
                  let url = '';
                  if (typeof file.response === 'string') {
                    url = file.response;
                  } else if (file.response && file.response.data) {
                    url = file.response.data;
                  } else if (file.response && file.response.url) {
                    url = file.response.url;
                  }
                  
                  // 确保URL格式正确
                  if (url) {
                    if (!url.startsWith('http') && !url.startsWith('/')) {
                      url = '/' + url;
                    }
                    imageUrls.push(url);
                  }
                }
              });
              
              // 过滤掉空URL并转为逗号分隔的字符串
              this.form.images = imageUrls.filter(url => url).join(',');
              console.log('最终处理的图片URLs:', this.form.images);
            } else {
              this.form.images = '';
            }
            
            // 强制设置一些数值类型的字段
            this.form.price = parseFloat(this.form.price) || 0;
            if (typeof this.form.isSpecial !== 'number') {
              this.form.isSpecial = parseInt(this.form.isSpecial) || 0;
            }
            
            // 确保有餐厅ID
            if (!this.form.restaurantId && this.restaurantId) {
              this.form.restaurantId = this.restaurantId;
            }
            
            // 确保餐厅ID是有效的
            if (!this.form.restaurantId) {
              this.$message.error('请选择所属餐厅');
              return;
            }
            
            console.log('提交的表单数据:', this.form);
            
            if (this.form.id) {
              this.$axios.put('/dish', this.form)
                .then(res => {
                  this.$modal.msgSuccess("修改成功");
                  this.open = false;
                  this.getList();
                })
                .catch(err => {
                  console.error('修改菜品错误:', err);
                  this.$modal.msgError("修改失败，请检查网络或服务器状态");
                });
            } else {
              this.$axios.post('/dish', this.form)
                .then(res => {
                  this.$modal.msgSuccess("新增成功");
                  this.open = false;
                  this.getList();
                })
                .catch(err => {
                  console.error('错误:', err);
                  this.$modal.msgError("新增失败，请检查网络或服务器状态");
                });
            }
          } catch (error) {
            console.error('提交表单处理错误:', error);
            this.$modal.msgError("表单提交处理错误: " + error.message);
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除菜品编号为"' + ids + '"的数据项？').then(() => {
        this.$axios.delete('/dish/' + ids).then(res => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        });
      }).catch(() => {});
    },
    /** 图片上传成功回调 */
    handleUploadSuccess(response, file, fileList) {
      try {
        console.log('上传成功响应数据:', response);
        console.log('文件信息:', file);
        
        // 处理不同的返回格式
        let imageUrl = '';
        
        // 详细分析响应数据
        if (response) {
          if (typeof response === 'string') {
            // 直接返回字符串URL
            imageUrl = response;
            console.log('响应为字符串:', imageUrl);
          } else if (response.success && response.data) {
            // {success: true, data: "url"}
            imageUrl = response.data;
            console.log('响应格式1:', imageUrl);
          } else if (response.code === 1000 && response.data) {
            // {code: 1000, data: "url"}
            imageUrl = response.data;
            console.log('响应格式2:', imageUrl);
          } else if (response.code !== undefined && response.message !== undefined && response.data) {
            // 通用后端返回格式
            imageUrl = response.data;
            console.log('响应格式3:', imageUrl);
          } else if (response.url) {
            // {url: "url"}
            imageUrl = response.url;
            console.log('响应格式4:', imageUrl);
          } else if (response.data && typeof response.data === 'string') {
            // {data: "url"}
            imageUrl = response.data;
            console.log('响应格式5:', imageUrl);
          } else {
            // 尝试遍历找出可能的URL字段
            console.log('尝试分析响应对象的所有字段');
            for (const key in response) {
              console.log(`字段 ${key}:`, response[key]);
              if (typeof response[key] === 'string' && 
                 (response[key].startsWith('/') || 
                  response[key].startsWith('http') || 
                  response[key].includes('img/'))) {
                imageUrl = response[key];
                console.log('找到可能的URL字段:', key, imageUrl);
                break;
              }
            }
          }
        }
        
        if (fileList) {
          this.fileList = fileList;
        }
        
        if (imageUrl) {
          // 修正URL，确保是完整路径
          if (imageUrl.startsWith('http')) {
            file.url = imageUrl;
          } else {
            file.url = 'http://localhost:8080' + (imageUrl.startsWith('/') ? '' : '/') + imageUrl;
          }
          console.log('最终处理后的图片URL:', file.url);
          this.$message.success('图片上传成功');
        } else {
          // 上传成功但未识别URL格式，直接使用响应作为URL
          if (response) {
            if (typeof response === 'string') {
              file.url = 'http://localhost:8080' + (response.startsWith('/') ? '' : '/') + response;
              console.log('使用原始响应作为URL:', file.url);
              this.$message.success('图片上传成功');
            } else {
              console.error('未能识别的响应格式:', response);
              this.$message.warning('图片上传成功，但未获取到URL，请检查后端响应格式');
            }
          } else {
            file.url = '';
            this.$message.warning('图片上传成功，但未获取到URL，请联系管理员');
          }
        }
      } catch (error) {
        console.error('图片上传处理错误:', error);
        this.$message.error('图片上传处理错误: ' + error.message);
        
        // 确保至少有一个基本的URL
        if (file) {
          file.url = '';
        }
      }
    },
    /** 图片删除操作 */
    handleRemove(file, fileList) {
      try {
        console.log('图片删除:', file);
        if (fileList) {
          this.fileList = fileList;
        } else {
          this.fileList = [];
        }
      } catch (error) {
        console.error('图片删除错误:', error);
        this.$message.error('图片删除处理错误');
      }
    },
    /** 图片预览操作 */
    handlePictureCardPreview(file) {
      try {
        if (file && file.url) {
          this.dialogImageUrl = file.url;
          this.dialogVisible = true;
        } else {
          this.$message.warning('图片URL不存在，无法预览');
        }
      } catch (error) {
        console.error('图片预览错误:', error);
        this.$message.error('图片预览处理错误');
      }
    },
    beforeUpload(file) {
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
      }
      return isLt5M;
    },
    addImageUrl() {
      this.$prompt('请输入图片URL', '手动添加图片', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^(\/|https?:\/\/).+/,
        inputErrorMessage: '请输入有效的URL（以/或http开头）'
      }).then(({ value }) => {
        // 确保URL格式正确
        let imgUrl = value;
        if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
          imgUrl = '/' + imgUrl;
        }
        if (!imgUrl.startsWith('http')) {
          imgUrl = 'http://localhost:8080' + imgUrl;
        }
        
        // 添加到文件列表
        this.fileList.push({
          name: 'manual-image-' + (this.fileList.length + 1),
          url: imgUrl,
          status: 'success'
        });
        
        this.$message.success('图片URL已添加');
      }).catch(() => {
        this.$message.info('已取消添加');
      });
    },
    handleUploadError(error) {
      console.error('图片上传错误:', error);
      this.$message.error('图片上传处理错误: ' + error.message);
    },
    getFirstImage(images) {
      if (!images) return '';
      
      try {
        const imageUrls = images.split(',');
        let imgUrl = imageUrls[0];
        
        if (!imgUrl) return '';
        
        // 确保URL格式正确
        if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
          imgUrl = '/' + imgUrl;
        }
        
        if (!imgUrl.startsWith('http')) {
          imgUrl = 'http://localhost:8080' + imgUrl;
        }
        
        return imgUrl;
      } catch (error) {
        console.error('处理图片URL错误:', error);
        return '';
      }
    },
    getImageList(images) {
      if (!images) return [];
      
      try {
        const imageUrls = images.split(',').filter(url => url && url.trim() !== '');
        
        return imageUrls.map(url => {
          let imgUrl = url.trim();
          
          // 确保URL格式正确
          if (!imgUrl.startsWith('http') && !imgUrl.startsWith('/')) {
            imgUrl = '/' + imgUrl;
          }
          
          if (!imgUrl.startsWith('http')) {
            imgUrl = 'http://localhost:8080' + imgUrl;
          }
          
          return imgUrl;
        });
      } catch (error) {
        console.error('处理图片列表错误:', error);
        return [];
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.dish-management {
  width: 100%;
  height: 100%;
  
  /* 页面标题区域样式已删除 */

  /* 信息卡片 */
  .info-card {
    margin-bottom: 16px;

    .restaurant-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
      
      .restaurant-title {
        font-size: 16px;
      font-weight: 600;
      color: #409EFF;
      }
      
      .back-btn {
      margin-left: auto;
    }
  }

  /* 警告卡片 */
  .warning-card {
    margin-bottom: 16px;

    ::v-deep .el-card__header {
      background-color: #FDF6EC;
      color: #E6A23C;
    }

    .warning-title {
      font-weight: 600;
      display: flex;
      align-items: center;
      
      i {
        margin-right: 8px;
      }
    }

    .warning-message {
      padding: 10px;
      color: #E6A23C;
    }
  }
  
  /* 搜索区域 */
  .search-panel {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.08);
    margin-bottom: 16px;
    padding: 5px 15px;
    
    .search-form {
      padding: 10px 0;
    }
    
    .search-row {
      padding: 10px 0;
    }
    
    .search-item {
      display: flex;
      flex-direction: column;
      margin-bottom: 10px;
      
      .search-label {
        font-weight: 500;
        margin-bottom: 5px;
        color: #606266;
        font-size: 14px;
      }
    }
    
    .status-select {
      width: 100%;
    }
    .search-buttons {
    display: flex;
    margin-top: 20px;
    
    .el-button {
      margin-right: 10px;
      padding: 9px 15px;
      
      &.el-button--primary {
        background-color: #409eff;
        border-color: #409eff;
        color: #ffffff;
        
        &:hover,
        &:focus {
          background-color: #66b1ff;
          border-color: #66b1ff;
          color: #ffffff;
        }
      }
    }
  
    }
  }
  
  /* 表格区域 */
  .table-panel {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.08);
    padding: 16px;
    margin-bottom: 16px;
    
    .table-toolbar {
      padding: 10px 0;
      margin-bottom: 15px;
      display: flex;
      border-bottom: 1px solid #f0f0f0;
      padding-bottom: 15px;
      
      .el-button {
        margin-right: 12px;
        padding: 9px 15px;
        
        &.el-button--primary {
          background-color: #409eff;
          border-color: #409eff;
          color: #ffffff;
          
          &:hover,
          &:focus {
            background-color: #66b1ff;
            border-color: #66b1ff;
            color: #ffffff;
          }
        }
      }
    }
    
    .data-table {
      margin-bottom: 15px;
      
      ::v-deep .el-table__header-wrapper {
        th {
          background-color: #f5f7fa;
          color: #606266;
          font-weight: 500;
          padding: 10px 0;
        }
      }
    }
    
    .image-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 80px;
      height: 60px;
      background-color: #f5f7fa;
      border-radius: 4px;
      color: #909399;
      font-size: 12px;
      
      i {
        font-size: 20px;
        margin-bottom: 4px;
      }
    }
    
    .price-tag {
      display: inline-block;
      padding: 2px 8px;
      background-color: #f0f9eb;
      color: #67C23A;
      border-radius: 4px;
      font-weight: bold;
    }
    
    .action-buttons {
      display: flex;
      white-space: nowrap;
      flex-wrap: nowrap;
      
      .el-button {
        margin-right: 5px;
        padding: 5px 8px;
        
        &:last-child {
          margin-right: 0;
        }
      }
    }
    
    .pagination-container {
      padding: 15px 0 5px 0;
      text-align: right;
      border-top: 1px solid #f0f0f0;
      margin-top: 10px;
    }
  }
  
  /* 对话框样式 */
  .dish-dialog {
    ::v-deep .el-dialog {
      border-radius: 8px;
      overflow: hidden;
    }
    
    ::v-deep .el-dialog__header {
      background-color: #f5f7fa;
      padding: 20px;
      border-bottom: 1px solid #ebeef5;
    }
    
    ::v-deep .el-dialog__body {
      padding: 20px;
    }
    
    ::v-deep .el-dialog__footer {
      padding: 15px 20px;
      border-top: 1px solid #ebeef5;
    }
    
    .upload-container {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      padding: 10px;
      background-color: #fbfdff;
    }
  }
  
  .image-preview-dialog {
    ::v-deep .el-dialog {
      border-radius: 8px;
      overflow: hidden;
    }
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .search-form {
    .search-item {
      flex-direction: column;
      align-items: flex-start;
      
      .search-label {
        width: 100%;
        text-align: left;
        margin-bottom: 5px;
      }
    }
  }
  
  .search-buttons {
    width: 100%;
    justify-content: flex-end;
  }
  
  .action-buttons {
    display: flex;
    flex-direction: column;
    
    .el-button {
      margin-bottom: 5px;
      margin-left: 0 !important;
    }
  }
}
</style>