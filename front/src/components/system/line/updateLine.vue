<template>
<div>
  <el-dialog title="修改旅游线路" width="50%" :destroy-on-close="true" :visible.sync="updateVisible" :before-close="handleClose" custom-class="line-dialog">
    <el-form :model="form" :rules="rules" ref="ruleForm" label-position="top">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form-item label="线路名称" prop="name" class="form-item">
            <el-input v-model="form.name" placeholder="请输入线路名称" autocomplete="off">
              <template slot="prepend"><i class="el-icon-map-location"></i></template>
            </el-input>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
          <el-form-item label="温度(℃)" prop="temperature" class="form-item">
            <el-input type="number" v-model="form.temperature" placeholder="请输入温度" autocomplete="off">
              <template slot="prepend"><i class="el-icon-sunny"></i></template>
            </el-input>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="12" :lg="6" :xl="6">
          <el-form-item label="价格(¥)" prop="price" class="form-item">
            <el-input-number v-model="form.price" :min="0" :precision="2" :step="100" controls-position="right" style="width:100%" placeholder="请输入价格"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form-item label="目的地" prop="geography" class="form-item">
            <el-input v-model="form.geography" placeholder="请输入目的地" autocomplete="off">
              <template slot="prepend"><i class="el-icon-location"></i></template>
            </el-input>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <el-form-item label="出行时间" prop="duration" class="form-item">
            <el-input v-model="form.duration" placeholder="如：3天2晚、每周六出发" autocomplete="off">
              <template slot="prepend"><i class="el-icon-date"></i></template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-divider content-position="left">线路详情</el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="包含景点" prop="attractions" class="form-item">
            <el-input type="textarea" v-model="form.attractions" rows="3" placeholder="请输入包含的景点，多个景点可用逗号分隔" autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="行程安排" prop="schedule" class="form-item">
            <el-input type="textarea" v-model="form.schedule" rows="3" placeholder="请详细描述行程安排，如：第一天...，第二天..." autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="线路简介" prop="introduce" class="form-item">
            <el-input type="textarea" v-model="form.introduce" rows="2" placeholder="请输入线路简介" autocomplete="off"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="线路图片" prop="images" class="form-item">
            <div class="upload-container">
              <el-upload
                :file-list="fileList"
                ref="upload"
                :action="uploadImageUrl()"
                accept="image/*"
                :multiple="true"
                :limit="5"
                :auto-upload="true"
                :before-upload="beforeAvatorUpload"
                list-type="picture-card"
                :on-preview="handlePictureCardPreview"
                :on-exceed="handleExceed"
                :on-success="handleAvatorSuccess"
                :on-remove="handleRemove">
                <i class="el-icon-plus"></i>
                <div slot="tip" class="el-upload__tip">支持JPG/PNG格式，最多上传5张图片</div>
              </el-upload>
            </div>
            <el-dialog title="图片预览" :visible.sync="dialogVisible" append-to-body>
              <img width="100%" :src="dialogImageUrl" alt="">
            </el-dialog>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="状态" prop="state" class="form-item">
            <el-radio-group v-model="form.state">
              <el-radio :label="1"><i class="el-icon-check"></i> 启用</el-radio>
              <el-radio :label="0"><i class="el-icon-close"></i> 禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-divider content-position="left">详细内容</el-divider>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
          <el-form-item label="线路详细描述" prop="content" class="form-item">
            <quill-editor ref="myQuillEditor" :options="editorOption" v-model="form.content" class="editor-container"/>
            <el-upload class="Quill-uploader" :show-file-list="false" accept="image/*" v-show="0" ref="updateUpload" :multiple="false" :action="uploadImageUrl()"
                :on-success="handleQillSuccess">
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="submit" :loading="submitLoading">确 定</el-button>
    </div>
  </el-dialog>
</div>
</template>

<script>
  import { mixin } from '../../../minix/index';
  import {getSysLineById,editSysLine} from '../../../api/api'
  export default {
    mixins: [mixin],
    data() {
      return{
        fileList: [],
        dialogImageUrl: '',
        dialogVisible: false,
        submitLoading: false,
        formLabelWidth: '80px',
        form: {
          introduce: "",
          images: "",
          name: "",
          content: "",
          temperature: "",
          geography: "",
          price: null,
          duration: "",
          attractions: "",
          schedule: "",
          state: 1
        },
        editorOption: {
          placeholder: "请输入详细内容描述",
          modules: { 
            toolbar: {
                container: [
                    ["bold", "italic", "underline", "strike"], 
                    ["blockquote", "code-block"], 
                    [{ header: 1 }, { header: 2 }], 
                    [{ list: "ordered" }, { list: "bullet" }], 
                    [{ script: "sub" }, { script: "super" }], 
                    [{ indent: "-1" }, { indent: "+1" }], 
                    [{ direction: "rtl" }], 
                    [{ size: ["small", false, "large", "huge"] }], 
                    [{ header: [1, 2, 3, 4, 5, 6, false] }], 
                    [{ color: [] }, { background: [] }], 
                    [{ font: [] }], [{ align: [] }], 
                    ["clean"], ["image"]
                ],
                handlers: {
                    'image': function ( value ) {
                        if ( value ) {
                            // 触发上传插件
                            document.querySelector('.Quill-uploader .el-upload__input').click()
                        } else {
                            this.quill.format( 'image', false );
                        }
                    }
                }
            },
            syntax: { 
                highlight: text => { 
                    return hljs.highlightAuto(text).value; // 这里就是代码高亮需要配置的地方 
                }
            }
          }
        },
        rules: {
          name: [
            { required: true, message: '请输入名称', trigger: 'blur' },
          ],
          content: [
            { required: true, message: '请输入内容', trigger: 'blur' },
          ],
          introduce: [
            { required: true, message: '请输入介绍', trigger: 'blur' },
          ],
          images: [
            { required: true, message: '请上传图片', trigger: 'change' },
          ],
          temperature: [
            { required: true, message: '请输入温度', trigger: 'blur' },
          ],
          geography: [
            { required: true, message: '请输入目的地', trigger: 'blur' },
          ],
          price: [
            { required: true, message: '请输入价格', trigger: 'blur' },
          ],
          duration: [
            { required: true, message: '请输入出行时间', trigger: 'blur' },
          ],
          attractions: [
            { required: true, message: '请输入包含景点', trigger: 'blur' },
          ],
          schedule: [
            { required: true, message: '请输入行程安排', trigger: 'blur' },
          ],
        },
      }
    },
    props: ['updateVisible', 'updateId'],
    methods: {
      handleAvatorSuccess(res) {
        let _this = this;
        if(res.code == 1000){
            _this.$message({
              type: 'success',
              message: '上传成功!'
            });
            const imageUrl = this.$store.state.configure.HOST + res.message;
            this.fileList.push({url: imageUrl});
            
            // 直接更新form.images字段，防止提交时验证失败
            let images = "";
            for (let i = 0; i < this.fileList.length; i++) {
              if(i == 0){
                images += this.fileList[i].url;
              }else{
                images += "," + this.fileList[i].url;
              }
            }
            this.form.images = images;
        }else{
          _this.$notify.error({
            title: '错误',
            message: res.message
          });
        }
      },
      handleRemove(file, fileList) {
        this.fileList = fileList
        
        // 更新form.images字段
        if (this.fileList.length === 0) {
          this.form.images = "";
        } else {
          let images = "";
          for (let i = 0; i < this.fileList.length; i++) {
            if(i == 0){
              images += this.fileList[i].url;
            }else{
              images += "," + this.fileList[i].url;
            }
          }
          this.form.images = images;
        }
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      },
      handleQillSuccess(res){
          let _this = this;
          if(res.code == 1000){
              // 获取富文本组件实例
              let quill = this.$refs.myQuillEditor.quill;
              // 获取光标位置
              let length = quill.selection.savedRange.index;
              // 插入图片
              quill.insertEmbed(length, 'image', this.$store.state.configure.HOST + res.message);
              // 调整光标到最后
              quill.setSelection(length + 1);
          }else{
              _this.$notify.error({
                title: '错误',
                message: res.message
              });
          }
      },
      handleExceed(){
        this.$message.warning(`当前限制上传5张图片`);
      },
      handleClose() {
        this.$emit('updateFalse')
        this.fileList = []
        this.form = {
          introduce: "",
          images: "",
          name: "",
          content: "",
          temperature: "",
          geography: "",
          price: null,
          duration: "",
          attractions: "",
          schedule: "",
          state: 1
        }
      },
      queryById(){
        this.submitLoading = true;
        getSysLineById({id:this.updateId}).then(res => {
          this.submitLoading = false;
          if(res.code == 1000) {
            this.form = res.data
            var images = res.data.images
            if(images){
              var list = images.split(",")
              this.fileList = []
              for (let i = 0;i < list.length;i++) {
                this.fileList.push({url:list[i]})
              }
            }
          } else {
            this.$notify.error({
              title: '错误',
              message: res.message
            });
          }
        }).catch(err => {
          this.submitLoading = false;
          console.error('获取线路详情失败:', err);
          this.$notify.error({
            title: '错误',
            message: '获取线路详情失败'
          });
        });
      },
      submit(){
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            // 检查图片是否已上传
            if(this.fileList.length == 0){
              this.$message.warning(`请上传图片`);
              return;
            }
            
            // 检查form.images是否已设置（正常情况下在图片上传时已经设置）
            if (!this.form.images && this.fileList.length > 0) {
              let images = "";
              for (let i = 0; i < this.fileList.length; i++) {
                if(i == 0){
                  images += this.fileList[i].url;
                }else{
                  images += "," + this.fileList[i].url;
                }
              }
              this.form.images = images;
            }
            
            this.submitLoading = true;
            console.log('提交编辑线路数据:', this.form);
            editSysLine(this.form).then(res => {
              this.submitLoading = false;
              console.log('编辑线路返回结果:', res);
              if(res.code == 1000) {
                this.$message({
                  type: 'success',
                  message: '保存成功!'
                });
                // 确保关闭对话框和通知父组件更新
                this.$emit('updateFalse', true);
              } else {
                this.$notify.error({
                  title: '错误',
                  message: res.message
                });
              }
            }).catch(err => {
              this.submitLoading = false;
              console.error('编辑线路出错:', err);
              this.$notify.error({
                title: '错误',
                message: '保存失败!'
              });
            });
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      }
    },
    watch: {
      updateId(val) {
        if(val){
          this.queryById()
        }
      },
    },
  }
</script>

<style lang="scss" scoped>
.line-dialog {
  border-radius: 8px;
  overflow: hidden;
}

::v-deep .el-dialog__header {
  padding: 20px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

::v-deep .el-dialog__title {
  font-weight: 600;
  color: #303133;
  font-size: 18px;
}

::v-deep .el-dialog__body {
  padding: 30px;
  max-height: 65vh;
  overflow-y: auto;
}

::v-deep .el-dialog__footer {
  padding: 15px 20px;
  border-top: 1px solid #ebeef5;
  background: #f5f7fa;
}

.form-item {
  margin-bottom: 20px;
}

::v-deep .el-form-item__label {
  font-weight: 500;
  padding-bottom: 5px;
}

::v-deep .el-divider__text {
  background-color: #f8f9fc;
  color: #409EFF;
  font-weight: 600;
  padding: 0 15px;
}

::v-deep .el-divider {
  margin: 20px 0;
}

::v-deep .el-input-number {
  width: 100%;
}

::v-deep .el-input-number .el-input__inner {
  text-align: left;
}

::v-deep .el-textarea__inner {
  font-family: Arial, sans-serif;
  padding: 10px 15px;
  line-height: 1.5;
}

.upload-container {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 15px;
  background-color: #fbfdff;
}

::v-deep .el-upload__tip {
  color: #909399;
  margin-top: 10px;
  text-align: center;
}

::v-deep .el-radio {
  margin-right: 20px;
  line-height: 32px;
}

::v-deep .el-radio__label {
  padding-left: 5px;
}

.editor-container {
  height: 300px;
  margin-bottom: 30px;
}

::v-deep .ql-container {
  min-height: 250px;
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
</style>