<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.userId"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请输入用户ID"
      />
      <el-input
        v-model="listQuery.orderSn"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="请输入订单编号"
      />
      <el-select
        v-model="listQuery.orderStatusArray"
        multiple
        style="width: 200px"
        class="filter-item"
        placeholder="请选择订单状态"
      >
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-button
        v-permission="['GET /admin/order/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
      >查找</el-button>
      <el-button type="primary" class="filter-item" icon="el-icon-edit" @click="addOrder">添加</el-button>
      <el-button
        :loading="downloadLoading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleDownload"
      >导出</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="正在查询中。。。"
      border=""
      fit
      highlight-current-row
    >
      <el-table-column align="center" min-width="100" label="订单编号" prop="orderSn"/>
      <el-table-column align="center" label="用户ID" prop="userId"/>
      <el-table-column align="center" label="订单状态" prop="orderStatus">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.orderStatus | orderStatusFilter }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="订单金额" prop="orderPrice"/>
      <el-table-column align="center" label="支付金额" prop="actualPrice"/>
      <el-table-column align="center" label="支付时间" prop="payTime"/>
      <el-table-column align="center" label="物流单号" prop="shipSn"/>
      <el-table-column align="center" label="物流渠道" prop="shipChannel"/>
      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-permission="['GET /admin/order/detail']"
            type="primary"
            size="mini"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-permission="['POST /admin/order/ship']"
            v-if="scope.row.orderStatus==201"
            type="primary"
            size="mini"
            @click="handleShip(scope.row)"
          >发货</el-button>
          <el-button
            v-permission="['POST /admin/order/refund']"
            v-if="scope.row.orderStatus==202"
            type="primary"
            size="mini"
            @click="handleRefund(scope.row)"
          >退款</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
    <!-- 订单详情对话框 -->
    <el-dialog :visible.sync="orderDialogVisible" title="订单详情" width="800">
      <el-form :data="orderDetail" label-position="left">
        <el-form-item label="订单编号">
          <span>{{ orderDetail.order.orderSn }}</span>
        </el-form-item>
        <el-form-item label="订单状态">
          <template slot-scope="scope">
            <el-tag>{{ scope.order.orderStatus | orderStatusFilter }}</el-tag>
          </template>
        </el-form-item>
        <el-form-item label="订单用户">
          <span>{{ orderDetail.user.nickname }}</span>
        </el-form-item>
        <el-form-item label="用户留言">
          <span>{{ orderDetail.order.message }}</span>
        </el-form-item>
        <el-form-item label="收货信息">
          <span>（收货人）{{ orderDetail.order.consignee }}</span>
          <span>（手机号）{{ orderDetail.order.mobile }}</span>
          <span>（地址）{{ orderDetail.order.address }}</span>
        </el-form-item>
        <el-form-item label="商品信息">
          <el-table :data="orderDetail.orderGoods" border="" fit highlight-current-row>
            <el-table-column align="center" label="商品名称" prop="goodsName"/>
            <el-table-column align="center" label="商品编号" prop="goodsSn"/>
            <el-table-column align="center" label="货品规格" prop="specifications"/>
            <el-table-column align="center" label="货品价格" prop="price"/>
            <el-table-column align="center" label="货品数量" prop="number"/>
            <el-table-column align="center" label="货品图片" prop="picUrl">
              <template slot-scope="scope">
                <img :src="scope.row.picUrl" width="40">
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 发货对话框 -->
    <el-dialog :visible.sync="shipDialogVisible" title="发货">
      <el-form
        ref="shipForm"
        :model="shipForm"
        status-icon
        label-position="left"
        label-width="100px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="快递公司" prop="shipChannel">
          <el-input v-model="shipForm.shipChannel"/>
        </el-form-item>
        <el-form-item label="快递编号" prop="shipSn">
          <el-input v-model="shipForm.shipSn"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确定</el-button>
      </div>
    </el-dialog>
    <!-- 退款对话框 -->
    <el-dialog :visible.sync="refundDialogVisible" title="退款">
      <el-form
        inline="true"
        ref="refundForm"
        :model="refundForm"
        status-icon
        label-position="left"
        label-width="100px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="退款金额" prop="refundMoney">
          <el-input v-model="refundForm.refundMoney" :disabled="true"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmRefund">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogFormVisible">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="填写订单基本参数"></el-step>
        <el-step title="请填写左眼规格参数"></el-step>
        <el-step title="请填写右眼规格参数 "></el-step>
        <el-step title="请输入其他信息"></el-step>
      </el-steps>
      <div v-if="active==1" class="btn-wrap" style="margin-top:50px">
        <el-form 
          :model="ruleForm" 
          :inline="true" 
          ref="ruleForm" 
          :rules="rules" 
          label-width="100px"
        >
          <el-form-item label="眼镜名称" prop="glassName" required>
            <el-input v-model="ruleForm.glassName"></el-input>
          </el-form-item>
          <el-form-item label="眼镜价格" prop="glassPrice" required>
            <el-input v-model="ruleForm.glassPrice"></el-input>
          </el-form-item>
          <el-form-item label="购买日期" prop="glassDate" required>
            <el-date-picker
              align="left"
              type="datetime"
              placeholder="选择日期"
              v-model="ruleForm.glassDate"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="购买人" prop="buyName">
            <el-input v-model="ruleForm.buyName"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="buyPhone">
            <el-input v-model="ruleForm.buyPhone"></el-input>
          </el-form-item>
          <el-form-item label="购买人地址" prop="buyAddress">
            <el-input v-model="ruleForm.buyAddress"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitForm('ruleForm')" class="buttonOne">下一步填写左眼参数</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="active==2">
        <el-form
          :model="ruleForm1"
          :inline="true"
          ref="ruleForm1"
          :rules="rules"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="球镜" prop="leftGlassMirror" required>
            <el-input v-model="ruleForm1.leftGlassMirror"></el-input>
          </el-form-item>
          <el-form-item label="柱镜" prop="leftGlassCylinder" required>
            <el-input v-model="ruleForm1.leftGlassCylinder"></el-input>
          </el-form-item>
          <el-form-item label="轴位" prop="leftGlassAxialposition" required>
            <el-input v-model="ruleForm1.leftGlassAxialposition"></el-input>
          </el-form-item>
          <el-form-item label="裸眼视力" prop="leftGlassNakedeyesight" required>
            <el-input v-model="ruleForm1.leftGlassNakedeyesight"></el-input>
          </el-form-item>
          <el-form-item label="矫正视力" prop="leftGlassCorrectivevision" required>
            <el-input v-model="ruleForm1.leftGlassCorrectivevision"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="submitForm('ruleForm1')"
              style="margin-left:500px"
            >下一步填写右眼参数</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="active==3">
        <el-form
          :model="ruleForm2"
          :inline="true"
          ref="ruleForm2"
          :rules="rules"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="球镜" prop="rightGlassMirror" required>
            <el-input v-model="ruleForm2.rightGlassMirror"></el-input>
          </el-form-item>
          <el-form-item label="柱镜" prop="rightGlassCylinder" required>
            <el-input v-model="ruleForm2.rightGlassCylinder"></el-input>
          </el-form-item>
          <el-form-item label="轴位" prop="rightGlassAxialposition" required>
            <el-input v-model="ruleForm2.rightGlassAxialposition"></el-input>
          </el-form-item>
          <el-form-item label="裸眼视力" prop="rightGlassNakedeyesight" required>
            <el-input v-model="ruleForm2.rightGlassNakedeyesight"></el-input>
          </el-form-item>
          <el-form-item label="矫正视力" prop="rightGlassCorrectivevision" required>
            <el-input v-model="ruleForm2.rightGlassCorrectivevision"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              @click="submitForm('ruleForm2')"
              style="margin-left:500px"
            >下一步填写其他信息</el-button>
          </el-form-item>
        </el-form>
      </div>
      <div v-if="active==4">
        <el-form
          :model="ruleForm3"
          :inline="true"
          ref="ruleForm3"
          :rules="rules"
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
          <el-form-item>
            <el-button
              type="primary"
              @click="submitForm('ruleForm3')"
              style="margin-left:200px"
              size="medium"
            >提交订单</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped="scoped">
.btn-wrap {
  display: flex;
  .btn {
    justify-content: flex-end;
  }
  .buttonOne {
    float: right;
    margin-left: 500px;
  }
}
</style>

<script>
import { listOrder, shipOrder, refundOrder, detailOrder } from "@/api/order";
import Pagination from "@/components/Pagination"; // Secondary package based on el-pagination
import checkPermission from "@/utils/permission"; // 权限判断函数

const statusMap = {
  100: "线下订单",
  101: "未付款",
  102: "用户取消",
  103: "系统取消",
  201: "已付款",
  202: "申请退款",
  203: "已退款",
  301: "已发货",
  401: "用户收货",
  402: "系统收货"
};

export default {
  name: "Order",
  components: { Pagination },
  filters: {
    orderStatusFilter(status) {
      return statusMap[status];
    }
  },
  data() {
    return {
      list: undefined,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        id: undefined,
        name: undefined,
        orderStatusArray: [],
        sort: "add_time",
        order: "desc"
      },
      statusMap,
      orderDialogVisible: false,
      orderDetail: {
        order: {},
        user: {},
        orderGoods: [
          {
            goodsName: "11",
            goodsSn: "1",
            specifications: "name:zl",
            price: 1,
            number: 2,
            picUrl: "xx"
          },
          {
            goodsName: "左眼参数",
            goodsSn: "左眼参数",
            specifications: "name:zl",
            price: "左眼参数",
            number: 2,
            picUrl: "xx"
          },
          {
            goodsName: "11",
            goodsSn: "1",
            specifications: "name:zl",
            price: 1,
            number: 2,
            picUrl: "xx"
          }
        ]
      },
      shipForm: {
        orderId: undefined,
        shipChannel: undefined,
        shipSn: undefined
      },
      shipDialogVisible: false,
      refundForm: {
        orderId: undefined,
        refundMoney: undefined
      },
      refundDialogVisible: false,
      downloadLoading: false,
      active: 1,
      ruleForm: {
        glassName: undefined,
        glassPrice: undefined,
        glassNum: 1,
        glassDate: undefined,
        buyName: undefined,
        buyPhone: undefined,
        buyAddress: undefined,
        leftEyeDetail: {},
        rightEyeDetail: {},
        allEyeDetail: {}
      },
      ruleForm1: {
        leftGlassMirror: undefined,
        leftGlassCylinder: undefined,
        leftGlassAxialposition: undefined,
        leftGlassNakedeyesight: undefined,
        leftGlassCorrectivevision: undefined
      },
      ruleForm2: {
        rightGlassMirror: undefined,
        rightGlassCylinder: undefined,
        rightGlassAxialposition: undefined,
        rightGlassNakedeyesight: undefined,
        rightGlassCorrectivevision: undefined
      },
      ruleForm3: {
        glassPitch: undefined,
        glassHigh: undefined,
        glassADD: undefined,
        glassbevel: undefined,
        glassFramebrand: undefined,
        glassFramenumber: undefined,
        glassLensindex: undefined,
        glassLensbrand: undefined,
        glassLensfunction: undefined,
        glassAmountofconsumption: undefined,
        glassRemarks: undefined
      },
      dialogFormVisible: false,
      rules: {
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
          { type: "date", required: true, message: "请输入购买眼镜时间", trigger: "change" }
        ],
        buyName: [
          { required: true, message: "请输入购买人姓名", trigger: "blur" }
        ],
        buyPhone: [
          { required: true, message: "请输入购买人手机号", trigger: "blur" }
        ],
        buyAddress: [
          { required: true, message: "请输入购买人地址", trigger: "blur" }
        ],
        leftGlassMirror: [
          { required: true, message: "请输入球镜", trigger: "blur" }
        ],
        leftGlassCylinder: [
          { required: true, message: "请输入柱镜", trigger: "blur" }
        ],
        leftGlassAxialposition: [
          { required: true, message: "请输入轴位", trigger: "blur" }
        ],
        leftGlassNakedeyesight: [
          { required: true, message: "请输入裸眼视力", trigger: "blur" }
        ],
        leftGlassCorrectivevision: [
          { required: true, message: "请输入矫正视力", trigger: "blur" }
        ],
        rightGlassMirror: [
          { required: true, message: "请输入球镜", trigger: "blur" }
        ],
        rightGlassCylinder: [
          { required: true, message: "请输入柱镜", trigger: "blur" }
        ],
        rightGlassAxialposition: [
          { required: true, message: "请输入轴位", trigger: "blur" }
        ],
        rightGlassNakedeyesight: [
          { required: true, message: "请输入裸眼视力", trigger: "blur" }
        ],
        rightGlassCorrectivevision: [
          { required: true, message: "请输入矫正视力", trigger: "blur" }
        ],
        glassPitch: [
          { required: true, message: "请输入瞳距", trigger: "blur" }
        ],
        glassHigh: [
          { required: true, message: "请输入瞳高", trigger: "blur" }
        ],
        glassADD: [
          { required: true, message: "请输入ADD", trigger: "blur" }
        ],
        glassbevel: [
          { required: true, message: "请输入斜角", trigger: "blur" }
        ],
        glassFramebrand: [
          { required: true, message: "请输入镜架品牌", trigger: "blur" }
        ],
        glassFramenumber: [
          { required: true, message: "请输入镜架货号", trigger: "blur" }
        ],
        glassLensindex: [
          { required: true, message: "请输入镜片折射率", trigger: "blur" }
        ],
        glassLensbrand: [
          { required: true, message: "请输入镜片品牌", trigger: "blur" }
        ],
        glassLensfunction: [
          { required: true, message: "请输入镜片功能", trigger: "blur" }
        ],
        glassAmountofconsumption: [
          { required: true, message: "请输入消费金额", trigger: "blur" }
        ],
        glassRemarks: [
          { required: true, message: "请输入备注信息", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    checkPermission,
    getList() {
      this.listLoading = true;
      listOrder(this.listQuery)
        .then(response => {
          this.list = response.data.data.items;
          this.total = response.data.data.total;
          this.listLoading = false;
        })
        .catch(() => {
          this.list = [];
          this.total = 0;
          this.listLoading = false;
        });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleDetail(row) {
      detailOrder(row.id).then(response => {
        this.orderDetail = response.data.data;
      });
      this.orderDialogVisible = true;
    },
    handleShip(row) {
      this.shipForm.orderId = row.id;
      this.shipForm.shipChannel = row.shipChannel;
      this.shipForm.shipSn = row.shipSn;

      this.shipDialogVisible = true;
      this.$nextTick(() => {
        this.$refs["shipForm"].clearValidate();
      });
    },
    confirmShip() {
      this.$refs["shipForm"].validate(valid => {
        if (valid) {
          shipOrder(this.shipForm)
            .then(response => {
              this.shipDialogVisible = false;
              this.$notify.success({
                title: "成功",
                message: "确认发货成功"
              });
              this.getList();
            })
            .catch(response => {
              this.$notify.error({
                title: "失败",
                message: response.data.errmsg
              });
            });
        }
      });
    },
    addOrder() {
      // this.$router.push("/addOrder");
      this.dialogFormVisible = true;
    },
    handleRefund(row) {
      this.refundForm.orderId = row.id;
      this.refundForm.refundMoney = row.actualPrice;

      this.refundDialogVisible = true;
      this.$nextTick(() => {
        this.$refs["refundForm"].clearValidate();
      });
    },
    confirmRefund() {
      this.$refs["refundForm"].validate(valid => {
        if (valid) {
          refundOrder(this.refundForm)
            .then(response => {
              this.refundDialogVisible = false;
              this.$notify.success({
                title: "成功",
                message: "确认退款成功"
              });
              this.getList();
            })
            .catch(response => {
              this.$notify.error({
                title: "失败",
                message: response.data.errmsg
              });
            });
        }
      });
    },
    handleDownload() {
      this.downloadLoading = true;
      import("@/vendor/Export2Excel").then(excel => {
        const tHeader = [
          "订单ID",
          "订单编号",
          "用户ID",
          "订单状态",
          "是否删除",
          "收货人",
          "收货联系电话",
          "收货地址"
        ];
        const filterVal = [
          "id",
          "orderSn",
          "userId",
          "orderStatus",
          "isDelete",
          "consignee",
          "mobile",
          "address"
        ];
        excel.export_json_to_excel2(tHeader, this.list, filterVal, "订单信息");
        this.downloadLoading = false;
      });
    },
    handleChange(value) {
      console.log(value);
    },
    submitForm(formName) {
      this.active++;
      return
      var that = this;
      this.$refs[formName].validate(valid => {
        if (valid) {
          that.active++;
          if (that.active == 4) {
            that.ruleForm.leftEyeDetail = that.ruleForm1;
            that.ruleForm.rightEyeDetail = that.ruleForm2;
            that.ruleForm.allEyeDetail = that.ruleForm3;
            that.dialogFormVisible = false;
          }
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  }
};
</script>
