<template>
  <div class="admin-management">
    <!-- 页面标题区域已删除 -->
    
    <div class="search-table-container">
      <!-- 搜索区域 -->
      <div class="search-panel">
        <el-row :gutter="20" class="search-row">
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">用户名称:</span>
              <el-input
                size="small"
                placeholder="请输入用户名称"
                prefix-icon="el-icon-user"
                v-model="search.userName">
              </el-input>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">手机号码:</span>
              <el-input
                size="small"
                placeholder="请输入手机号码"
                prefix-icon="el-icon-mobile-phone"
                v-model="search.tel">
              </el-input>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-item">
              <span class="search-label">状态:</span>
              <el-select 
                size="small"
                clearable 
                v-model="search.status" 
                placeholder="请选择状态"
                class="status-select">
                <el-option label="正常" value="0"></el-option>
                <el-option label="停用" value="1"></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="6" :xl="6">
            <div class="search-buttons">
              <el-button size="small" type="primary" icon="el-icon-search" @click="query">查 询</el-button>
              <el-button size="small" icon="el-icon-refresh" @click="refresh">重 置</el-button>
            </div>
          </el-col>
        </el-row>
      </div>
      
      <!-- 表格区域 -->
      <div class="table-panel">
        <div class="table-toolbar">
          <el-button type="primary" size="small" icon="el-icon-plus" @click="addUser">新增管理员</el-button>
          <el-button 
            type="success" 
            size="small" 
            icon="el-icon-edit" 
            :disabled="update.length !== 1" 
            @click="updateUserBtn">编辑管理员</el-button>
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
            prop="userName"
            label="用户名称"
            min-width="120"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="loginAccount"
            label="登录账号"
            min-width="120"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="tel"
            label="手机号"
            min-width="120"
            show-overflow-tooltip>
          </el-table-column>
          <el-table-column
            prop="status"
            label="状态"
            width="80"
            align="center">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status == 0 ? 'success' : 'warning'" effect="dark" size="small">
                {{ scope.row.status == 0 ? '正常' : '停用' }}
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
            width="180"
            fixed="right">
            <template slot-scope="scope">
              <div class="action-buttons">
                <el-button size="mini" type="primary" plain icon="el-icon-edit" @click="updateUser(scope.row.id)">编辑</el-button>
                <el-dropdown trigger="click" @command="handleCommand" size="mini">
                  <el-button size="mini" type="info" plain>
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-key" v-if="scope.row.loginAccount != 'admin'" :command="scope.row.id + '#pass#' + scope.row.userName">重置密码</el-dropdown-item>
                    <el-dropdown-item icon="el-icon-delete" v-if="scope.row.loginAccount != 'admin'" :command="scope.row.id + '#remove'">删除账户</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
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
    
    <add-user @addUserFalse="addUserFalse" :addUserVisible="addUserVisible"></add-user>
    <update-user @updateUserFalse="updateUserFalse" :updateId="updateId" :updateUserVisible="updateUserVisible"></update-user>
    
    <!-- 重置密码对话框 -->
    <el-dialog
      title="重置密码"
      :visible.sync="passwordDialogVisible"
      width="30%"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :before-close="handlePasswordClose">
      <div class="password-dialog-content">
        <p class="password-dialog-text">请输入 <span class="user-name">{{userName}}</span> 的新密码：</p>
        <el-input 
          v-model="newPassword" 
          show-password 
          size="small" 
          placeholder="请输入新密码" 
          prefix-icon="el-icon-lock"
          autocomplete="off">
        </el-input>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="handlePasswordClose">取 消</el-button>
        <el-button size="small" type="primary" @click="passwordSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import {getUserPage,removeUser,resetPassword} from '../../../api/api' 
  import addUser from "../../../components/system/admin/addAdmin"
  import updateUser from "../../../components/system/admin/updateAdmin"
  export default {
    data() {
      return{
        loading: true,
        update: [],
        remove: [],
        updateId: "",
        addUserVisible: false,
        updateUserVisible: false,
        passwordDialogVisible: false,
        search: {
            userName: "",
            tel: "",
            status: "",
            userType: 0,
            pageNumber: 1,
            pageSize:10
        },
        userName: "",
        userId: "",
        newPassword: "",
        total: 0,
        tableData: [],
        data: [],
        post: [],
        role: [],
      }
    },
    components: {
      addUser,
      updateUser
    },
    methods: {
      query() {
        this.loading = true;
        getUserPage(this.search).then(res => {
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
        this.search.userName = ""
        this.search.tel = ""
        this.search.status = ""
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
      addUser() {
        this.addUserVisible = true
      },
      addUserFalse() {
        this.addUserVisible = false
        this.query()
      },
      updateUser(id) {
        this.updateId = id
        this.updateUserVisible = true
      },
      updateUserFalse() {
        this.updateUserVisible = false
        this.updateId = ""
        this.query()
      },
      updateUserBtn() {
        this.updateUser(this.update[0])
      },
      handleCommand(command) {
        var data = command.split("#")
        if(data[1] == 'edit') {
          this.updateUser(data[0])
        } else if(data[1] == 'pass') {
          this.userName = data[2]
          this.openPassword(data[0])
        } else {
          this.$confirm('确定删除选中的数据?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.deleteDate(data[0])
        }).catch(() => {
                 
        });
        }
      },
      deleteDateBtn() {
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
        removeUser({ids:ids}).then(res => {
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
      openPassword(id) {
        this.userId = id
        this.passwordDialogVisible = true
      },
      handlePasswordClose() {
        this.userName = ""
        this.userId = ""
        this.newPassword = ""
        this.passwordDialogVisible = false
      },
      passwordSubmit() {
        if(!this.newPassword.trim()) {
          this.$message({
            message: '请输入新密码',
            type: 'warning'
          });
          return;
        }
        resetPassword({id: this.userId,newPassword: this.newPassword}).then(res => {
          if(res.code == 1000) {
            this.$notify.success({
                  title: '成功',
                  message: "重置成功"
                });
            this.handlePasswordClose()
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
.admin-management {
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
      .el-button {
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
  
  /* 密码对话框 */
  .password-dialog-content {
    padding: 10px 0;
    
    .password-dialog-text {
      margin-bottom: 15px;
      font-size: 14px;
      
      .user-name {
        font-weight: 600;
        color: #6366f1;
      }
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