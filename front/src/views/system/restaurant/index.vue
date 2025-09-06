<template>
  <div class="restaurant-management">
    <!-- 搜索区域 -->
    <div class="search-panel">
      <el-form :model="queryParams" ref="queryForm" class="search-form" label-width="80px">
        <el-row :gutter="20" class="search-row">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">餐厅名称:</span>
              <el-input 
                v-model="queryParams.name" 
                placeholder="请输入餐厅名称" 
                clearable 
                prefix-icon="el-icon-search"
                size="small"
                @keyup.enter.native="handleQuery" />
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">地区:</span>
              <el-input 
                v-model="queryParams.region" 
                placeholder="请输入地区" 
                clearable 
                prefix-icon="el-icon-location"
                size="small"
                @keyup.enter.native="handleQuery" />
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">菜系类型:</span>
              <el-input 
                v-model="queryParams.cuisineType" 
                placeholder="请输入菜系类型" 
                clearable 
                prefix-icon="el-icon-food"
                size="small"
                @keyup.enter.native="handleQuery" />
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">状态:</span>
              <el-select 
                v-model="queryParams.state" 
                placeholder="餐厅状态" 
                clearable 
                size="small"
                class="status-select">
                <el-option v-for="dict in stateOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
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
        <el-button type="primary" size="small" icon="el-icon-plus" @click="handleAdd">新增餐厅</el-button>
        <el-button type="success" size="small" icon="el-icon-edit" :disabled="single" @click="handleUpdate">编辑餐厅</el-button>
        <el-button type="danger" size="small" icon="el-icon-delete" :disabled="multiple" @click="handleDelete">删除餐厅</el-button>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </div>

    <div class="table-panel">
      <el-table 
        v-loading="loading" 
        :data="restaurantList" 
        @selection-change="handleSelectionChange"
        class="data-table"
        border
        highlight-current-row
        stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="餐厅名称" align="center" prop="name" min-width="120" show-overflow-tooltip />
        <el-table-column label="餐厅图片" align="center" width="120">
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
        <el-table-column label="地区" align="center" prop="region" width="120" />
        <el-table-column label="地址" align="center" prop="address" min-width="150" show-overflow-tooltip />
        <el-table-column label="联系电话" align="center" prop="phone" width="120" />
        <el-table-column label="菜系类型" align="center" prop="cuisineType" width="120" />
        <el-table-column label="价格等级" align="center" prop="priceLevel" width="160">
          <template slot-scope="scope">
            <div class="price-level">
              <el-rate
                v-model="scope.row.priceLevel"
                disabled
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                :max="5">
              </el-rate>
              <span class="price-text">{{ formatPriceLevel(scope.row.priceLevel) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="state" width="80">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.state"
              :active-value="1"
              :inactive-value="0"
              active-color="#13ce66"
              inactive-color="#ff4949"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button size="mini" type="primary" plain icon="el-icon-edit" @click="handleUpdate(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
              <el-button size="mini" type="info" plain icon="el-icon-s-goods" @click="handleViewDishes(scope.row)">菜品</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="handlePagination"
        />
      </div>
    </div>

    <!-- 添加或修改餐厅对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body class="restaurant-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="餐厅名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入餐厅名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地区" prop="region">
              <el-input v-model="form.region" placeholder="请输入地区" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="form.address" placeholder="请输入详细地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="菜系类型" prop="cuisineType">
              <el-input v-model="form.cuisineType" placeholder="请输入菜系类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格等级" prop="priceLevel">
              <el-rate v-model="form.priceLevel" :max="5" :texts="['很便宜', '便宜', '适中', '贵', '很贵']" show-text :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="form.businessHours" placeholder="请输入营业时间" />
        </el-form-item>
        <el-form-item label="餐厅介绍" prop="introduce">
          <el-input v-model="form.introduce" type="textarea" placeholder="请输入餐厅介绍" :rows="3" />
        </el-form-item>
        <el-form-item label="餐厅图片" prop="images">
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
              :show-file-list="true"
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
        <el-form-item label="状态" prop="state">
          <el-radio-group v-model="form.state">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
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
  name: "Restaurant",
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
      // 餐厅表格数据
      restaurantList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 状态选项
      stateOptions: [
        { value: 1, label: "正常" },
        { value: 0, label: "停用" }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 4,
        name: undefined,
        region: undefined,
        cuisineType: undefined,
        state: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "餐厅名称不能为空", trigger: "blur" }
        ],
        region: [
          { required: true, message: "地区不能为空", trigger: "blur" }
        ],
        cuisineType: [
          { required: true, message: "菜系类型不能为空", trigger: "blur" }
        ]
      },
      // 上传图片相关
      dialogImageUrl: '',
      dialogVisible: false,
      fileList: [],
      uploadUrl: '',
      uploadHeaders: {},
      uploadParams: {},
      // 保存完整数据用于前端分页
      fullRestaurantList: []
    };
  },
  created() {
    this.getList();
    // 初始化上传配置
    this.initUploadConfig();
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
        type: 'restaurant', // 图片类型标识
        token: token // 某些后端可能需要通过请求参数获取token
      };
      
      console.log('图片上传配置已初始化:', {
        URL: this.uploadUrl,
        headers: this.uploadHeaders,
        params: this.uploadParams
      });
    },
    /** 查询餐厅列表 */
    getList() {
      this.loading = true;
      this.$axios.get('/restaurant/list', { params: this.queryParams })
        .then(res => {
          console.log('餐厅数据返回:', res);
          // 兼容处理API返回数据格式
          let success = false;
          let data = [];
          
          // 处理不同的返回格式
          if (res && typeof res === 'object') {
            if (res.success !== undefined) {
              // 格式1: {success: true, data: [...]}
              success = res.success;
              data = res.data || [];
            } else if (res.code !== undefined) {
              // 格式2: {code: 1000, data: [...]}
              success = res.code === 1000;
              data = res.data || [];
            } else if (Array.isArray(res)) {
              // 格式3: 直接返回数组
              success = true;
              data = res;
            } else if (res.data !== undefined) {
              // 格式4: {data: [...]}
              success = true;
              if (Array.isArray(res.data)) {
                data = res.data;
              } else if (res.data && res.data.data && Array.isArray(res.data.data)) {
                // 处理 {data: {data: [...]}} 的情况
                data = res.data.data;
              }
            }
          }
          
          if (success) {
            // 保存完整数据用于前端分页
            this.fullRestaurantList = data;
            this.total = data.length;
            
            // 前端手动分页
            const { pageNum, pageSize } = this.queryParams;
            const startIndex = (pageNum - 1) * pageSize;
            const endIndex = startIndex + pageSize;
            this.restaurantList = data.slice(startIndex, endIndex);
            
            this.loading = false;
          } else {
            this.$message.error('获取餐厅列表失败: ' + (res.message || '未知错误'));
            this.restaurantList = [];
            this.fullRestaurantList = [];
            this.total = 0;
            this.loading = false;
          }
        })
        .catch(err => {
          console.error('获取餐厅列表错误:', err);
          this.$message.error('获取餐厅列表失败，请检查网络或服务器状态');
          this.restaurantList = [];
          this.fullRestaurantList = [];
          this.total = 0;
          this.loading = false;
        });
    },
    // 价格等级格式化
    formatPriceLevel(level) {
      const texts = ['很便宜', '便宜', '适中', '贵', '很贵'];
      return level > 0 && level <= 5 ? texts[level - 1] : '';
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
        region: '',
        address: '',
        phone: '',
        cuisineType: '',
        priceLevel: 3,
        businessHours: '',
        introduce: '',
        images: '',
        state: 1,
        createTime: undefined,
        updateTime: undefined
      };
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
      this.handleQuery();
    },
    // 重置表单
    resetForm(formRef) {
      if (this.$refs[formRef]) {
        this.$refs[formRef].resetFields();
        // 重置查询参数
        this.queryParams = {
          pageNum: 1,
          pageSize: 4,
          name: undefined,
          region: undefined,
          cuisineType: undefined,
          state: undefined
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
      console.log('新增按钮点击');
      this.title = "添加餐厅";
      // 确保在DOM更新后再打开弹窗
      this.$nextTick(() => {
        this.open = true;
        console.log('弹窗状态:', this.open);
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0];
      this.$axios.get('/restaurant/' + id)
        .then(res => {
          console.log('获取餐厅详情返回:', res);
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
            this.$message.error('获取餐厅数据失败');
            return;
          }
          
          // 确保form有所有必要的属性，避免undefined错误
          this.form = {
            id: responseData.id || '',
            name: responseData.name || '',
            region: responseData.region || '',
            address: responseData.address || '',
            phone: responseData.phone || '',
            cuisineType: responseData.cuisineType || '',
            priceLevel: responseData.priceLevel || 3,
            businessHours: responseData.businessHours || '',
            introduce: responseData.introduce || '',
            images: responseData.images || '',
            state: responseData.state !== undefined ? responseData.state : 1,
            createTime: responseData.createTime,
            updateTime: responseData.updateTime
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
          this.title = "修改餐厅";
        })
        .catch(err => {
          console.error('获取餐厅详情错误:', err);
          this.$message.error('获取餐厅详情失败，请检查网络或服务器状态');
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
            
            // 确保数值字段正确
            if (typeof this.form.priceLevel !== 'number') {
              this.form.priceLevel = parseInt(this.form.priceLevel) || 3;
            }
            
            if (typeof this.form.state !== 'number') {
              this.form.state = parseInt(this.form.state) || 1;
            }
            
            if (this.form.id) {
              this.$axios.put('/restaurant', this.form)
                .then(res => {
                  if (res.success) {
                    this.$modal.msgSuccess("修改成功");
                    this.open = false;
                    this.getList();
                  } else {
                    this.$modal.msgError("修改失败: " + (res.message || '未知错误'));
                  }
                })
                .catch(err => {
                  console.error('修改餐厅错误:', err);
                  this.$modal.msgError("修改失败，请检查网络或服务器状态");
                });
            } else {
              this.$axios.post('/restaurant', this.form)
                .then(res => {
                  if (res.success) {
                    this.$modal.msgSuccess("新增成功");
                    this.open = false;
                    this.getList();
                  } else {
                    this.$modal.msgError("新增失败: " + (res.message || '未知错误'));
                  }
                })
                .catch(err => {
                  console.error('新增餐厅错误:', err);
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
      this.$modal.confirm('是否确认删除餐厅编号为"' + ids + '"的数据项？').then(() => {
        this.$axios.delete('/restaurant/' + ids).then(res => {
          this.$modal.msgSuccess("删除成功");
          this.getList();
        });
      }).catch(() => {});
    },
    /** 查看菜品按钮操作 */
    handleViewDishes(row) {
      // 确保有有效的ID和名称
      if (!row || !row.id) {
        this.$message.error('无法获取餐厅信息，请重新选择');
        return;
      }
      
      // 打印餐厅详细信息用于调试
      console.log('餐厅详细信息:', row);
      
      // 使用query参数传递餐厅ID和名称
      const params = {
        restaurantId: row.id,
        restaurantName: row.name || '未命名餐厅'
      };
      
      console.log('跳转到菜品管理页面，参数:', params);
      
      this.$router.push({
        path: '/dish',
        query: params
      }).then(() => {
        console.log('跳转成功');
      }).catch(err => {
        console.error('路由跳转错误:', err);
        if (err.name !== 'NavigationDuplicated') {
          this.$message.error('页面跳转失败');
        }
      });
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
    /* 餐厅状态修改 */
    handleStatusChange(row) {
      let text = row.state === 1 ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.name + '"餐厅吗？')
        .then(() => {
          // 确保state是数字类型
          row.state = parseInt(row.state);
          
          this.$axios.put('/restaurant', row)
            .then(res => {
              if (res.success) {
                this.$modal.msgSuccess(text + "成功");
              } else {
                this.$modal.msgError(text + "失败: " + (res.message || '未知错误'));
                row.state = row.state === 1 ? 0 : 1;
              }
            })
            .catch(err => {
              console.error('修改餐厅状态错误:', err);
              this.$modal.msgError(text + "失败，请检查网络或服务器状态");
              row.state = row.state === 1 ? 0 : 1;
            });
        })
        .catch(() => {
          row.state = row.state === 1 ? 0 : 1;
        });
    },
    /** 图片上传错误回调 */
    handleUploadError(err, file) {
      console.error('图片上传错误:', err);
      this.$message.error('图片上传失败，请检查网络或服务器状态');
    },
    /** 上传前校验 */
    beforeUpload(file) {
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!');
      }
      return isLt5M;
    },
    /** 手动添加图片URL */
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
    /** 获取第一个图片 */
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
    /** 获取所有图片列表 */
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
    },
    // 处理分页变化
    handlePagination({ page, limit }) {
      this.queryParams.pageNum = page;
      this.queryParams.pageSize = limit;
      
      // 前端手动分页，不需要重新请求数据
      if (this.fullRestaurantList && this.fullRestaurantList.length > 0) {
        const startIndex = (page - 1) * limit;
        const endIndex = startIndex + limit;
        this.restaurantList = this.fullRestaurantList.slice(startIndex, endIndex);
      } else {
        // 如果没有完整数据，则重新请求
        this.getList();
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.restaurant-management {
  width: 100%;
  height: 100%;
  
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
        
        &.el-button--success {
          background-color: #67c23a;
          border-color: #67c23a;
          color: #ffffff;
          
          &:hover,
          &:focus {
            background-color: #85ce61;
            border-color: #85ce61;
            color: #ffffff;
          }
        }
        
        &.el-button--danger {
          background-color: #f56c6c;
          border-color: #f56c6c;
          color: #ffffff;
          
          &:hover,
          &:focus {
            background-color: #f78989;
            border-color: #f78989;
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
    
    .price-level {
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: center;
      white-space: nowrap;
      
      ::v-deep .el-rate {
        display: inline-flex;
        height: 20px;
        line-height: 1;
      }
      
      ::v-deep .el-rate__icon {
        font-size: 16px;
        margin-right: 2px;
      }
      
      .price-text {
        margin-left: 6px;
        color: #909399;
        font-size: 12px;
      }
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
  .restaurant-dialog {
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
      margin-bottom:
      5px;
      margin-left: 0 !important;
    }
  }
}
</style>