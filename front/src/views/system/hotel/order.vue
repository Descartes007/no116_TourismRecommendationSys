<template>
  <div class="hotel-order-management">
      <!-- 页面标题区域已删除 -->
      
      <div class="search-table-container">
        <!-- 搜索区域 -->
        <div class="search-panel">
            <el-row :gutter="20" class="search-row">
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">酒店名称:</span>
                        <el-input
                            size="small"
                            placeholder="请输入酒店名称"
                            prefix-icon="el-icon-search"
                            v-model="search.name">
                        </el-input>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">预约状态:</span>
                        <el-select 
                          clearable 
                          size="small" 
                          v-model="search.state" 
                          placeholder="请选择状态"
                          class="full-width">
                          <el-option label="未确认" value="0"></el-option>
                          <el-option label="已确认" value="1"></el-option>
                          <el-option label="取消中" value="2"></el-option>
                          <el-option label="已取消" value="3"></el-option>
                          <el-option label="取消失败" value="4"></el-option>
                          <el-option label="已使用" value="5"></el-option>
                        </el-select>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">预约人:</span>
                        <el-input
                            size="small"
                            placeholder="请输入预约人姓名"
                            prefix-icon="el-icon-user"
                            v-model="search.createBy">
                        </el-input>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-buttons">
                        <el-button size="small" type="primary" icon="el-icon-search" @click="searchPage">查 询</el-button>
                        <el-button size="small" icon="el-icon-refresh" @click="refresh">重 置</el-button>
                    </div>
                </el-col>
            </el-row>
        </div>
        
        <!-- 表格区域 -->
        <div class="table-panel">
            <div class="table-toolbar">
                <el-button 
                    type="danger" 
                    size="small" 
                    icon="el-icon-delete" 
                    :disabled="update.length <= 0" 
                    @click="deleteDataBtn">批量删除</el-button>
            </div>
            
            <el-table
                v-loading="loading"
                :data="tableData"
                @selection-change="handleSelectionChange"
                stripe
                border
                highlight-current-row
                class="data-table">
                <el-table-column
                  type="selection"
                  width="50"
                  align="center">
                </el-table-column>
                <el-table-column
                  prop="name"
                  label="酒店名称"
                  min-width="140"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="num"
                  label="人数"
                  width="70"
                  align="center">
                </el-table-column>
                <el-table-column
                  prop="time"
                  label="预约时间"
                  min-width="120"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="state"
                  label="状态"
                  width="100"
                  align="center">
                  <template slot-scope="scope">
                    <el-tag v-if="scope.row.state == 0" type="info" effect="dark" size="small">未确认</el-tag>
                    <el-tag v-if="scope.row.state == 1" type="success" effect="dark" size="small">已确认</el-tag>
                    <el-tag v-if="scope.row.state == 2" type="warning" effect="dark" size="small">取消中</el-tag>
                    <el-tag v-if="scope.row.state == 3" type="danger" effect="dark" size="small">已取消</el-tag>
                    <el-tag v-if="scope.row.state == 4" type="warning" effect="dark" size="small">取消失败</el-tag>
                    <el-tag v-if="scope.row.state == 5" type="success" effect="dark" size="small">已使用</el-tag>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="itemName"
                  label="房型"
                  min-width="100"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="price"
                  label="价格"
                  min-width="80"
                  align="right">
                  <template slot-scope="scope">
                    <span class="price-value">{{ scope.row.price ? '¥' + scope.row.price : '-' }}</span>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="createBy"
                  label="预约人"
                  min-width="100"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="createTime"
                  label="创建时间"
                  min-width="160"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  label="操作"
                  width="300"
                  fixed="right">
                  <template slot-scope="scope">
                    <div class="action-buttons">
                      <el-button size="mini" type="success" plain icon="el-icon-check" @click="editSysHotelOrder(scope.row.id,5)">标记已使用</el-button>
                      <template v-if="scope.row.state == 2">
                        <el-button size="mini" type="warning" plain icon="el-icon-close" @click="editSysHotelOrder(scope.row.id,4)">拒绝取消</el-button>
                        <el-button size="mini" type="primary" plain icon="el-icon-check" @click="editSysHotelOrder(scope.row.id,3)">允许取消</el-button>
                      </template>
                      <el-button size="mini" type="primary" plain icon="el-icon-check" v-if="scope.row.state == 0" @click="editSysHotelOrder(scope.row.id,1)">确认</el-button>
                      <el-button size="mini" type="info" plain icon="el-icon-view" @click="updateData(scope.row.id)">编辑</el-button>
                      <el-button size="mini" type="danger" plain icon="el-icon-delete" @click="deleteConfirm(scope.row.id)">删除</el-button>
                    </div>
                  </template>
                </el-table-column>
            </el-table>
              
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
    <update @updateFalse="updateFalse" :updateId="updateId" :updateVisible="updateVisible"></update>
  </div>
</template>

<script>
  import {getSysHotelOrderPage,removeSysHotelOrder,editSysHotelOrder} from '../../../api/api' 
  import update from '../../../components/system/hotel/viewOrder'
  export default {
    data() {
      return{
        loading: true,
        update: [],
        remove: [],
        updateId: "",
        addVisible: false,
        updateVisible: false,
        search: {
            hotelId: "",
            name: "",
            state: "",
            userId: "",
            itemId: "",
            createBy: "",
            createTime: "",
            pageNumber: 1,
            pageSize:10
        },
        total: 0,
        tableData: []
      }
    },
    components: {
      update
    },
    methods: {
      editSysHotelOrder(id,state) {
        const stateText = {
          1: '确认',
          3: '取消',
          4: '拒绝取消',
          5: '标记已使用'
        }[state] || '更新';
        
        var param = {
          id: id,
          state: state
        }
        
        this.loading = true;
        editSysHotelOrder(param).then(res => {
          this.loading = false;
          if (res.code == 1000) {
            this.$message({
              type: 'success',
              message: `预约${stateText}成功!`
            });
            this.query()
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message || '操作失败'
            });
          }
        }).catch(() => {
          this.loading = false;
          this.$notify.error({
            title: '错误',
            message: '网络异常，请稍后再试'
          });
        });
      },
      
      deleteConfirm(id) {
        this.$confirm('确认删除该预约记录?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDate(id)
        }).catch(() => {});
      },
      
      searchPage() {
        this.search.pageNumber = 1
        this.query()
      },
      query() {
        this.loading = true;
        getSysHotelOrderPage(this.search).then(res => {
          if(res.code == 1000) {
            this.tableData = res.data.records
            this.total = res.data.total
            this.loading = false
          } else {
            this.loading = false;
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        }).catch(err => {
          this.loading = false;
          this.$notify.error({
            title: '错误',
            message: '查询数据失败'
          });
        });
      },
      refresh() {
        this.search = {
          hotelId: "",
          name: "",
          state: "",
          userId: "",
          itemId: "",
          createBy: "",
          createTime: "",
          pageNumber: 1,
          pageSize: this.search.pageSize
        };
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
        this.update = []
        this.remove = []
        for (let i = 0;i < val.length;i++) {
          var item = val[i]
          this.update.push(item.id)
          this.remove.push(item.id)
        }
      },
      updateFalse() {
        this.updateId = ''
        this.updateVisible = false
        this.query()
      },
      updateData(id) {
        this.updateId = id
        this.updateVisible = true
      },
      updateDataBtn() {
        this.updateData(this.update[0])
      },
      deleteDataBtn() {
        this.$confirm('确定删除选中的'+ this.remove.length +'条数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDate(this.remove.join(","))
        }).catch(() => {
                 
        });
      },
      deleteDate(ids) {
        this.loading = true;
        removeSysHotelOrder({ids:ids}).then(res => {
            this.loading = false;
            if(res.code == 1000) {
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
              this.query()
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message
              });
            }
          }).catch(() => {
            this.loading = false;
            this.$notify.error({
              title: '错误',
              message: '删除失败，请稍后再试'
            });
          });
      },
    },
    mounted() {
      this.query()
    }
 }
</script>

<style lang="scss" scoped>
.hotel-order-management {
  width: 100%;
  height: 100%;
  
  /* 页面标题区域样式已删除 */
  
  .search-table-container {
    width: 100%;
  }
  
  /* 搜索区域 */
  .search-panel {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.08);
    margin-bottom: 16px;
    padding: 5px 15px;
    
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
      
      .full-width {
        width: 100%;
      }
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
    overflow-x: auto;
    
    .table-toolbar {
      padding: 10px 0;
      margin-bottom: 15px;
      display: flex;
      border-bottom: 1px solid #f0f0f0;
      padding-bottom: 15px;
      
      .el-button {
        margin-right: 12px;
        padding: 9px 15px;
      }
    }
    
    .data-table {
      margin-bottom: 15px;
      width: 100%;
      
      ::v-deep .el-table__header-wrapper {
        th {
          background-color: #f5f7fa;
          color: #606266;
          font-weight: 500;
          padding: 10px 0;
        }
      }
    }
    
    .price-value {
      color: #67c23a;
      font-weight: 500;
    }
    
    .action-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 5px;
      justify-content: center;
      
      .el-button {
        margin: 0;
        padding: 5px 8px;
      }
    }
    
    .pagination-container {
      padding: 15px 0 5px 0;
      text-align: right;
      border-top: 1px solid #f0f0f0;
      margin-top: 10px;
    }
  }
}

/* Element UI 样式覆盖 */
::v-deep .el-table {
  th {
    background-color: #f5f7fa;
    color: #606266;
    font-weight: 600;
  }
  
  td {
    padding: 8px 0;
  }
}

::v-deep .el-table .cell {
  word-break: break-word;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .search-item {
    flex-direction: column;
    align-items: flex-start;
    
    .search-label {
      width: 100%;
      text-align: left;
      margin-bottom: 5px;
    }
  }
  
  .search-buttons {
    width: 100%;
    justify-content: flex-end;
  }
  
  .action-buttons {
    flex-direction: column;
    
    .el-button {
      margin-bottom: 5px;
      width: 100%;
    }
  }
}
</style>