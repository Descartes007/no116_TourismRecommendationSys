<template>
  <div class="line-management">
      <!-- 页面标题区域已删除 -->
      
      <div class="search-table-container">
        <!-- 搜索区域 -->
        <div class="search-panel">
            <el-row :gutter="20" class="search-row">
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">名称:</span>
                        <el-input
                            size="small"
                            placeholder="请输入名称"
                            prefix-icon="el-icon-search"
                            v-model="search.name">
                        </el-input>
                    </div>
                </el-col>
                <!-- <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">目的地:</span>
                        <el-input
                            size="small"
                            placeholder="请输入目的地"
                            prefix-icon="el-icon-location"
                            v-model="search.geography">
                        </el-input>
                    </div>
                </el-col> -->
                <!-- <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">出行时间:</span>
                        <el-input
                            size="small"
                            placeholder="请输入出行时间"
                            prefix-icon="el-icon-time"
                            v-model="search.duration">
                        </el-input>
                    </div>
                </el-col> -->
                <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
                    <div class="search-item price-range">
                        <span class="search-label">价格区间:</span>
                        <div class="price-inputs">
                            <el-input-number
                                size="small"
                                placeholder="最低价"
                                controls-position="right"
                                :min="0"
                                v-model="search.minPrice"
                                :value="search.minPrice || undefined">
                            </el-input-number>
                            <span class="price-separator">-</span>
                            <el-input-number
                                size="small"
                                placeholder="最高价"
                                controls-position="right"
                                :min="0"
                                v-model="search.maxPrice"
                                :value="search.maxPrice || undefined">
                            </el-input-number>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
                    <div class="search-item">
                        <span class="search-label">状态:</span>
                        <el-select
                            size="small"
                            placeholder="请选择状态"
                            v-model="search.state"
                            class="status-select">
                            <el-option label="全部" :value="null"></el-option>
                            <el-option label="启用" :value="1"></el-option>
                            <el-option label="禁用" :value="0"></el-option>
                        </el-select>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="12" :md="16" :lg="6" :xl="6">
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
                <el-button type="primary" size="small" icon="el-icon-plus" @click="add">新增线路</el-button>
                <el-button 
                    type="success" 
                    size="small" 
                    icon="el-icon-edit" 
                    :disabled="update.length !== 1" 
                    @click="updateDataBtn">编辑线路</el-button>
                <el-button 
                    type="danger" 
                    size="small" 
                    icon="el-icon-delete" 
                    :disabled="update.length <= 0" 
                    @click="deleteDataBtn">删除线路</el-button>
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
                  label="名称"
                  min-width="120"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="geography"
                  label="目的地"
                  min-width="100"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  prop="duration"
                  label="出行时间"
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
                  prop="state"
                  label="状态"
                  width="80"
                  align="center">
                  <template slot-scope="scope">
                    <el-tag :type="scope.row.state === 1 ? 'success' : 'danger'" effect="dark" size="small">
                      {{ scope.row.state === 1 ? '启用' : '禁用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="createTime"
                  label="创建时间"
                  min-width="160"
                  show-overflow-tooltip>
                </el-table-column>
                <el-table-column
                  label="操作"
                  width="240"
                  fixed="right">
                  <template slot-scope="scope">
                    <div class="action-buttons">
                      <el-button size="mini" type="primary" plain icon="el-icon-edit" @click="updateData(scope.row.id)">编辑</el-button>
                      <el-button 
                        size="mini" 
                        :type="scope.row.state === 1 ? 'warning' : 'success'"
                        plain
                        :icon="scope.row.state === 1 ? 'el-icon-turn-off' : 'el-icon-open'"
                        @click="toggleState(scope.row.id, scope.row.state === 1 ? 0 : 1)">
                        {{ scope.row.state === 1 ? '禁用' : '启用' }}
                      </el-button>
                      <el-popconfirm
                        confirm-button-text='确认'
                        cancel-button-text='取消'
                        icon="el-icon-info"
                        icon-color="red"
                        title="确认删除选中的数据？"
                        @confirm="deleteDate(scope.row.id)"
                      >
                        <el-button size="mini" slot="reference" type="danger" plain icon="el-icon-delete">删除</el-button>
                      </el-popconfirm>
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
    <add @addFalse="addFalse" :addVisible="addVisible"></add>
    <update @updateFalse="updateFalse" :updateId="updateId" :updateVisible="updateVisible"></update>
  </div>
</template>

<script>
  import {getSysLinePage, removeSysLine, toggleSysLineState} from '../../../api/api' 
  import add from '../../../components/system/line/addLine'
  import update from '../../../components/system/line/updateLine'
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
            name: "",
            geography: "",
            duration: "",
            minPrice: null,
            maxPrice: null,
            state: null,
            pageNumber: 1,
            pageSize:10
        },
        total: 0,
        tableData: []
      }
    },
    components: {
      add,
      update
    },
    methods: {
        searchPage() {
            console.log('执行查询操作，重置页码为1');
            this.search.pageNumber = 1;
            this.query();
        },
      query() {
        console.log("开始查询线路数据，参数:", this.search);
        this.loading = true;
        
        // 创建查询参数，添加所有搜索条件
        const queryParams = {
          pageNumber: this.search.pageNumber || 1,
          pageSize: this.search.pageSize || 10,
          name: this.search.name || "",
          geography: this.search.geography || "",
          duration: this.search.duration || ""
        };
        
        // 只有当值有效且非零时才添加价格区间条件
        if (this.search.minPrice !== null && this.search.minPrice !== undefined && this.search.minPrice > 0) {
          queryParams.minPrice = this.search.minPrice;
        }
        
        if (this.search.maxPrice !== null && this.search.maxPrice !== undefined && this.search.maxPrice > 0) {
          queryParams.maxPrice = this.search.maxPrice;
        }
        
        if (this.search.state !== null && this.search.state !== undefined) {
          queryParams.state = this.search.state;
        }
        
        console.log("实际发送的查询参数:", queryParams);
        
        getSysLinePage(queryParams).then(res => {
          console.log("线路数据查询结果:", res);
          if(res.code == 1000) {
            if (res.data && res.data.records) {
              this.tableData = res.data.records;
              this.total = res.data.total;
              console.log("更新表格数据, 记录数:", this.tableData.length);
            } else {
              console.error("返回数据结构异常:", res.data);
              this.tableData = [];
              this.total = 0;
            }
            this.loading = false;
          } else {
            this.loading = false;
            console.error("查询返回错误码:", res.code, res.message);
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        }).catch(err => {
          console.error("查询线路数据出错:", err);
          this.loading = false;
          this.$notify.error({
            title: '错误',
            message: '查询数据失败'
          });
        });
      },
      refresh() {
        console.log('重置查询条件并刷新数据');
        this.search = {
          name: "",
          geography: "",
          duration: "",
          minPrice: null,
          maxPrice: null,
          state: null,
          pageNumber: 1,
          pageSize: 10
        };
        this.loading = true;
        this.tableData = [];
        this.total = 0;
        this.query();
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
      add() {
        this.addVisible = true
      },
      addFalse(needRefresh) {
        console.log('关闭新增对话框，是否需要刷新:', needRefresh);
        this.addVisible = false;
        if (needRefresh) {
          console.log('正在刷新数据...');
          this.refresh();
        } else {
          this.query();
        }
      },
      updateFalse(needRefresh) {
        console.log('关闭编辑对话框，是否需要刷新:', needRefresh);
        this.updateId = '';
        this.updateVisible = false;
        if (needRefresh) {
          console.log('正在刷新数据...');
          this.refresh();
        } else {
          this.query();
        }
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
        removeSysLine({ids:ids}).then(res => {
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
          })
      },
      toggleState(id, state) {
        toggleSysLineState({id: id, state: state}).then(res => {
          if(res.code == 1000) {
            this.$message({
              type: 'success',
              message: state === 1 ? '启用成功!' : '禁用成功!'
            });
            this.query()
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        })
      }
    },
    mounted() {
      this.query()
    }
 }
</script>

<style lang="scss" scoped>
.line-management {
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
    }
    
    .price-range {
      .price-inputs {
        display: flex;
        align-items: center;
        
        .price-separator {
          margin: 0 5px;
          color: #606266;
        }
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
    
    .status-select {
      width: 100%;
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
    
    .price-value {
      color: #67c23a;
      font-weight: 500;
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

::v-deep .el-input-number {
  width: 120px;
}

::v-deep .el-table .cell {
  white-space: nowrap;
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
  
  .price-range .price-inputs {
    flex-direction: column;
    align-items: flex-start;
    
    .price-separator {
      margin: 5px 0;
    }
    
    .el-input-number {
      width: 100%;
    }
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
  white-space: nowrap;
  flex-wrap: nowrap;
  
  .el-button {
    margin-right: 5px;
    padding: 5px 8px;
    
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
