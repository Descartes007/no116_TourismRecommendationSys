<template>
<div>
  <el-dialog 
    title="编辑景点预约" 
    width="50%" 
    :destroy-on-close="true" 
    :visible.sync="updateVisible" 
    :before-close="handleClose"
    custom-class="attraction-order-dialog">
    
    <div class="compact-layout">
      <el-form :model="form" :rules="rules" ref="ruleForm" label-position="left" :show-message="false" size="small" class="compact-form" label-width="80px">
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="景点名称" prop="name" class="compact-form-item">
              <el-input v-model="form.name" size="small" placeholder="请输入景点名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="景点ID" prop="attractionsId" class="compact-form-item">
              <el-input v-model="form.attractionsId" size="small" placeholder="景点ID"></el-input>
            </el-form-item>
        </el-col>
        </el-row>
        
        <el-row :gutter="12">
          <el-col :span="24">
            <el-form-item label="景点简介" prop="introduce" class="compact-form-item">
              <el-input v-model="form.introduce" type="textarea" :rows="2" size="small" placeholder="景点简介"></el-input>
            </el-form-item>
        </el-col>
        </el-row>
        
        <el-divider content-position="left" class="compact-divider"><span class="divider-text">预约信息</span></el-divider>
        
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="预约人数" prop="num" class="compact-form-item">
              <el-input-number v-model="form.num" :min="1" size="small" controls-position="right" style="width: 100%"></el-input-number>
            </el-form-item>
        </el-col>
          <el-col :span="8">
            <el-form-item label="预约时间" prop="time" class="compact-form-item">
              <el-date-picker size="small" v-model="form.time" type="date" value-format="yyyy-MM-dd" placeholder="预约时间" style="width: 100%"></el-date-picker>
            </el-form-item>
        </el-col>
          <el-col :span="8">
            <el-form-item label="预约状态" prop="state" class="compact-form-item">
              <el-select v-model="form.state" size="small" placeholder="请选择状态" style="width: 100%">
                <el-option label="未确认" value="0"></el-option>
                <el-option label="已确认" value="1"></el-option>
                <el-option label="取消中" value="2"></el-option>
                <el-option label="已取消" value="3"></el-option>
                <el-option label="取消失败" value="4"></el-option>
                <el-option label="已使用" value="5"></el-option>
              </el-select>
            </el-form-item>
        </el-col>
        </el-row>
        
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId" class="compact-form-item">
              <el-input v-model="form.userId" size="small" placeholder="用户ID"></el-input>
            </el-form-item>
        </el-col>
        </el-row>
        
        <el-divider content-position="left" class="compact-divider"><span class="divider-text">联系人信息</span></el-divider>
        
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName" class="compact-form-item">
              <el-input v-model="contactInfo.name" size="small" placeholder="联系人姓名" @input="updateContactInfo"></el-input>
            </el-form-item>
        </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone" class="compact-form-item">
              <el-input v-model="contactInfo.tel" size="small" placeholder="联系人电话" @input="updateContactInfo"></el-input>
            </el-form-item>
        </el-col>
          <el-col :span="8" v-if="showIdCard">
            <el-form-item label="身份证号" prop="contactIdCard" class="compact-form-item">
              <el-input v-model="contactInfo.idCard" size="small" placeholder="身份证号码" @input="updateContactInfo"></el-input>
            </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    </div>
    
    <div slot="footer" class="dialog-footer">
      <el-button size="small" @click="handleClose" plain>取 消</el-button>
      <el-button size="small" type="primary" @click="submit" icon="el-icon-check">保 存</el-button>
    </div>
  </el-dialog>
</div>
</template>

<script>
  import {editSysAttractionOrder,getSysAttractionOrderById} from '../../../api/api'
  export default {
    data() {
      return{
        formLabelWidth: '80px',
        loading: false,
        form: {
          attractionsId: "",
          name: "",
          introduce: "",
          images: "",
          num: "",
          time: "",
          people: "",
          state: "",
          userId: "",
        },
        contactInfo: {
          name: "",
          tel: "",
          idCard: ""
        },
        showIdCard: false,
        rules: {} // 清空验证规则
      }
    },
    props: ['updateVisible','updateId'],
    methods: {
      // 解析联系信息JSON字符串
      parseContactInfo(jsonStr) {
        try {
          // 如果没有数据，初始化为空对象
          if (!jsonStr) {
            this.contactInfo = {
              name: "",
              tel: "",
              idCard: ""
            };
            return;
          }
          
          // 尝试解析JSON字符串
          let contactObj = null;
          
          // 处理可能的格式问题
          if (typeof jsonStr === 'string') {
            // 检查是否是JSON字符串
            if (jsonStr.trim().startsWith('{') && jsonStr.trim().endsWith('}')) {
              // 替换单引号为双引号
              const formattedStr = jsonStr.replace(/'/g, '"')
                                          .replace(/([{,]\s*)(\w+)(\s*:)/g, '$1"$2"$3'); // 确保键名有引号
              try {
                contactObj = JSON.parse(formattedStr);
              } catch (e) {
                console.error("标准JSON解析失败:", e);
                
                // 尝试使用正则表达式提取信息
                this.extractContactInfoWithRegex(jsonStr);
                return;
              }
            } else {
              // 非JSON格式，尝试直接使用正则提取
              this.extractContactInfoWithRegex(jsonStr);
              return;
            }
          } else if (typeof jsonStr === 'object') {
            contactObj = jsonStr;
          }
          
          if (contactObj) {
            this.contactInfo.name = contactObj.name || "";
            this.contactInfo.tel = contactObj.tel || "";
            this.contactInfo.idCard = contactObj.idCard || "";
            this.showIdCard = !!contactObj.idCard;
          }
        } catch (error) {
          console.error("解析联系信息失败:", error);
          this.$notify.warning({
            title: '警告',
            message: '联系信息格式异常，请手动修正'
          });
          
          // 确保即使解析失败也有默认值
          this.contactInfo = {
            name: "",
            tel: "",
            idCard: ""
          };
        }
      },
      
      // 添加一个专门用于正则提取的辅助方法
      extractContactInfoWithRegex(str) {
        console.log("使用正则表达式提取联系信息:", str);
        
        // 尝试匹配姓名、电话和身份证
        const nameMatch = str.match(/"?name"?\s*:\s*"?([^",]+)"?/) || 
                          str.match(/姓名\s*[：:]\s*([^\s,，:：]+)/) ||
                          str.match(/联系人\s*[：:]\s*([^\s,，:：]+)/);
                          
        const telMatch = str.match(/"?tel"?\s*:\s*"?([^",]+)"?/) || 
                         str.match(/电话\s*[：:]\s*([^\s,，:：]+)/) ||
                         str.match(/手机\s*[：:]\s*([^\s,，:：]+)/) ||
                         str.match(/1[3-9]\d{9}/); // 匹配手机号
                         
        const idCardMatch = str.match(/"?idCard"?\s*:\s*"?([^",]+)"?/) || 
                            str.match(/身份证\s*[：:]\s*([^\s,，:：]+)/) ||
                            str.match(/\d{17}[\dXx]/); // 匹配身份证号
        
        this.contactInfo = {
          name: nameMatch ? nameMatch[1].trim() : "",
          tel: telMatch ? telMatch[1].trim() : "",
          idCard: idCardMatch ? idCardMatch[1].trim() : ""
        };
        
        this.showIdCard = !!this.contactInfo.idCard;
        
        console.log("提取结果:", this.contactInfo);
      },
      
      // 更新联系信息
      updateContactInfo() {
        // 确保contactInfo各项都有值，避免undefined
        const name = this.contactInfo.name || "";
        const tel = this.contactInfo.tel || "";
        const idCard = this.contactInfo.idCard || "";
        
        const contactObj = {
          name: name,
          tel: tel
        };
        
        if (this.showIdCard && idCard) {
          contactObj.idCard = idCard;
        }
        
        // 更新到表单数据
        this.form.people = JSON.stringify(contactObj);
      },
      
      // 修复提交方法
      submit() {
        // 手动验证联系人信息
        let isValid = true;
        let errorMessage = "";
        
        // 验证联系人姓名
        if (!this.contactInfo.name || this.contactInfo.name.trim() === "") {
          isValid = false;
          errorMessage = "请输入联系人姓名";
        }
        // 验证联系电话
        else if (!this.contactInfo.tel || this.contactInfo.tel.trim() === "") {
          isValid = false;
          errorMessage = "请输入联系电话";
        }
        
        if (!isValid) {
          // 使用通知而不是表单错误
          this.$notify.warning({
            title: '提示',
            message: errorMessage
          });
          return;
        }
        
        // 确保联系信息已更新到form.people
        this.updateContactInfo();
        
        // 直接提交表单，不使用validate
        this.loading = true;
            editSysAttractionOrder(this.form).then(res => {
          this.loading = false;
              if(res.code == 1000) {
                this.$notify.success({
                  title: '成功',
                  message: "保存成功"
                });
                this.handleClose();
              } else {
                this.$notify.error({
                  title: '错误',
              message: res.message || '保存失败'
                });
              }
        }).catch(err => {
          this.loading = false;
          this.$notify.error({
            title: '错误',
            message: '网络异常，请稍后再试'
          });
        });
      },
      
      // 修复关闭方法
      handleClose() {
        console.log("关闭对话框");
        this.$emit("updateFalse");
      },
    },
    created() {
      // 初始化联系信息
      this.contactInfo = {
        name: "",
        tel: "",
        idCard: ""
      };
    },
    mounted() {
      
    },
    watch: {
      updateVisible(newVal) {
        if (newVal) {
          // 重置联系人信息
          this.contactInfo = {
            name: "",
            tel: "",
            idCard: ""
          };
        }
      },
      updateId(newVal) {
        if(newVal) {
          getSysAttractionOrderById({id:newVal}).then(res => {
            if(res.code == 1000) {
              this.form = res.data;
              // 确保数值类型正确
              this.form.num = res.data.num ? parseInt(res.data.num) : 1;
              this.form.state = res.data.state ? res.data.state.toString() : "0";
              
              // 解析联系信息
              this.parseContactInfo(res.data.people);
              
              // 如果没有联系信息，初始化为空对象
              if (!res.data.people) {
                this.contactInfo = {
                  name: "",
                  tel: "",
                  idCard: ""
                };
              }
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message || '获取数据失败'
              });
            }
          }).catch(err => {
            this.$notify.error({
              title: '错误',
              message: '网络异常，请稍后再试'
            });
          });
        }
      }
    }
 }
</script>

<style lang="scss" scoped>
.attraction-order-dialog {
  ::v-deep .el-dialog__header {
    padding: 10px 15px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  ::v-deep .el-dialog__body {
    padding: 15px;
  }
  
  ::v-deep .el-dialog__footer {
    padding: 10px 15px;
    border-top: 1px solid #f0f0f0;
    position: relative;
    z-index: 10;
  }
  
  ::v-deep .el-form-item__label {
    font-weight: 500;
    color: #606266;
    padding-bottom: 0;
    line-height: 1.2;
    font-size: 13px;
  }
  
  /* 隐藏表单验证错误信息 */
  ::v-deep .el-form-item__error {
    display: none !important;
  }
  
  /* 确保按钮可点击 */
  ::v-deep .el-button {
    position: relative;
    z-index: 10;
  }
}

.compact-layout {
  padding: 0;
  position: relative;
  z-index: 1;
}

.compact-form {
  .el-row {
    margin-bottom: 0;
  }
}

.compact-form-item {
  margin-bottom: 8px;
}

.compact-divider {
  margin: 5px 0 8px;
  
  ::v-deep .el-divider__text {
    padding: 0 10px;
    background-color: white;
    font-size: 13px;
    color: #606266;
    font-weight: 500;
  }
}

.divider-text {
  font-size: 12px;
}

::v-deep .el-input__inner {
  height: 30px;
  line-height: 30px;
  font-size: 12px;
}

::v-deep .el-input-number__decrease, 
::v-deep .el-input-number__increase {
  height: 28px;
  line-height: 28px;
  width: 28px;
}

::v-deep .el-textarea__inner {
  min-height: 50px !important;
  font-size: 12px;
}

::v-deep .el-select {
  width: 100%;
}

.dialog-footer {
  position: relative;
  z-index: 10;
}

::v-deep .el-button--primary {
  background-color: #FF9800;
  border-color: #FF9800;
  
  &:hover, &:focus {
    background-color: #FF8500;
    border-color: #FF8500;
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 95% !important;
    margin-top: 10vh !important;
  }
  
  .el-col {
    width: 100% !important;
  }
  
  .compact-form {
    ::v-deep .el-form-item__label {
      float: none;
      display: block;
      text-align: left;
      padding: 0 0 5px;
      width: 100% !important;
  }
    
    ::v-deep .el-form-item__content {
      margin-left: 0 !important;
    }
  }
  }
</style>