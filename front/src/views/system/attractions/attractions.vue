<template>
  <div class="attractions-management">
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
                            placeholder="请输入景点名称"
                            prefix-icon="el-icon-search"
                        v-model="search.name">
                    </el-input>
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
                <el-button type="primary" size="small" icon="el-icon-plus" @click="add">新增景点</el-button>
                <el-button 
                    type="success" 
                    size="small" 
                    icon="el-icon-edit" 
                    :disabled="update.length !== 1" 
                    @click="updateDataBtn">编辑景点</el-button>
                <el-button 
                    type="danger" 
                    size="small" 
                    icon="el-icon-delete" 
                    :disabled="update.length <= 0" 
                    @click="deleteDataBtn">删除景点</el-button>
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
              prop="price"
              label="价格"
                  min-width="80"
                  align="right">
                  <template slot-scope="scope">
                    <span class="price-value">{{ scope.row.price ? '¥' + scope.row.price : '-' }}</span>
                  </template>
            </el-table-column>
            <el-table-column
              prop="introduce"
              label="简介"
                  min-width="180"
                  show-overflow-tooltip>
            </el-table-column>
            <el-table-column
              prop="num"
              label="库存"
                  min-width="80"
                  align="center">
            </el-table-column>
            <el-table-column
              prop="state"
              label="状态"
                  width="80"
                  align="center">
              <template slot-scope="scope">
                    <el-tag :type="scope.row.state === 1 ? 'success' : 'info'" effect="dark" size="small">
                      {{ scope.row.state === 1 ? '上架' : '下架' }}
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
                  width="280">
              <template slot-scope="scope">
                    <div class="action-buttons">
                      <el-button 
                        size="mini" 
                        :type="scope.row.state === 0 ? 'success' : 'warning'"
                        plain
                        @click="toEdit(scope.row.id, scope.row.state === 0 ? 1 : 0)">
                        {{ scope.row.state === 0 ? '上架' : '下架' }}
                      </el-button>
                      <el-button size="mini" type="info" plain @click="toComment(scope.row.id)">评论</el-button>
                      <el-button size="mini" type="primary" plain @click="updateData(scope.row.id)">编辑</el-button>
                      <el-button size="mini" type="danger" plain @click="deleteConfirm(scope.row.id)">删除</el-button>
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
  import add from '../../../components/system/attractions/addAttractions'
  import update from '../../../components/system/attractions/updateAttractions'
  import {getSysAttractionsPage,removeSysAttractions,editSysAttractions} from '../../../api/api' 
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
      toEdit(id,state) {
        var param = {
          id: id,
          state: state
        }
        editSysAttractions(param).then(res => {
          if(res.code == 1000) {
            this.$message({
              type: 'success',
              message: state === 1 ? '上架成功!' : '下架成功!'
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
      toComment(id) {
        var param = {
            "name": "景点评论",
            "url": "/comments?id=" + id
        }
        this.$store.commit('menu/addActiveMenu', param)
        this.$router.push("/comments?id=" + id)
        this.$store.commit('menu/setActiveMenu', "/comments?id=" + id)
      },
      deleteConfirm(id) {
        this.$confirm('确认删除该景点数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDate(id)
        }).catch(() => {})
      },
      searchPage() {
        this.search.pageNumber = 1
        this.query()
      },
      query() {
        this.loading = true;
        getSysAttractionsPage(this.search).then(res => {
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
        this.search.name = ""
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
      add() {
        this.addVisible = true
      },
      addFalse() {
        this.addVisible = false
        this.query()
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
        removeSysAttractions({ids:ids}).then(res => {
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
    },
    mounted() {
      this.query()
    }
 }
</script>

<style lang="scss" scoped>
.attractions-management {
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
        
        // 为新增景点按钮添加默认状态的背景和边框颜色
        &--primary {
          background-color: #409eff;
          border: 2px solid #409eff;
          color: #ffffff;
          
          &:hover {
            background-color: #66b1ff;
            border-color: #66b1ff;
          }
          
          &:focus {
            background-color: #409eff;
            border-color: #409eff;
          }
        }
        
        // 为编辑按钮添加默认状态的背景和边框颜色
        &--success {
          background-color: #67c23a;
          border: 2px solid #67c23a;
          color: #ffffff;
          
          &:hover {
            background-color: #85ce61;
            border-color: #85ce61;
          }
          
          &:focus {
            background-color: #67c23a;
            border-color: #67c23a;
          }
        }
        
        // 为删除按钮添加默认状态的背景和边框颜色
        &--danger {
          background-color: #f56c6c;
          border: 2px solid #f56c6c;
          color: #ffffff;
          
          &:hover {
            background-color: #f78989;
            border-color: #f78989;
          }
          
          &:focus {
            background-color: #f56c6c;
            border-color: #f56c6c;
          }
        }
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