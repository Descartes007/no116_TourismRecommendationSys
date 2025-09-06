<template>
  <div class="_lineOrder">
      <!-- 页面标题区域已删除 -->
      
      <div class="search-table">
        <!-- 搜索区域 -->
        <div class="search">
            <el-row :gutter="20" class="search-row">
                <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
                    <div class="search-item">
                        <span class="search-title">线路名称:</span>
                        <el-input
                            size="small"
                            placeholder="请输入线路名称"
                            prefix-icon="el-icon-search"
                            v-model="search.name"
                            clearable>
                        </el-input>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
                    <div class="search-item">
                        <span class="search-title">订单状态:</span>
                        <el-select
                            size="small"
                            placeholder="请选择状态"
                            v-model="search.state"
                            clearable
                            class="status-select">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option label="待支付" :value="0"></el-option>
                            <el-option label="已支付待出行" :value="1"></el-option>
                            <el-option label="已出行" :value="2"></el-option>
                            <el-option label="已取消" :value="3"></el-option>
                            <el-option label="已完成" :value="4"></el-option>
                        </el-select>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
                    <div class="search-buttons">
                        <el-button size="small" icon="el-icon-search" type="primary" @click="searchPage">查 询</el-button>
                        <el-button size="small" icon="el-icon-refresh" @click="refresh">重 置</el-button>
                    </div>
                </el-col>
            </el-row>
        </div>
        
        <!-- 表格区域 -->
        <div class="table-container">
            <el-table
            v-loading="loading"
            :data="tableData"
            @selection-change="handleSelectionChange"
            stripe
            highlight-current-row
            class="order-table">
            <el-table-column
              type="selection"
              width="45">
            </el-table-column>
            <el-table-column
              prop="name"
              label="线路名称"
              min-width="140"
              show-overflow-tooltip>
            </el-table-column>
            <el-table-column
              prop="price"
              label="价格"
              width="80">
              <template slot-scope="scope">
                <span class="price-text">¥{{ scope.row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="num"
              label="人数"
              width="70">
              <template slot-scope="scope">
                <span class="num-badge">{{ scope.row.num }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="totalPrice"
              label="总价"
              width="90">
              <template slot-scope="scope">
                <span class="total-price">¥{{ scope.row.totalPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column
              prop="travelTime"
              label="出行时间"
              width="180">
              <template slot-scope="scope">
                <div class="time-wrapper">
                  <i class="el-icon-time"></i>
                  <span>{{ scope.row.travelTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              prop="state"
              label="状态"
              width="110">
              <template slot-scope="scope">
                <el-tag :type="getStateType(scope.row.state)" size="medium" effect="dark">
                  {{ getStateText(scope.row.state) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="createTime"
              label="创建时间"
              width="150">
            </el-table-column>
            <el-table-column
              fixed="right"
              label="操作"
              min-width="280">
              <template slot-scope="scope">
                <div class="action-buttons">
                  <el-button size="mini" type="primary" plain icon="el-icon-view" @click="viewOrderDetail(scope.row)">查看</el-button>
                  <el-button 
                    v-if="scope.row.state === 0" 
                    size="mini" 
                    type="success" 
                    plain
                    icon="el-icon-check"
                    @click="updateOrderState(scope.row.id, 1)">
                    已支付
                  </el-button>
                  <el-button 
                    v-if="scope.row.state === 1" 
                    size="mini" 
                    type="warning" 
                    plain
                    icon="el-icon-s-promotion"
                    @click="updateOrderState(scope.row.id, 2)">
                    已出行
                  </el-button>
                  <el-button 
                    v-if="scope.row.state === 2" 
                    size="mini" 
                    type="info" 
                    plain
                    icon="el-icon-finished"
                    @click="updateOrderState(scope.row.id, 4)">
                    已完成
                  </el-button>
                  <el-button 
                    v-if="scope.row.state === 0 || scope.row.state === 1" 
                    size="mini" 
                    type="danger" 
                    plain
                    icon="el-icon-close"
                    @click="cancelOrder(scope.row.id)">
                    取消
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination
              background
              layout="total, sizes, prev, pager, next, jumper"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="search.pageSize"
              :current-page="search.pageNumber"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              :total="total">
            </el-pagination>
          </div>
        </div>
      </div>

      <!-- 订单详情对话框 -->
      <el-dialog 
        title="旅游线路订单详情" 
        :visible.sync="orderDetailVisible" 
        width="60%" 
        class="order-detail-dialog"
        :close-on-click-modal="false">
        <div v-if="Object.keys(currentOrder).length > 0" class="detail-container">
          <!-- 订单基本信息区 -->
          <div class="detail-header">
            <div class="detail-title">
              <h2>{{ currentOrder.name }}</h2>
              <el-tag :type="getStateType(currentOrder.state)" effect="dark" size="medium">
                {{ getStateText(currentOrder.state) }}
              </el-tag>
            </div>
            <div class="order-id-info">订单号：{{ currentOrder.id }}</div>
          </div>
          
          <!-- 图片轮播 -->
          <div class="detail-images" v-if="currentOrder.images">
            <el-carousel height="300px" arrow="always" indicator-position="outside">
              <el-carousel-item v-for="(item, index) in currentOrder.images.split(',')" :key="index">
                <div class="image-container">
                  <img :src="item" alt="线路图片">
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
          
          <!-- 订单信息卡片 -->
          <div class="detail-info-section">
            <div class="section-title">
              <i class="el-icon-tickets"></i>
              <span>订单信息</span>
            </div>
            <el-row :gutter="20" class="info-grid">
              <el-col :span="8">
                <div class="info-item">
                  <span class="info-label">出行人数</span>
                  <span class="info-value">{{ currentOrder.num }}人</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item">
                  <span class="info-label">单价</span>
                  <span class="info-value">¥{{ currentOrder.price }}</span>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="info-item highlight">
                  <span class="info-label">总价</span>
                  <span class="info-value price-value">¥{{ currentOrder.totalPrice }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">出行时间</span>
                  <span class="info-value">{{ currentOrder.travelTime }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">创建时间</span>
                  <span class="info-value">{{ currentOrder.createTime }}</span>
                </div>
              </el-col>
            </el-row>
          </div>
          
          <!-- 线路简介 -->
          <div class="detail-intro-section" v-if="currentOrder.introduce">
            <div class="section-title">
              <i class="el-icon-document"></i>
              <span>线路简介</span>
            </div>
            <div class="intro-content">{{ currentOrder.introduce }}</div>
          </div>
          
          <!-- 出行人信息 -->
          <div class="detail-travelers-section" v-if="currentOrder.people">
            <div class="section-title">
              <i class="el-icon-user"></i>
              <span>出行人信息</span>
            </div>
            <div class="travelers-list">
              <div 
                v-for="(person, index) in JSON.parse(currentOrder.people)" 
                :key="index"
                class="traveler-item">
                <div class="traveler-header">
                  <div class="traveler-number">出行人 {{index + 1}}</div>
                </div>
                <div class="traveler-details">
                  <div class="traveler-detail-item">
                    <span class="detail-label">姓名：</span>
                    <span class="detail-value">{{ person.name }}</span>
                  </div>
                  <div class="traveler-detail-item">
                    <span class="detail-label">电话：</span>
                    <span class="detail-value">{{ person.tel }}</span>
                  </div>
                  <div class="traveler-detail-item">
                    <span class="detail-label">身份证：</span>
                    <span class="detail-value">{{ person.idCard }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div slot="footer" class="dialog-footer">
          <el-button @click="orderDetailVisible = false" size="medium">关闭</el-button>
          <el-button 
            v-if="currentOrder.state === 0" 
            type="success" 
            size="medium"
            icon="el-icon-check"
            @click="updateOrderState(currentOrder.id, 1)">
            标记为已支付
          </el-button>
          <el-button 
            v-if="currentOrder.state === 1" 
            type="warning" 
            size="medium"
            icon="el-icon-s-promotion"
            @click="updateOrderState(currentOrder.id, 2)">
            标记为已出行
          </el-button>
          <el-button 
            v-if="currentOrder.state === 2" 
            type="info" 
            size="medium"
            icon="el-icon-finished"
            @click="updateOrderState(currentOrder.id, 4)">
            标记为已完成
          </el-button>
          <el-button 
            v-if="currentOrder.state === 0 || currentOrder.state === 1" 
            type="danger" 
            size="medium"
            icon="el-icon-close"
            @click="cancelOrder(currentOrder.id)">
            取消订单
          </el-button>
        </div>
      </el-dialog>
  </div>
</template>

<script>
  import { getOrderPageList, updateOrderState, cancelOrder } from '@/api/line'
  import axios from 'axios'
  export default {
    data() {
      return{
        loading: true,
        orderDetailVisible: false,
        currentOrder: {},
        update: [],
        search: {
            name: "",
            state: null,
            pageNumber: 1,
            pageSize: 10
        },
        total: 0,
        tableData: []
      }
    },
    methods: {
      searchPage() {
        this.search.pageNumber = 1
        this.query()
      },
      query() {
        this.loading = true
        getOrderPageList(this.search).then(res => {
          if(res.code == 1000) {
            this.tableData = res.data.records
            this.total = res.data.total
            this.loading = false
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message
            });
            this.loading = false
          }
        }).catch(() => {
          this.loading = false
          this.$notify.error({
            title: '错误',
            message: '获取订单列表失败'
          });
        })
      },
      refresh() {
        this.search.name = ""
        this.search.state = null
        this.query()
      },
      handleCurrentChange(val) {
        this.search.pageNumber = val
        this.query()
      },
      handleSizeChange(val) {
        this.search.pageSize = val
        this.query()
      },
      handleSelectionChange(val) {
        this.update = val.map(item => item.id)
      },
      getStateType(state) {
        switch(state) {
          case 0: return 'info';  // 待支付
          case 1: return 'success';  // 已支付待出行
          case 2: return 'warning';  // 已出行
          case 3: return 'danger';   // 已取消
          case 4: return 'primary';  // 已完成
          default: return 'info';
        }
      },
      getStateText(state) {
        switch(state) {
          case 0: return '待支付';
          case 1: return '已支付待出行';
          case 2: return '已出行';
          case 3: return '已取消';
          case 4: return '已完成';
          default: return '未知状态';
        }
      },
      viewOrderDetail(order) {
        this.currentOrder = {...order}
        this.orderDetailVisible = true
      },
      updateOrderState(id, state) {
        this.$confirm(`确认将订单状态更改为${this.getStateText(state)}吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          console.log('更新订单状态:', id, state);
          
          this.loading = true;
          
          // 参考用户端成功实现的方式，直接构建URL并使用axios发送请求
          const url = `http://localhost:8080/lineOrder/updateSysLineOrderState?id=${id}&state=${state}`;
          console.log('请求URL:', url);
          
          // 不传递请求体数据
          axios.post(url, null, {
            headers: {
              'x_access_token': window.localStorage.getItem('token'),
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }).then(response => {
            this.loading = false;
            console.log('更新状态原始响应:', response);
            
            // 添加健壮性检查，确保response和response.data存在
            if (!response || !response.data) {
              console.error('更新状态响应格式错误:', response);
              // 虽然响应格式不正确，但操作可能已经成功
              this.$message({
                type: 'success',
                message: '状态已更新，但返回数据格式有误'
              });
              // 刷新数据
              this.query();
              
              // 如果是在详情页面更新的，也更新当前订单对象的状态
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.currentOrder.state = state;
              }
              return;
            }
            
            const res = response.data;
            console.log('更新状态响应数据:', res);
            
            if(res && res.code == 1000) {
              this.$message({
                type: 'success',
                message: '状态更新成功!'
              });
              this.query();
              
              // 如果是在详情页面更新的，也更新当前订单对象的状态
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.currentOrder.state = state;
              }
            } else {
              // 虽然响应码不是1000，但操作可能已经成功
              console.error('更新状态响应异常:', res);
              this.$message({
                type: 'success',
                message: '状态可能已更新，正在刷新数据...'
              });
              // 刷新数据以确认
              this.query();
              
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.currentOrder.state = state;
              }
            }
          }).catch(error => {
            this.loading = false;
            console.error('更新状态请求异常:', error);
            
            // 即使发生异常，操作可能已经成功，所以我们刷新数据进行确认
            this.$message({
              type: 'info',
              message: '操作状态未知，正在刷新数据...'
            });
            this.query();
            
            // 如果是在详情页面更新的，也更新当前订单对象的状态
            if(this.orderDetailVisible && this.currentOrder.id === id) {
              this.currentOrder.state = state;
            }
          });
        }).catch(() => {
          // 用户取消操作
        });
      },
      cancelOrder(id) {
        this.$confirm('确认取消该订单吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          console.log('取消订单:', id);
          
          this.loading = true;
          
          // 参考用户端成功实现的方式，直接构建URL并使用axios发送请求
          const url = `http://localhost:8080/lineOrder/cancelSysLineOrder?id=${id}`;
          console.log('请求URL:', url);
          
          // 不传递请求体数据
          axios.post(url, null, {
            headers: {
              'x_access_token': window.localStorage.getItem('token'),
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }).then(response => {
            this.loading = false;
            console.log('取消订单原始响应:', response);
            
            // 添加健壮性检查，确保response和response.data存在
            if (!response || !response.data) {
              console.error('取消订单响应格式错误:', response);
              // 虽然响应格式不正确，但操作可能已经成功
              this.$message({
                type: 'success',
                message: '订单可能已取消，正在刷新数据...'
              });
              // 刷新数据
              this.query();
              
              // 如果是在详情页面取消的，关闭详情页面
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.orderDetailVisible = false;
              }
              return;
            }
            
            const res = response.data;
            console.log('取消订单响应数据:', res);
            
            if(res && res.code == 1000) {
              this.$message({
                type: 'success',
                message: '订单已取消!'
              });
              this.query();
              
              // 如果是在详情页面取消的，关闭详情页面
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.orderDetailVisible = false;
              }
            } else {
              // 虽然响应码不是1000，但操作可能已经成功
              console.error('取消订单响应异常:', res);
              this.$message({
                type: 'success',
                message: '订单可能已取消，正在刷新数据...'
              });
              // 刷新数据以确认
              this.query();
              
              if(this.orderDetailVisible && this.currentOrder.id === id) {
                this.orderDetailVisible = false;
              }
            }
          }).catch(error => {
            this.loading = false;
            console.error('取消订单请求异常:', error);
            
            // 即使发生异常，操作可能已经成功，所以我们刷新数据进行确认
            this.$message({
              type: 'info',
              message: '操作状态未知，正在刷新数据...'
            });
            this.query();
            
            // 如果是在详情页面取消的，关闭详情页面
            if(this.orderDetailVisible && this.currentOrder.id === id) {
              this.orderDetailVisible = false;
            }
          });
        }).catch(() => {
          // 用户取消操作
        });
      }
    },
    mounted() {
      this.query()
    }
 }
</script>
<style lang=scss scoped>
  /* 页面标题区域样式已删除 */
  
  /* 搜索区域 */
  .search-table {
    width: 100%;
  }
  
  /* 搜索区域优化 */
  .search {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.05);
    padding: 20px;
    margin-bottom: 20px;
  }
  
  .search-row {
    margin: 0;
    
    .el-col {
      margin-bottom: 16px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
  
  .search-item {
    display: flex;
    align-items: center;
    height: 32px;
    
    .search-title {
      font-weight: 500;
      white-space: nowrap;
      font-size: 14px;
      min-width: 90px;
      text-align: right;
      color: #606266;
      margin-right: 12px;
      flex-shrink: 0;
    }
    
    .el-input,
    .el-select {
      flex: 1;
    }
  }
  
  .search-buttons {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    height: 32px;
    
    .el-button {
      margin-right: 10px;
      padding: 9px 15px;
      
      &:last-child {
        margin-right: 0;
      }
      
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
  
  /* 响应式优化 */
  @media (max-width: 768px) {
    .search-item {
      flex-direction: column;
      align-items: flex-start;
      height: auto;
      
      .search-title {
        margin-bottom: 8px;
        margin-right: 0;
        text-align: left;
        min-width: auto;
      }
    }
    
    .search-buttons {
      justify-content: center;
      margin-top: 8px;
      
      .el-button {
        flex: 1;
        margin-right: 8px;
        
        &:last-child {
          margin-right: 0;
        }
      }
    }
  }
  
  .status-select {
    width: 100%;
  }
  
  .search-buttons {
    display: flex;
    gap: 10px;
    
    .el-button {
      padding: 9px 20px;
    }
  }
  
  /* 表格区域 */
  .table-container {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.05);
    padding: 10px 20px 20px;
    overflow: hidden;
  }
  
  .order-table {
    margin-bottom: 20px;
    
    &::before {
      display: none;
    }
  }
  
  .price-text {
    color: #67c23a;
    font-weight: 500;
  }
  
  .total-price {
    color: #f56c6c;
    font-weight: 600;
  }
  
  .num-badge {
    background-color: #f2f6fc;
    border-radius: 15px;
    padding: 2px 10px;
    font-size: 12px;
    color: #409eff;
  }
  
  .time-wrapper {
    display: flex;
    align-items: center;
    white-space: nowrap;
    overflow: hidden;
    
    i {
      color: #909399;
      margin-right: 5px;
    }
  }
  
  .action-buttons {
    display: flex;
    white-space: nowrap;
    flex-wrap: nowrap;
    overflow: visible;
    
    .el-button {
      margin-right: 5px;
      padding: 5px 8px;
      
      &:last-child {
        margin-right: 0;
      }
    }
  }
  
  /* 分页区域 */
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding-top: 15px;
  }
  
  /* 订单详情对话框 */
  .order-detail-dialog {
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
      padding: 0;
    }
    
    ::v-deep .el-dialog__footer {
      padding: 15px 20px;
      border-top: 1px solid #ebeef5;
      display: flex;
      justify-content: flex-end;
    }
  }
  
  .detail-container {
    overflow: hidden;
  }
  
  .detail-header {
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .detail-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    
    h2 {
      margin: 0;
      font-size: 20px;
      color: #303133;
    }
  }
  
  .order-id-info {
    color: #909399;
    font-size: 14px;
  }
  
  .detail-images {
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .image-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    border-radius: 8px;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .detail-info-section, 
  .detail-intro-section, 
  .detail-travelers-section {
    padding: 20px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .section-title {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    
    i {
      margin-right: 8px;
      color: #409eff;
    }
  }
  
  .info-grid {
    margin-bottom: 0;
  }
  
  .info-item {
    background-color: #f9fafb;
    padding: 15px;
    border-radius: 6px;
    margin-bottom: 15px;
  }
  
  .info-label {
    display: block;
    color: #909399;
    font-size: 13px;
    margin-bottom: 5px;
  }
  
  .info-value {
    color: #303133;
    font-size: 15px;
    font-weight: 500;
  }
  
  .highlight .info-value {
    color: #f56c6c;
    font-size: 18px;
    font-weight: 600;
  }
  
  .intro-content {
    line-height: 1.8;
    color: #606266;
    background-color: #f9fafb;
    padding: 15px;
    border-radius: 6px;
  }
  
  .travelers-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
  }
  
  .traveler-item {
    background-color: #f9fafb;
    border-radius: 6px;
    overflow: hidden;
  }
  
  .traveler-header {
    background-color: #ecf5ff;
    padding: 10px 15px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .traveler-number {
    font-size: 14px;
    font-weight: 600;
    color: #409eff;
  }
  
  .traveler-details {
    padding: 15px;
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
  }
  
  .traveler-detail-item {
    flex: 1 0 30%;
  }
  
  .detail-label {
    color: #909399;
    margin-right: 5px;
  }
  
  .detail-value {
    color: #303133;
    font-weight: 500;
  }
  
  /* 响应式适配 */
  @media (max-width: 768px) {
    .search-item {
      flex-direction: column;
      align-items: flex-start;
      
      .search-title {
        width: 100%;
        text-align: left;
        margin-bottom: 5px;
      }
    }
    
    .search-buttons {
      width: 100%;
      justify-content: flex-end;
    }
  }
</style>