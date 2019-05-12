<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.userId" clearable style="width: 200px;" placeholder="请输入用户ID"/>
      <el-input
        v-model="listQuery.phone"
        clearable
        style="width: 200px;margin-left:15px"
        placeholder="请输入用户手机号"
      />
      <el-date-picker
        v-model="listQuery.addTime"
        type="datetimerange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="margin-left:15px"
      ></el-date-picker>
      <el-select
        v-model="listQuery.orderStatusArray"
        multiple
        style="width: 200px;margin-left:15px"
        placeholder="请选择订单状态"
      >
        <el-option v-for="(key, value) in statusMap" :key="key" :label="key" :value="value"/>
      </el-select>
      <el-button
        v-permission="['GET /admin/order/list']"
        type="primary"
        icon="el-icon-search"
        style="margin-left:15px"
        @click="handleFilter"
      >查找</el-button>
      <el-button type="primary" icon="el-icon-edit" @click="addOrder">添加</el-button>
      <el-button
        :loading="downloadLoading"
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
      <el-table-column align="center" label="配镜时间" prop="addTime"/>
      <el-table-column align="center" label="操作" width="300" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-permission="['GET /admin/order/detail']"
            type="primary"
            size="mini"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-permission="['GET /admin/order/detail']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
            v-permission="['GET /admin/order/detail']"
            type="warning"
            size="mini"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
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
        <el-form-item label="眼镜店名称">
          <span>{{ orderDetail.orderGoods.goodsName }}</span>
        </el-form-item>
        <el-form-item label="收货信息">
          <span>（购买人）{{ orderDetail.order.consignee }}</span>
          <span>（手机号）{{ orderDetail.order.mobile }}</span>
        </el-form-item>
        <el-form-item label="商品信息">
          <el-table :data="tableData" style="width: 100%">
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-form label-position="left" inline class="table-expand">
                  <el-form-item label="ADD">
                    <span>{{ props.row.glassADD }}</span>
                  </el-form-item>
                  <el-form-item label="瞳距">
                    <span>{{ props.row.glassPitch }}</span>
                  </el-form-item>
                  <el-form-item label="瞳高">
                    <span>{{ props.row.glassHigh }}</span>
                  </el-form-item>
                  <el-form-item label="斜角">
                    <span>{{ props.row.glassbevel }}</span>
                  </el-form-item>
                  <el-form-item label="左球镜">
                    <span>{{ props.row.leftGlassMirror }}</span>
                  </el-form-item>
                  <el-form-item label="右球镜">
                    <span>{{ props.row.rightGlassMirror }}</span>
                  </el-form-item>
                  <el-form-item label="左柱镜">
                    <span>{{ props.row.leftGlassCylinder }}</span>
                  </el-form-item>
                  <el-form-item label="右柱镜">
                    <span>{{ props.row.rightGlassCylinder }}</span>
                  </el-form-item>
                  <el-form-item label="左轴位">
                    <span>{{ props.row.leftGlassAxialposition }}</span>
                  </el-form-item>
                  <el-form-item label="右轴位">
                    <span>{{ props.row.rightGlassAxialposition }}</span>
                  </el-form-item>
                  <el-form-item label="左裸眼视力">
                    <span>{{ props.row.leftGlassNakedeyesight }}</span>
                  </el-form-item>
                  <el-form-item label="右裸眼视力">
                    <span>{{ props.row.rightGlassNakedeyesight }}</span>
                  </el-form-item>
                  <el-form-item label="左矫正视力">
                    <span>{{ props.row.leftGlassCorrectivevision }}</span>
                  </el-form-item>
                  <el-form-item label="右矫正视力">
                    <span>{{ props.row.rightGlassCorrectivevision }}</span>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="镜架品牌" prop="glassFramebrand"></el-table-column>
            <el-table-column label="镜片折射率" prop="glassLensindex"></el-table-column>
            <el-table-column label="镜片品牌" prop="glassLensbrand"></el-table-column>
            <el-table-column label="镜片功能" prop="glassLensfunction"></el-table-column>
            <el-table-column label="眼镜单价" prop="glassAmountofconsumption"></el-table-column>
            <el-table-column label="备注" prop="glassRemarks"></el-table-column>
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
        <el-form :model="ruleForm" :inline="true" ref="ruleForm" label-width="100px">
          <el-form-item label="眼镜店名称" prop="glassHomeName">
            <el-input v-model="ruleForm.glassHomeName"></el-input>
          </el-form-item>
          <el-form-item label="客户名" prop="consignee">
            <el-input v-model="ruleForm.consignee"></el-input>
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="ruleForm.mobile"></el-input>
          </el-form-item>
          <el-form-item label="购买时间" style="margin-right:20px">
            <el-date-picker
              v-model="ruleForm.addTime"
              format="yyyy-MM-dd HH:mm:ss"
              type="datetime"
              placeholder="购买时间"
              style="width:200px"
            ></el-date-picker>
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
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="球镜" prop="leftGlassMirror">
            <el-input v-model="ruleForm1.leftGlassMirror"></el-input>
          </el-form-item>
          <el-form-item label="柱镜" prop="leftGlassCylinder">
            <el-input v-model="ruleForm1.leftGlassCylinder"></el-input>
          </el-form-item>
          <el-form-item label="轴位" prop="leftGlassAxialposition">
            <el-input v-model="ruleForm1.leftGlassAxialposition"></el-input>
          </el-form-item>
          <el-form-item label="裸眼视力" prop="leftGlassNakedeyesight">
            <el-input v-model="ruleForm1.leftGlassNakedeyesight"></el-input>
          </el-form-item>
          <el-form-item label="矫正视力" prop="leftGlassCorrectivevision">
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
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="球镜" prop="rightGlassMirror">
            <el-input v-model="ruleForm2.rightGlassMirror"></el-input>
          </el-form-item>
          <el-form-item label="柱镜" prop="rightGlassCylinder">
            <el-input v-model="ruleForm2.rightGlassCylinder"></el-input>
          </el-form-item>
          <el-form-item label="轴位" prop="rightGlassAxialposition">
            <el-input v-model="ruleForm2.rightGlassAxialposition"></el-input>
          </el-form-item>
          <el-form-item label="裸眼视力" prop="rightGlassNakedeyesight">
            <el-input v-model="ruleForm2.rightGlassNakedeyesight"></el-input>
          </el-form-item>
          <el-form-item label="矫正视力" prop="rightGlassCorrectivevision">
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
          label-width="110px"
          class="demo-ruleForm"
        >
          <el-form-item label="瞳距" prop="glassPitch">
            <el-input v-model="ruleForm3.glassPitch"></el-input>
          </el-form-item>
          <el-form-item label="瞳高" prop="glassHigh">
            <el-input v-model="ruleForm3.glassHigh"></el-input>
          </el-form-item>
          <el-form-item label="ADD" prop="glassADD">
            <el-input v-model="ruleForm3.glassADD"></el-input>
          </el-form-item>
          <el-form-item label="斜角" prop="glassbevel">
            <el-input v-model="ruleForm3.glassbevel"></el-input>
          </el-form-item>
          <el-form-item label="镜架品牌" prop="glassFramebrand">
            <el-input v-model="ruleForm3.glassFramebrand"></el-input>
          </el-form-item>
          <el-form-item label="镜片折射率" prop="glassLensindex">
            <el-input v-model="ruleForm3.glassLensindex"></el-input>
          </el-form-item>
          <el-form-item label="镜片品牌" prop="glassLensbrand">
            <el-input v-model="ruleForm3.glassLensbrand"></el-input>
          </el-form-item>
          <el-form-item label="镜片功能" prop="glassLensfunction">
            <el-input v-model="ruleForm3.glassLensfunction"></el-input>
          </el-form-item>
          <el-form-item label="眼镜总价" prop="glassAmountofconsumption">
            <el-input v-model="ruleForm3.glassAmountofconsumption"></el-input>
          </el-form-item>
          <el-form-item label="验光员" prop="Optometrist">
            <el-input v-model="ruleForm3.Optometrist"></el-input>
          </el-form-item>
          <el-form-item label="身高" prop="buyHeight">
            <el-input v-model="ruleForm3.buyHeight"></el-input>
          </el-form-item>
          <el-form-item label="体重" prop="buyWeight">
            <el-input v-model="ruleForm3.buyWeight"></el-input>
          </el-form-item>
          <el-form-item label="年龄" prop="buyAge">
            <el-input v-model="ruleForm3.buyAge"></el-input>
          </el-form-item>
          <el-form-item label="备注" prop="glassRemarks">
            <el-input v-model="ruleForm3.glassRemarks"></el-input>
          </el-form-item>
          <el-form-item label="双眼矫正视力" prop="glassAmountofconsumption">
            <el-input v-model="ruleForm3.CorrectionWithBoth"></el-input>
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
.table-expand {
  font-size: 0;
}
.table-expand label {
  width: 90px;
  color: #99a9bf;
}
.table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>

<script>
import { mapGetters } from "vuex";
import {
  listOrder,
  shipOrder,
  refundOrder,
  detailOrder,
  deleteOrder,
  updateOrder,
  addOrder
} from "@/api/order";

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
        addTime: undefined,
        phone: undefined,
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
        orderGoods: {}
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
      isUpdate: false,
      orderId: undefined,
      orderGoodsId: undefined,
      goodsId: undefined,
      ruleForm: {
        glassHomeName: undefined,
        // glassPrice: undefined,
        glassNum: 1,
        consignee: undefined,
        mobile: undefined,
        addTime: undefined
        // address: undefined
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
        glassRemarks: undefined,
        Optometrist: undefined,
        buyHeight: undefined,
        buyWeight: undefined,
        buyAge: undefined,
        CorrectionWithBoth: undefined
      },
      tableData: [],
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
        consignee: [
          { required: true, message: "请输入购买人姓名", trigger: "blur" }
        ],
        mobile: [
          { required: true, message: "请输入购买人手机号", trigger: "blur" }
        ],
        address: [
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
        glassHigh: [{ required: true, message: "请输入瞳高", trigger: "blur" }],
        glassADD: [{ required: true, message: "请输入ADD", trigger: "blur" }],
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
      },
      orderForm: {}
    };
  },

  computed: {
    ...mapGetters(["userId"])
  },
  created() {
    console.log("订单")
    this.listQuery.userId=this.$route.query.userId;
    if(this.listQuery.userId!="undefined"){
      console.log("查找")
      this.handleFilter()

    }else{
      console.log("列表")
      this.getList();
    }
  },
  methods: {
    changeDate(dateA) {
      var dateee = new Date(dateA).toJSON();
      var date = new Date(+new Date(dateee) + 8 * 3600 * 1000)
        .toISOString()
        .replace(/T/g, " ")
        .replace(/\.[\d]{3}Z/, "");
      return date;
    },

    checkPermission,
    getList() {
      this.listLoading = true;
      if (this.listQuery.addTime != null) {
        this.listQuery.addTime[0] = this.changeDate(this.listQuery.addTime[0]);
        this.listQuery.addTime[1] = this.changeDate(this.listQuery.addTime[1]);
      }
      listOrder(this.listQuery)
        .then(response => {
          console.log(response);
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
      detailOrder(row.id)
        .then(response => {
          let data = response.data.data;
          let tableData = {};
          data.goodsAttributes.forEach(attribute => {
            tableData[attribute.attribute] = attribute.value;
          });
          this.tableData = [tableData];
          console.log(this.tableData);
          this.orderDetail.order = data.order;
          this.orderDetail.orderGoods = data.orderGoods[0];
          console.log(this.orderDetail);

          this.orderDialogVisible = true;
        })
        .catch(response => {});
    },
    handleDelete(row) {
      var that = this;
      deleteOrder(row.id)
        .then(response => {
          that.getList();
          that.$message({
            message: "删除订单成功",
            type: "success"
          });
        })
        .catch(response => {});
    },
    handleUpdate(row) {
      this.isUpdate = true;
      let that = this;
      detailOrder(row.id)
        .then(response => {
          let data = response.data.data;
          let tableData = {};
          data.goodsAttributes.forEach(attribute => {
            tableData[attribute.attribute] = attribute.value;
          });

          this.tableData = [tableData];
          this.orderDetail.order = data.order;
          this.orderDetail.orderGoods = data.orderGoods[0];
          this.dialogFormVisible = true;
          this.orderId = data.order.id;
          this.orderGoodsId = data.orderGoods[0].id;
          this.goodsId = data.orderGoods[0].goodsId;
          for (var prop in this.orderDetail.order) {
            that.ruleForm[prop] = this.orderDetail.order[prop];
          }
          that.ruleForm.glassHomeName = data.orderGoods[0].goodsName;
          for (var prop in that.tableData[0]) {
            if (prop in that.ruleForm1) {
              that.ruleForm1[prop] = this.tableData[0][prop];
            }
            if (prop in that.ruleForm2) {
              that.ruleForm2[prop] = this.tableData[0][prop];
            }
            if (prop in that.ruleForm3) {
              that.ruleForm3[prop] = this.tableData[0][prop];
            }
          }
        })
        .catch(response => {});
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
      this.isUpdate = false;
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
      var that = this;
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.active++;
          if (this.active == 5) {
            let attributes = [];
            for (let prop in this.ruleForm1) {
              attributes.push({
                attribute: prop,
                value: this.ruleForm1[prop]
              });
            }
            for (let prop in this.ruleForm2) {
              attributes.push({
                attribute: prop,
                value: this.ruleForm2[prop]
              });
            }
            for (let prop in this.ruleForm3) {
              attributes.push({
                attribute: prop,
                value: this.ruleForm3[prop]
              });
            }
            let order = {
              userId: this.userId,
              consignee: this.ruleForm.consignee,
              address: this.ruleForm.address,
              mobile: this.ruleForm.mobile,
              addTime: this.ruleForm.addTime,
              goodsAllinone: {
                goods: {
                  name: this.ruleForm.glassHomeName,
                  retailPrice: this.ruleForm.glassPrice
                },
                attributes: attributes
              }
            };
            if (that.isUpdate) {
              order.orderId = this.orderId;
              order.orderGoodsId = this.orderGoodsId;
              order.goodsId = this.goodsId;
              updateOrder(order)
                .then(response => {
                  this.refundDialogVisible = false;
                  this.$notify.success({
                    title: "成功",
                    message: "修改订单成功"
                  });
                  this.getList();
                })
                .catch(response => {
                  this.$notify.error({
                    title: "失败",
                    message: response.data.errmsg
                  });
                });
            } else {
              addOrder(order)
                .then(response => {
                  this.refundDialogVisible = false;
                  this.$notify.success({
                    title: "成功",
                    message: "添加订单成功"
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
          }
          if (this.active == 5) {
            this.dialogFormVisible = false;
            this.active = 1;
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
