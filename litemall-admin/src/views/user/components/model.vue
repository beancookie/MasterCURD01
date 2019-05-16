<template>
  <div class="model">
    <div class="model_content">
      <div class="model_head">
        <i
          class="el-icon-error"
          @click="closeModel"/>
      </div>
      <div class="app-container">
        <!-- 查询和其他操作 -->
        <div class="filter-container">
          <div>
            <el-input
              v-model="listQuery.name"
              clearable
              class="filter-item"
              style="width: 200px;"
              placeholder="请输入优惠券标题" />
          </div>
          <div>
            <el-select
              v-model="listQuery.type"
              clearable
              style="width: 200px"
              class="filter-item"
              placeholder="请选择优惠券类型">
              <el-option
                v-for="type in typeOptions"
                :key="type.value"
                :label="type.label"
                :value="type.value" />
            </el-select>
          </div>
          <div>
            <el-select
              v-model="listQuery.status"
              clearable
              style="width: 200px"
              class="filter-item"
              placeholder="请选择优惠券状态">
              <el-option
                v-for="type in statusOptions"
                :key="type.value"
                :label="type.label"
                :value="type.value" />
            </el-select>
          </div>
          <div>
            <el-button
              v-permission="['GET /admin/coupon/list']"
              class="filter-item"
              type="primary"
              icon="el-icon-search"
              @click="handleFilter">查找</el-button>
          </div>
          <div>
            <el-button
              :loading="downloadLoading"
              class="filter-item"
              type="primary"
              icon="el-icon-download"
              @click="handleDownload">导出</el-button>
          </div>
        </div>

        <!-- 查询结果 -->
        <el-table
          v-loading="listLoading"
          :data="list"
          element-loading-text="正在查询中。。。"
          border
          fit
          highlight-current-row>

          <el-table-column
            align="center"
            width="120px"
            label="优惠券ID"
            prop="coupon.id"
            sortable />

          <el-table-column
            align="center"
            label="优惠券名称"
            prop="coupon.name" />

          <el-table-column
            align="center"
            label="最低消费"
            prop="coupon.min">
            <template slot-scope="scope">满{{ scope.row.coupon.min }}元可用</template>
          </el-table-column>

          <el-table-column
            align="center"
            label="满减金额"
            prop="coupon.discount">
            <template slot-scope="scope">减免{{ scope.row.coupon.discount }}元</template>
          </el-table-column>

          <el-table-column
            align="center"
            label="商品使用范围"
            prop="coupon.goodsType">
            <template slot-scope="scope">{{ scope.row.coupon.goodsType | formatGoodsType }}</template>
          </el-table-column>

          <el-table-column
            align="center"
            label="优惠券类型"
            prop="coupon.type">
            <template slot-scope="scope">{{ scope.row.coupon.type | formatType }}</template>
          </el-table-column>

          <el-table-column
            align="center"
            label="领取时间"
            prop="couponUser.addTime" />
          <el-table-column
            align="center"
            label="使用时间"
            prop="couponUser.updateTime" />

          <el-table-column
            align="center"
            label="使用状态"
            prop="couponUser.status">
            <template slot-scope="scope">{{ scope.row.couponUser.status | formatStatus }}</template>
          </el-table-column>

          <el-table-column
            align="center"
            label="操作"
            width="100"
            class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                :disabled="scope.row.couponUser.status !== 0"
                type="primary"
                size="mini"
                @click="handleConsume(scope.row)">使用</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="listQuery.page"
          :limit.sync="listQuery.limit"
          @pagination="getList" />
      </div>
    </div>
  </div>
</template>

<style>
  .model {
    width: 100%;
    height: 100%;
    background: rgba(9, 13, 21, 0.5);
    position: fixed;
    top: 0;
    left: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 100;
  }

  .model_content {
    width: 900px;
    background: #fff;
  }

  .model_head {
    margin-top: 10px;
    float: right;
    margin-right: 20px;
    font-size: 20px;
  }
  .el-icon-error {
    color: #ccc;
    cursor: pointer;
  }
  .app-container {
    margin-top: 30px;
  }
  .filter-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }

  .avatar-uploader .el-upload {
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
    width: 120px;
    height: 120px;
    display: block;
  }
</style>

<script>
import { listCoupon, consume } from '@/api/coupon'
import Pagination from '@/components/Pagination' // Secondary package based on el-pagination

const defaultTypeOptions = [
  {
    label: '通用领券',
    value: 0
  },
  {
    label: '注册赠券',
    value: 1
  },
  {
    label: '兑换码',
    value: 2
  }
]

const defaultStatusOptions = [
  {
    label: '正常',
    value: 0
  },
  {
    label: '已使用',
    value: 1
  },
  {
    label: '已下架',
    value: 2
  }
]

export default {
  name: 'Model',
  components: { Pagination },
  filters: {
    formatType(type) {
      for (let i = 0; i < defaultTypeOptions.length; i++) {
        if (type === defaultTypeOptions[i].value) {
          return defaultTypeOptions[i].label
        }
      }
      return ''
    },
    formatGoodsType(goodsType) {
      if (goodsType === 0) {
        return '全场通用'
      } else if (goodsType === 1) {
        return '指定分类'
      } else {
        return '指定商品'
      }
    },
    formatStatus(status) {
      if (status === 0) {
        return '正常'
      } else if (status === 1) {
        return '已使用'
      } else {
        return '已下架'
      }
    }
  },
  props: ['userId'],
  data() {
    return {
      typeOptions: Object.assign({}, defaultTypeOptions),
      statusOptions: Object.assign({}, defaultStatusOptions),
      list: undefined,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        userId: this.userId,
        sort: 'add_time',
        order: 'desc'
      },
      downloadLoading: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
    closeModel() {
      this.$emit('closeToModel')
    },
    getList() {
      this.listLoading = true
      listCoupon(this.listQuery)
        .then(response => {
          this.list = response.data.data.items
          this.total = response.data.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleConsume(row) {
      this.listLoading = true
      consume({ userId: row.couponUser.userId, couponId: row.coupon.id })
        .then(response => {
          this.$notify.success({
            title: '成功',
            message: '使用优惠券成功'
          })
          this.listLoading = false
          this.getList()
        })
        .catch(() => {
          this.$notify.success({
            title: '成功',
            message: '使用优惠券成功'
          })
          this.listLoading = false
        })
    },
    handleDownload() {
      this.downloadLoading = true
        import('@/vendor/Export2Excel').then(excel => {
          const tHeader = [
            '优惠券ID',
            '名称',
            '内容',
            '标签',
            '最低消费',
            '减免金额',
            '每人限领',
            '优惠券数量'
          ]
          const filterVal = [
            'id',
            'name',
            'desc',
            'tag',
            'min',
            'discount',
            'limit',
            'total'
          ]
          excel.export_json_to_excel2(tHeader, this.list, filterVal, '优惠券信息')
          this.downloadLoading = false
        })
    }
  }
}
</script>

