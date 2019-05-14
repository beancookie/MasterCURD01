<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>活动内容</h3>
      <el-form ref="dataForm" :rules="rules" :model="dataForm" label-width="150px">
        <el-form-item label="活动标题" prop="name">
          <el-input v-model="dataForm.name"/>
        </el-form-item>
        <el-form-item label="活动内容" prop="content">
          <el-input v-model="dataForm.content"/>
        </el-form-item>
        <el-form-item label="活动图片" prop="url">
          <el-upload
            :headers="headers"
            :action="uploadPath"
            :show-file-list="false"
            :on-success="uploadUrl"
            class="avatar-uploader"
            accept=".jpg,.jpeg,.png,.gif">
            <img v-if="dataForm.url" :src="dataForm.url" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="活动位置" prop="position">
          <el-select v-model="dataForm.position" placeholder="请选择">
            <el-option :value="1" label="首页"/>
          </el-select>
        </el-form-item>
        <el-form-item label="活动链接" prop="link">
          <el-input v-model="dataForm.link"/>
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-select v-model="dataForm.enabled" placeholder="请选择">
            <el-option :value="true" label="启用"/>
            <el-option :value="false" label="不启用"/>
          </el-select>
        </el-form-item>
        <el-form-item label="活动详细介绍">
          <editor class="editor" :init="editorInit" v-model="dataForm.detail"/>
        </el-form-item>
      </el-form>
    </el-card>
    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="createData">确定</el-button>
    </div>

  </div>
</template>

<style>
  .el-card {
    margin-bottom: 10px;
  }

  .el-tag + .el-tag {
    margin-left: 10px;
  }

  .input-new-keyword {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }

  .avatar-uploader .el-upload {
    width: 145px;
    height: 145px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
  }

  .avatar {
    width: 145px;
    height: 145px;
    display: block;
  }

  .editor {
    display: block;
    width: 100%;
    height: 1200px;
  }
</style>

<script>
import { createStorage, uploadPath } from '@/api/storage'
import { createAd, updateAd } from '@/api/ad'
import Editor from '@tinymce/tinymce-vue'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'
import { mapGetters } from 'vuex'

export default {
  name: 'AdCreate',
  components: { Editor },

  data() {
    return {
      uploadPath,
      dataForm: {
        id: undefined,
        name: undefined,
        content: undefined,
        detail: undefined,
        url: undefined,
        link: undefined,
        position: 1,
        enabled: true
      },
      rules: {
        name: [
          { required: true, message: '广告标题不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '广告内容不能为空', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '广告链接不能为空', trigger: 'blur' }
        ]
      },
      editorInit: {
        language: 'zh_CN',
        convert_urls: false,
        plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
        toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Litemall-Admin-Token': getToken()
      }
    },
    ...mapGetters([
      'ad',
      'isUpdate'
    ])
  },
  mounted() {
    if (this.isUpdate) {
      this.dataForm = this.ad
      console.log(this.dataForm);
      
    }
  },
  methods: {
    handleCancel: function() {
      this.$router.push({ path: '/promotion/ad' })
    },
    uploadUrl: function(response) {
      this.dataForm.url = response.data.url
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          if (this.isUpdate) {
              updateAd(this.dataForm)
                .then(response => {
                  this.$notify.success({
                    title: '成功',
                    message: '更新成功'
                  })
                  this.$router.push({ path: '/promotion/ad' })
                })
                .catch(response => {
                  this.$notify.error({
                    title: '失败',
                    message: response.data.errmsg
                  })
                })
            } else {
              createAd(this.dataForm)
                .then(response => {
                  this.$notify.success({
                    title: '成功',
                    message: '创建成功'
                  })
                  this.$router.push({ path: '/promotion/ad' })
                })
                .catch(response => {
                  this.$notify.error({
                    title: '失败',
                    message: response.data.errmsg
                  })
                })
            }
        }
      })
    },
  }
}
</script>
