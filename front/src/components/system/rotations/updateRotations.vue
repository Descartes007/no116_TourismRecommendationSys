<template>
<div>
  <el-dialog 
    title="编辑轮播图" 
    width="50%" 
    :destroy-on-close="true" 
    :visible.sync="updateVisible" 
    :before-close="handleClose"
    custom-class="rotation-dialog">
    
    <el-form :model="form" :rules="rules" ref="ruleForm" label-position="top" class="rotation-form">
      <div class="form-section">
        <div class="section-title">
          <i class="el-icon-picture-outline"></i>
          <span>编辑轮播图片</span>
        </div>
        <p class="section-desc">请上传高质量的图片作为网站首页轮播图展示，建议尺寸为1920×600像素</p>
        
        <el-form-item prop="image" class="upload-container">
          <el-upload
            :file-list="fileList"
            ref="upload"
            :action="uploadImageUrl()"
            accept="image/*"
            :multiple="false"
            :limit="1"
            :before-upload="beforeAvatorUpload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-exceed="handleExceed"
            :on-success="handleAvatorSuccess"
            :on-remove="handleRemove">
            <i class="el-icon-plus"></i>
            <div class="upload-tip" slot="tip">点击更换轮播图片，只能上传jpg/png格式文件</div>
          </el-upload>
        </el-form-item>
      </div>
      
      <el-dialog :visible.sync="dialogVisible" append-to-body width="70%" class="preview-dialog">
        <img width="100%" :src="dialogImageUrl" alt="预览图片">
      </el-dialog>
    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button size="small" @click="handleClose" plain>取 消</el-button>
      <el-button size="small" type="primary" @click="submit" :loading="submitting">保 存</el-button>
    </div>
  </el-dialog>
</div>
</template>

<script>
  import {mixin} from "../../../minix";
  import {editSysRotations,getSysRotationsById} from '../../../api/api'
  export default {
    mixins: [mixin],
    data() {
      return{
        fileList: [],
        dialogImageUrl: '',
        dialogVisible: false,
        submitting: false,
        formLabelWidth: '80px',
        form: {
          image: "",
        },
        rules: {
          image: [
            { required: true, message: '请上传轮播图片', trigger: 'blur' },
          ],
        },
      }
    },
    props: ['updateVisible','updateId'],
    methods: {
      handleAvatorSuccess(res) {
        let _this = this;
        if(res.code == 1000){
            _this.$message({
              type: 'success',
              message: '上传成功!'
            });
            this.fileList = [{url:this.$store.state.configure.HOST + res.message}];
            this.form.image = res.message;
        }else{
          _this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      },
      handleRemove(file, fileList) {
        this.form.content = "";
        this.fileList = [];
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      submit() {
        this.$refs["ruleForm"].validate((valid) => {
          if (valid) {
            this.submitting = true;
            editSysRotations(this.form).then(res => {
              this.submitting = false;
              if(res.code == 1000) {
                this.$notify.success({
                  title: '成功',
                  message: "轮播图修改成功"
                });
                this.handleClose();
              } else {
                this.$notify.error({
                  title: '错误',
                  message: res.message
                });
              }
            }).catch(err => {
              this.submitting = false;
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
        this.fileList = [];
        this.$emit("updateFalse");
      },
    },
    watch: {
      updateId(newVal) {
        if(newVal) {
          this.loading = true;
          getSysRotationsById({id:newVal}).then(res => {
            this.loading = false;
            if(res.code == 1000) {
              this.form = res.data;
              this.fileList = [{url:this.$store.state.configure.HOST + this.form.image}];
            } else {
              this.$notify.error({
                title: '错误',
                message: res.message
              });
            }
          }).catch(err => {
            this.loading = false;
            this.$notify.error({
              title: '错误',
              message: '获取轮播图数据失败'
            });
          });
        }
      }
    }
 }
</script>

<style lang="scss" scoped>
.rotation-dialog {
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

.rotation-form {
  .form-section {
    margin-bottom: 20px;
    
    .section-title {
      margin-bottom: 10px;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      display: flex;
      align-items: center;
      
      i {
        margin-right: 8px;
        color: #FF9500;
      }
    }
    
    .section-desc {
      font-size: 14px;
      color: #909399;
      margin-bottom: 15px;
    }
  }
  
  .upload-container {
    display: flex;
    justify-content: center;
    margin: 20px 0;
    
    ::v-deep .el-upload--picture-card {
      width: 200px;
      height: 120px;
      line-height: 120px;
      border-radius: 4px;
      border: 1px dashed #d9d9d9;
      
      &:hover {
        border-color: #FF9500;
      }
    }
    
    ::v-deep .el-upload-list--picture-card .el-upload-list__item {
      width: 200px;
      height: 120px;
      border-radius: 4px;
    }
    
    .upload-tip {
      font-size: 12px;
      color: #909399;
      line-height: 1.5;
      text-align: center;
      margin-top: 10px;
    }
  }
}

.preview-dialog {
  ::v-deep .el-dialog__body {
    padding: 10px;
    text-align: center;
    background-color: #f5f7fa;
  }
  
  img {
    max-width: 100%;
    max-height: 70vh;
    object-fit: contain;
  }
}

// 为保存按钮添加蓝色背景样式
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
</style>