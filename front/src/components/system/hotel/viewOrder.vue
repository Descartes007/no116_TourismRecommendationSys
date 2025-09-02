<template>
<div>
  <el-dialog title="编辑酒店预约" width="60%" :destroy-on-close="true" :visible.sync="updateVisible" :before-close="handleClose" custom-class="hotel-order-dialog">
    <el-form :model="form" :rules="rules" ref="ruleForm" label-position="top" class="order-form">
      <!-- 基本信息 -->
      <div class="form-section">
        <div class="section-title">
          <i class="el-icon-info"></i>
          <span>基本信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="酒店名称" prop="name">
              <el-input v-model="form.name" size="small" placeholder="请输入酒店名称"></el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="预约状态" prop="state">
              <el-select v-model="form.state" size="small" placeholder="请选择状态" class="full-width">
                <el-option label="未确认" value="0"></el-option>
                <el-option label="已确认" value="1"></el-option>
                <el-option label="取消中" value="2"></el-option>
                <el-option label="已取消" value="3"></el-option>
                <el-option label="取消失败" value="4"></el-option>
                <el-option label="已使用" value="5"></el-option>
              </el-select>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="预约时间" prop="time">
              <el-date-picker
                size="small"
                v-model="form.time"
                type="date"
                placeholder="选择预约时间"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
        </el-col>
        </el-row>
      </div>
      
      <!-- 房间信息 -->
      <div class="form-section">
        <div class="section-title">
          <i class="el-icon-house"></i>
          <span>房间信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="房型名称" prop="itemName">
              <el-input v-model="form.itemName" size="small" placeholder="请输入房型名称"></el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="价格" prop="price">
              <el-input v-model="form.price" size="small" placeholder="请输入价格">
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8">
            <el-form-item label="预约人数" prop="num">
              <el-input-number v-model="form.num" :min="1" :max="20" size="small" style="width: 100%"></el-input-number>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <el-form-item label="酒店简介" prop="introduce">
              <el-input type="textarea" v-model="form.introduce" size="small" placeholder="请输入酒店简介" :rows="3"></el-input>
            </el-form-item>
        </el-col>
        </el-row>
      </div>
      
      <!-- 联系人信息 -->
      <div class="form-section">
        <div class="section-title">
          <i class="el-icon-user"></i>
          <span>联系人信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
            <el-form-item label="联系人信息" prop="people">
              <div v-if="parsedPeople" class="contact-info">
                <div class="contact-header">
                  <span class="contact-title">预约联系人信息</span>
                  <el-button type="text" size="mini" @click="switchToEditMode" v-if="!editingContacts">
                    <i class="el-icon-edit"></i> 编辑联系人
                  </el-button>
                </div>
                
                <!-- 查看模式 -->
                <div v-if="!editingContacts" class="contact-list">
                  <div class="contact-item" v-for="(item, index) in parsedPeople" :key="index">
                    <div class="contact-field">
                      <span class="contact-label"><i class="el-icon-user"></i> 姓名：</span>
                      <span class="contact-value">{{item.name || '未提供'}}</span>
                    </div>
                    <div class="contact-field">
                      <span class="contact-label"><i class="el-icon-mobile-phone"></i> 电话：</span>
                      <span class="contact-value">{{item.phone || item.tel || '未提供'}}</span>
                    </div>
                    <div class="contact-field" v-if="item.idCard">
                      <span class="contact-label"><i class="el-icon-document"></i> 身份证：</span>
                      <span class="contact-value">{{item.idCard}}</span>
                    </div>
                  </div>
                </div>
                
                <!-- 编辑模式 -->
                <div v-else class="contact-edit-form">
                  <div class="contact-item-edit" v-for="(item, index) in editableContacts" :key="index">
                    <div class="contact-edit-header">
                      <span class="contact-edit-title">联系人 #{{index + 1}}</span>
                      <el-button 
                        v-if="editableContacts.length > 1" 
                        type="danger" 
                        size="mini" 
                        icon="el-icon-delete" 
                        circle 
                        @click="removeContact(index)">
                      </el-button>
                    </div>
                    <el-row :gutter="12">
                      <el-col :span="8">
                        <el-form-item label="姓名" :prop="'contacts.' + index + '.name'">
                          <el-input v-model="item.name" placeholder="联系人姓名"></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item label="电话" :prop="'contacts.' + index + '.phone'">
                          <el-input v-model="item.phone" placeholder="联系人电话"></el-input>
                        </el-form-item>
                      </el-col>
                      <el-col :span="8">
                        <el-form-item label="身份证" :prop="'contacts.' + index + '.idCard'">
                          <el-input v-model="item.idCard" placeholder="身份证号码"></el-input>
                        </el-form-item>
                      </el-col>
                    </el-row>
                  </div>
                  
                  <div class="contact-actions">
                    <el-button type="primary" size="small" @click="saveContacts">保存联系人</el-button>
                    <el-button size="small" @click="cancelEdit">取消</el-button>
                    <el-button type="success" size="small" icon="el-icon-plus" @click="addContact">添加联系人</el-button>
                  </div>
                </div>
              </div>
              <el-input v-else type="textarea" v-model="form.people" size="small" placeholder="联系人信息" :rows="3"></el-input>
            </el-form-item>
        </el-col>
        </el-row>
      </div>
      
      <!-- 系统信息 -->
      <div class="form-section">
        <div class="section-title">
          <i class="el-icon-setting"></i>
          <span>系统信息</span>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <el-form-item label="酒店ID" prop="hotelId">
              <el-input v-model="form.hotelId" size="small" placeholder="酒店ID" disabled></el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <el-form-item label="房型ID" prop="itemId">
              <el-input v-model="form.itemId" size="small" placeholder="房型ID" disabled></el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" size="small" placeholder="用户ID" disabled></el-input>
            </el-form-item>
        </el-col>
          <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
            <el-form-item label="图片" prop="images">
              <el-input v-model="form.images" size="small" placeholder="图片链接" disabled></el-input>
            </el-form-item>
        </el-col>
      </el-row>
      </div>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button size="small" type="primary" @click="submit">保 存</el-button>
      <el-button size="small" @click="handleClose">取 消</el-button>
    </div>
  </el-dialog>
</div>
</template>

<script>
  import {editSysHotelOrder,getSysHotelOrderById} from '../../../api/api'
  export default {
    data() {
      return{
        formLabelWidth: '80px',
        form: {
          hotelId: "",
          name: "",
          introduce: "",
          images: "",
          num: "",
          time: "",
          people: "",
          state: "",
          userId: "",
          itemId: "",
          itemName: "",
          price: "",
        },
        rules: {
          name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }],
          state: [{ required: true, message: '请选择预约状态', trigger: 'change' }],
          time: [{ required: true, message: '请选择预约时间', trigger: 'change' }],
          num: [{ required: true, message: '请输入预约人数', trigger: 'blur' }]
        },
        parsedPeople: null,
        editingContacts: false,
        editableContacts: []
      }
    },
    props: ['updateVisible','updateId'],
    methods: {
      submit() {
        this.$refs["ruleForm"].validate((valid) => {
          if (valid) {
            // 确保数字类型正确
            const submitForm = {...this.form};
            submitForm.num = parseInt(submitForm.num);
            submitForm.state = parseInt(submitForm.state);
            
            // 格式化联系人信息
            if (this.parsedPeople) {
              submitForm.people = this.formatContactInfo();
            }
            
            editSysHotelOrder(submitForm).then(res => {
              if(res.code == 1000) {
                this.$notify.success({
                  title: '成功',
                  message: "保存成功"
                });
                this.handleClose();
              } else {
                this.$notify.error({
                  title: '错误',
                  message: res.message
                });
              }
            }).catch(err => {
              this.$notify.error({
                title: '错误',
                message: '保存失败，请稍后再试'
              });
            });
          } else {
            return false;
          }
        });
      },
      handleClose() {
        this.$emit("updateFalse")
      },
      tryParseJson(jsonString) {
        try {
          if (jsonString) {
            return JSON.parse(jsonString);
          }
        } catch (e) {
          console.error('解析JSON失败:', e);
        }
        return null;
      },
      processPeopleData() {
        if (this.form.people) {
          // 尝试标准JSON解析
          let parsed = this.tryParseJson(this.form.people);
          
          // 如果标准解析失败，尝试修复常见格式问题
          if (!parsed) {
            try {
              // 处理单引号替换为双引号的情况
              const formattedStr = this.form.people
                .replace(/'/g, '"')
                .replace(/([{,]\s*)(\w+)(\s*:)/g, '$1"$2"$3'); // 确保键名有引号
              parsed = JSON.parse(formattedStr);
            } catch (e) {
              console.error("修复后JSON解析仍然失败:", e);
              
              // 尝试使用正则表达式提取信息
              parsed = this.extractContactInfoWithRegex(this.form.people);
            }
          }
          
          // 处理解析结果
          if (parsed) {
            // 如果是数组，取第一个元素
            if (Array.isArray(parsed)) {
              this.parsedPeople = parsed;
            } 
            // 如果是单个对象
            else if (typeof parsed === 'object') {
              this.parsedPeople = [parsed];
            } else {
              this.parsedPeople = null;
            }
          } else {
            this.parsedPeople = null;
          }
        } else {
          this.parsedPeople = null;
        }
      },
      
      // 添加一个专门用于正则提取的辅助方法
      extractContactInfoWithRegex(str) {
        console.log("使用正则表达式提取联系信息:", str);
        
        // 尝试匹配姓名、电话和身份证
        const nameMatch = str.match(/"?name"?\s*:\s*"?([^",]+)"?/) || 
                          str.match(/姓名\s*[：:]\s*([^\s,，:：]+)/) ||
                          str.match(/联系人\s*[：:]\s*([^\s,，:：]+)/);
                          
        const phoneMatch = str.match(/"?tel"?\s*:\s*"?([^",]+)"?/) || 
                          str.match(/"?phone"?\s*:\s*"?([^",]+)"?/) ||
                          str.match(/电话\s*[：:]\s*([^\s,，:：]+)/) ||
                          str.match(/手机\s*[：:]\s*([^\s,，:：]+)/) ||
                          str.match(/1[3-9]\d{9}/); // 匹配手机号
                          
        const idCardMatch = str.match(/"?idCard"?\s*:\s*"?([^",]+)"?/) || 
                            str.match(/身份证\s*[：:]\s*([^\s,，:：]+)/) ||
                            str.match(/\d{17}[\dXx]/); // 匹配身份证号
        
        const contactInfo = {
          name: nameMatch ? nameMatch[1].trim() : "未提供",
          phone: phoneMatch ? phoneMatch[1].trim() : "未提供",
          idCard: idCardMatch ? idCardMatch[1].trim() : "未提供"
        };
        
        console.log("正则提取结果:", contactInfo);
        return [contactInfo];
      },
      
      // 格式化联系人信息为JSON
      formatContactInfo() {
        if (this.parsedPeople && this.parsedPeople.length > 0) {
          // 将当前解析的联系人信息格式化为标准JSON
          const formatted = JSON.stringify(this.parsedPeople);
          this.form.people = formatted;
          return formatted;
        }
        return this.form.people;
      },

      // 添加联系人信息编辑相关方法
      switchToEditMode() {
        this.editableContacts = JSON.parse(JSON.stringify(this.parsedPeople || []));
        if (this.editableContacts.length === 0) {
          this.addContact();
        }
        this.editingContacts = true;
      },
      
      addContact() {
        this.editableContacts.push({
          name: "",
          phone: "",
          idCard: ""
        });
      },
      
      removeContact(index) {
        this.editableContacts.splice(index, 1);
      },
      
      saveContacts() {
        // 验证联系人信息
        let isValid = true;
        
        for (let i = 0; i < this.editableContacts.length; i++) {
          const contact = this.editableContacts[i];
          if (!contact.name || !contact.phone) {
            this.$notify.warning({
              title: '验证失败',
              message: `联系人 #${i+1} 的姓名和电话不能为空`
            });
            isValid = false;
            break;
          }
        }
        
        if (!isValid) return;
        
        // 保存编辑后的联系人信息
        this.parsedPeople = JSON.parse(JSON.stringify(this.editableContacts));
        this.form.people = JSON.stringify(this.parsedPeople);
        this.editingContacts = false;
        
        this.$notify.success({
          title: '成功',
          message: '联系人信息已更新'
        });
      },
      
      cancelEdit() {
        this.editingContacts = false;
        this.editableContacts = [];
      }
    },
    watch: {
      updateId(newVal) {
        if(newVal) {
          getSysHotelOrderById({id:newVal}).then(res => {
            if(res.code == 1000) {
              this.form = res.data;
              // 确保表单数据类型正确
              this.form.num = res.data.num + "";
              this.form.state = res.data.state + "";
              // 处理联系人信息
              this.processPeopleData();
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message
              });
            }
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: '获取预约详情失败'
            });
          });
        }
      },
      'form.people': {
        handler() {
          this.processPeopleData();
        }
      }
    }
 }
</script>

<style lang="scss" scoped>
.hotel-order-dialog {
  ::v-deep .el-dialog__header {
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;
    background-color: #f5f7fa;
    border-radius: 8px 8px 0 0;
  }
  
  ::v-deep .el-dialog__title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  
  ::v-deep .el-dialog__body {
    padding: 20px;
  }
  
  ::v-deep .el-dialog__footer {
    padding: 15px 20px;
    border-top: 1px solid #f0f0f0;
    background-color: #f5f7fa;
    border-radius: 0 0 8px 8px;
  }
}

.order-form {
  .form-section {
    margin-bottom: 25px;
    padding-bottom: 15px;
    border-bottom: 1px dashed #ebeef5;
    
    &:last-child {
      margin-bottom: 0;
      border-bottom: none;
    }
    
    .section-title {
      margin-bottom: 15px;
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      display: flex;
      align-items: center;
      
      i {
        margin-right: 8px;
        color: #409EFF;
      }
    }
  }
  
  ::v-deep .el-form-item {
    margin-bottom: 18px;
    
    .el-form-item__label {
      padding-bottom: 5px;
      line-height: 1.2;
      color: #606266;
      font-weight: 500;
    }
    
    .el-form-item__error {
      display: none;
    }
  }
  
  .full-width {
    width: 100%;
  }
  
  .contact-info {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 15px;
    background-color: #f9f9f9;
    
    .contact-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      border-bottom: 1px solid #ebeef5;
      padding-bottom: 10px;
      
      .contact-title {
        font-weight: 600;
        color: #303133;
      font-size: 14px;
      }
    }
    
    .contact-list {
      .contact-item {
        padding: 10px;
        border-radius: 4px;
        background-color: white;
        margin-bottom: 10px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .contact-field {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          
          &:last-child {
            margin-bottom: 0;
          }
          
          .contact-label {
            font-weight: 500;
            color: #606266;
            margin-right: 8px;
            min-width: 70px;
            
            i {
              margin-right: 5px;
              color: #409EFF;
  }
          }
          
          .contact-value {
            color: #303133;
            flex: 1;
          }
        }
      }
    }
    
    .contact-edit-form {
      .contact-item-edit {
        background-color: white;
        border-radius: 4px;
        padding: 15px;
        margin-bottom: 15px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
        
        .contact-edit-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
          
          .contact-edit-title {
            font-weight: 500;
            color: #303133;
          }
        }
      }
      
      .contact-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 15px;
        
        .el-button {
          margin-left: 10px;
        }
      }
    }
  }
}

::v-deep .el-select {
  width: 100%;
}

::v-deep .el-input-number .el-input__inner {
  text-align: left;
  }
::v-deep .el-input-group__prepend {
  background-color: #f5f7fa;
  color: #909399;
}

// 为确认按钮添加蓝色背景样式
::v-deep .dialog-footer {
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

// 为保存联系人按钮添加蓝色背景样式
.contact-actions {
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
</style>