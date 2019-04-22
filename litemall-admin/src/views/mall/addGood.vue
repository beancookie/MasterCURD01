<template>
  <div class="login-container">
    <div class="steps">
      <el-row>
        <el-col :span="24" :offset="8">
          <div class="step">
            <el-steps :space="200" :active="active" finish-status="success">
              <el-step title="填写订单基本参数1"></el-step>
              <el-step title="请填写左眼1规格参数"></el-step>
              <el-step title="请填写右眼规格参数 "></el-step>
              <el-step title="请输入其他信息1"></el-step>
            </el-steps>
          </div>
        </el-col>
      </el-row>
      <div class="contents" v-if="active==1">
        <el-row>
          <el-col :span="10" :offset="8">
            <el-form :model="ruleForm" ref="ruleForm" label-width="100px" class="demo-ruleForm" :rules="rules">
              <el-form-item label="眼镜名称" prop="glassName" required>
                <el-input v-model="ruleForm.glassName"></el-input>
              </el-form-item>
              <el-form-item label="眼镜价格" prop="glassPrice" required>
                <el-col :span="11">
                  <el-input v-model="ruleForm.glassPrice"></el-input>
                </el-col>
                <el-col :span="4"></el-col>
                <el-col :span="5" :offset="4">
                  <el-input-number
                    v-model="ruleForm.grassNum"
                    @change="handleChange"
                    :min="1"
                    :max="10"
                    label="描述文字"
                  ></el-input-number>
                </el-col>
              </el-form-item>
              <el-form-item label="购买日期" required prop="glassDate">
                <el-col :span="11">
                  <el-form-item>
                    <el-date-picker
                      type="date"
                      placeholder="选择日期"
                      v-model="ruleForm.glassDate"
                      style="width: 100%;"
                    ></el-date-picker>
                  </el-form-item>
                </el-col>
              </el-form-item>
              <el-form-item label="购买人" required>
                <el-col :span="11">
                  <el-form-item prop="buyName">
                    <el-input v-model="ruleForm.buyName"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="11">
                  <el-form-item label="手机号" prop="buyPhone">
                    <el-input v-model="ruleForm.buyPhone"></el-input>
                  </el-form-item>
                </el-col>
              </el-form-item>
              <el-form-item label="购买人地址" prop="buyAddress">
                <el-input v-model="ruleForm.buyAddress"></el-input>
              </el-form-item>
              <el-button class="nextStep1" type="primary" @click="submitForm('ruleForm')">下一步填写左眼参数</el-button>
              <!-- <el-button @click="resetForm('ruleForm')">重置</el-button> -->
            </el-form>
          </el-col>
        </el-row>
      </div>
      <div class="contents" v-if="active==2">
        <el-row>
          <el-col :span="10" :offset="8">
            <el-form
              :model="ruleForm1"
              inline="true"
              ref="ruleForm1"
              label-width="100px"
              class="demo-ruleForm"
            >
              <el-form-item label="球镜" prop="glassMirror" required>
                <el-input v-model="ruleForm1.glassMirror"></el-input>
              </el-form-item>
              <el-form-item label="柱镜" prop="glassCylinder" required>
                <el-input v-model="ruleForm1.glassCylinder"></el-input>
              </el-form-item>
              <el-form-item label="轴位" prop="glassAxialposition" required>
                <el-input v-model="ruleForm1.glassAxialposition"></el-input>
              </el-form-item>
              <el-form-item label="裸眼视力" prop="glassNakedeyesight" required>
                <el-input v-model="ruleForm1.glassNakedeyesight"></el-input>
              </el-form-item>
              <el-form-item label="矫正视力" prop="glassCorrectivevision" required>
                <el-input v-model="ruleForm1.glassCorrectivevision"></el-input>
              </el-form-item>
              <el-button class="nextStep2" type="primary" @click="submitForm('ruleForm1')">下一步填写右眼参数</el-button>
              <!-- <el-button @click="resetForm('ruleForm')">重置</el-button> -->
            </el-form>
          </el-col>
        </el-row>
      </div>
      <div class="contents" v-if="active==3">
        <el-row>
          <el-col :span="10" :offset="8">
            <el-form
              :model="ruleForm2"
              inline="true"
              ref="ruleForm2"
              label-width="100px"
              class="demo-ruleForm"
            >
              <el-form-item label="球镜" prop="glassMirror" required>
                <el-input v-model="ruleForm2.glassMirror"></el-input>
              </el-form-item>
              <el-form-item label="柱镜" prop="glassCylinder" required>
                <el-input v-model="ruleForm2.glassCylinder"></el-input>
              </el-form-item>
              <el-form-item label="轴位" prop="glassAxialposition" required>
                <el-input v-model="ruleForm2.glassAxialposition"></el-input>
              </el-form-item>
              <el-form-item label="裸眼视力" prop="glassNakedeyesight" required>
                <el-input v-model="ruleForm2.glassNakedeyesight"></el-input>
              </el-form-item>
              <el-form-item label="矫正视力" prop="glassCorrectivevision" required>
                <el-input v-model="ruleForm2.glassCorrectivevision"></el-input>
              </el-form-item>
              <el-button class="nextStep2" type="primary" @click="submitForm('ruleForm1')">下一步填写其他信息</el-button>
              <!-- <el-button @click="resetForm('ruleForm')">重置</el-button> -->
            </el-form>
          </el-col>
        </el-row>
      </div>
      <div class="contents" v-if="active==4">
        <el-row>
          <el-col :span="10" :offset="8">
            <el-form
              :model="ruleForm3"
              inline="true"
              ref="ruleForm3"
              label-width="100px"
              class="demo-ruleForm"
            >
              <el-form-item label="瞳距" prop="glassPitch" required>
                <el-input v-model="ruleForm3.glassPitch"></el-input>
              </el-form-item>
              <el-form-item label="瞳高" prop="glassHigh" required>
                <el-input v-model="ruleForm3.glassHigh"></el-input>
              </el-form-item>
              <el-form-item label="ADD" prop="glassADD" required>
                <el-input v-model="ruleForm3.glassADD"></el-input>
              </el-form-item>
              <el-form-item label="斜角" prop="glassbevel" required>
                <el-input v-model="ruleForm3.glassbevel"></el-input>
              </el-form-item>
              <el-form-item label="镜架品牌" prop="glassFramebrand" required>
                <el-input v-model="ruleForm3.glassFramebrand"></el-input>
              </el-form-item>
              <el-form-item label="镜架货号" prop="glassFramenumber" required>
                <el-input v-model="ruleForm3.glassFramenumber"></el-input>
              </el-form-item>
              <el-form-item label="镜片折射率" prop="glassLensindex" required>
                <el-input v-model="ruleForm3.glassLensindex"></el-input>
              </el-form-item>
              <el-form-item label="镜片品牌" prop="glassLensbrand" required>
                <el-input v-model="ruleForm3.glassLensbrand"></el-input>
              </el-form-item>
              <el-form-item label="镜片功能" prop="glassLensfunction" required>
                <el-input v-model="ruleForm3.glassLensfunction"></el-input>
              </el-form-item>
              <el-form-item label="消费金额" prop="glassAmountofconsumption" required>
                <el-input v-model="ruleForm3.glassAmountofconsumption"></el-input>
              </el-form-item>
              <el-form-item label="备注" prop="glassRemarks" required>
                <el-input v-model="ruleForm3.glassRemarks"></el-input>
              </el-form-item>
              <el-button class="nextStep2" type="primary" @click="submitForm('ruleForm1')">下一步填写其他信息</el-button>
              <!-- <el-button @click="resetForm('ruleForm')">重置</el-button> -->
            </el-form>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      active: 1,
      ruleForm: {
        glassName: "",
        glassPrice:"",
        glassNum: "",
        glassDate: "",
        buyName: "",
        buyPhone: "",
        buyAddress: "",
        LeftEyeparameter:{},
        RightEyeparameter:{},
        allEyeparameter:{}
      },
      ruleForm1: {
        glassMirror: "",
        glassCylinder: "",
        glassAxialposition: "",
        glassNakedeyesight: "",
        glassCorrectivevision: ""
      },
      ruleForm2: {
        glassMirror: "",
        glassCylinder: "",
        glassAxialposition: "",
        glassNakedeyesight: "",
        glassCorrectivevision: ""
      },
      ruleForm3: {
        glassPitch: "",
        glassHigh: "",
        glassADD: "",
        glassbevel: "",
        glassFramebrand: "",
        glassFramenumber: "",
        glassLensindex: "",
        glassLensbrand: "",
        glassLensfunction: "",
        glassAmountofconsumption: "",
        glassRemarks: ""
      },

      //  rules3: {
      //   glassPitch: [
      //     { required: true, message: '请输入瞳距', trigger: 'blur' }
      //  ],
      //   glassHigh: [
      //     { required: true, message: '请输入瞳高', trigger: 'blur' }
      //  ],
      //   glassADD: [
      //     { required: true, message: '请输入ADD', trigger: 'blur' }
      //  ],
      //   glassbevel: [
      //     { required: true, message: '请输入斜角', trigger: 'blur' }
      //  ],
      //   glassFramebrand: [
      //     { required: true, message: '请输入镜架品牌', trigger: 'blur' }
      //  ],
      //   glassFramenumber: [
      //     { required: true, message: '请输入镜架货号', trigger: 'blur' }
      //  ],
      //   glassLensindex: [
      //     { required: true, message: '请输入镜片折射率', trigger: 'blur' }
      //  ],
      //   glassLensbrand: [
      //     { required: true, message: '请输入镜片品牌', trigger: 'blur' }
      //  ],
      //   glassLensfunction: [
      //     { required: true, message: '请输入镜片功能', trigger: 'blur' }
      //  ],
      //   glassAmountofconsumption: [
      //     { required: true, message: '请输入消费金额', trigger: 'blur' }
      //  ],
      //  glassRemarks: [
      //     { required: true, message: '请输入备注信息', trigger: 'blur' }
      //  ],
      // },

      rules: {
        //    glassMirror: [
        //     { required: true, message: '请输入球镜', trigger: 'blur' }
        //  ],
        //   glassCylinder: [
        //     { required: true, message: '请输入柱镜', trigger: 'blur' }
        //  ],
        //   glassAxialposition: [
        //     { required: true, message: '请输入轴位', trigger: 'blur' }
        //  ],
        //   glassNakedeyesight: [
        //     { required: true, message: '请输入裸眼视力', trigger: 'blur' }
        //  ],
        //   glassCorrectivevision: [
        //     { required: true, message: '请输入矫正视力', trigger: 'blur' }
        //  ],
        glassName: [
          { required: true, message: "请输入眼镜名称", trigger: "blur" }
        ],
        glassPrice: [
          { required: true, message: "请输入眼镜价格", trigger: "blur" }
        ],
        glassNum: [
          { required: true, message: "请输入眼镜数量", trigger: "blur" }
        ],
        glassDate: [
          {
            type: "date",
            required: true,
            message: "请输入购买眼镜时间",
            trigger: "blur"
          }
        ],
        buyName: [
          { required: true, message: "请输入购买人姓名", trigger: "blur" }
        ],
        buyPhone: [
          { required: true, message: "请输入购买人手机号", trigger: "blur" }
        ],
        buyAddress: [
          { required: true, message: "请输入购买人地址", trigger: "blur" }
        ]
      }
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    // window.addEventListener('hashchange', this.afterQRScan)
  },
  destroyed() {
    // window.removeEventListener('hashchange', this.afterQRScan)
  },
  methods: {
    handleChange(value) {
      console.log(value);
    },
    submitForm(formName) {
      var that = this;
      that.active++;
      //     var that=this;
      //    this.$refs[formName].validate((valid) => {
      //       if (valid) {
      //        that.active++;
      //       } else {
      //         console.log('error submit!!');
      //         return false;
      //       }
      //     });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.steps {
  margin-top: 71px;
  border: 1px solid RGB(236, 245, 239);
  padding: 50px;
}
.contents {
  margin-top: 70px;
  margin-left: -43px;
}

.nextStep1 {
  float: right;
}
.nextStep2 {
  float: right;
  margin-top: 50px;
}
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
</style>
