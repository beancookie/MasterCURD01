<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <div>
        <el-input
          v-model="listQuery.username"
          clearable
          class="filter-item"
          style="width: 200px;"
          placeholder="请输入用户名"
        />
        <el-input
          v-model="listQuery.mobile"
          clearable
          class="filter-item"
          style="width: 200px;"
          placeholder="请输入手机号"
        />
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
        <el-button
          :loading="downloadLoading"
          class="filter-item"
          type="primary"
          icon="el-icon-download"
          @click="handleDownload"
        >导出</el-button>
      </div>
      <div>
        <el-switch
          v-model="isRandom"
          style="margin-right: 20px;"
          active-color="#13ce66"
          inactive-color="#f56c6c"
          active-text="随机积分签到"
          inactive-text="固定积分签到"
          @change="switchChange"
        />
        <el-button
          :disabled="update.signinType == 0 ? true : false"
          @click="editSign"
          type="primary"
          size="small" 
          round
          >修改固定积分
        </el-button>
      </div>
    </div>
    <el-dialog :visible.sync="enitSigninIntrgral" title="编辑签到积分" width="30%">
      <el-form ref="update">
        <el-form-item label="输入签到积分" prop="signIntegral">
          <el-input-number v-model="update.signinIntegral" :min="1" :max="10" :step="1"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="enitUserIntrgral = false">取 消</el-button>
        <el-button type="primary" @click="editSignIntegral">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog :visible.sync="enitUserIntrgral" title="编辑用户积分" width="30%">
      <el-form ref="dataForm">
        <el-form-item label="输入积分" prop="integral">
          <el-input-number v-model="dataForm.integral" :min="1" :step="1"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="enitUserIntrgral = false">取 消</el-button>
        <el-button type="primary" @click="sure">确 定</el-button>
      </span>
    </el-dialog>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="正在查询中。。。"
      border=""
      fit
      highlight-current-row
    >
      <el-table-column align="center" width="100px" label="用户ID" prop="id" sortable/>
      <el-table-column align="center" label="用户名" prop="username"/>
      <el-table-column align="center" label="手机号码" prop="mobile"/>
      <el-table-column align="center" label="性别" prop="gender">
        <template slot-scope="scope">
          <el-tag>{{ genderDic[scope.row.gender] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="生日" prop="birthday"/>
      <el-table-column align="center" label="用户等级" prop="userLevel">
        <template slot-scope="scope">
          <el-tag>{{ levelDic[scope.row.userLevel] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status">
        <template slot-scope="scope">
          <el-tag>{{ statusDic[scope.row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="积分" prop="integral"/>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
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
  </div>
</template>

<style lang="scss" scoped>
.filter-container {
  display: flex;
  justify-content: space-between;
}
</style>


<script>
import { fetchList, addById, getType, chanSign } from "@/api/user";
import Pagination from "@/components/Pagination"; // Secondary package based on el-pagination

export default {
  name: "User",
  components: { Pagination },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        username: undefined,
        mobile: undefined,
        sort: "add_time",
        order: "desc"
      },
      downloadLoading: false,
      genderDic: ["未知", "男", "女"],
      levelDic: ["普通用户", "VIP用户", "高级VIP用户"],
      statusDic: ["可用", "禁用", "注销"],
      enitUserIntrgral: false,
      enitSigninIntrgral: false,
      dataForm: {
        id: undefined,
        integral: undefined
      },
      isRandom: true,
      update: {
        signinType: undefined,
        signinIntegral: undefined
      }
    };
  },
  created() {
    this.getList();
    this.getType();
  },
  methods: {
    getList() {
      this.listLoading = true;
      fetchList(this.listQuery)
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
    getType() {
      this.listLoading = true;
      getType()
        .then(response => {
          this.update = response.data.data;
          this.isRandom = this.update.signinType == 0
          this.listLoading = false;
        })
        .catch(err => {
          this.listLoading = false;
        });
    },
    handleFilter() {
      this.listQuery.page = 1;
      this.getList();
    },
    handleDownload() {
      this.downloadLoading = true;
      import("@/vendor/Export2Excel").then(excel => {
        const tHeader = ["用户名", "手机号码", "性别", "生日", "状态", "积分"];
        const filterVal = [
          "username",
          "mobile",
          "gender",
          "birthday",
          "status",
          "integral"
        ];
        excel.export_json_to_excel2(tHeader, this.list, filterVal, "用户信息");
        this.downloadLoading = false;
      });
    },
    handleEdit(index, row) {
      this.enitUserIntrgral = true;
      this.dataForm.integral = row.integral;
      this.dataForm.id = row.id;
      // console.log(index);
    },
    sure() {
      this.enitUserIntrgral = false;
      addById(this.dataForm)
        .then(response => {
          this.getList();
          console.log(this.dataForm);
        })
        .catch(error => {
          console.log(error);
        });
    },
    switchChange() {
      if (this.update.signinType == 0) {
        this.update.signinType = 1
      } else if (this.update.signinType == 1) {
        this.update.signinType = 0
      }
      this.editSignIntegral();
    },
    editSign() {
      this.enitSigninIntrgral = true;
    },
    editSignIntegral() {
      chanSign(this.update)
        .then(response => {
          this.enitSigninIntrgral = false;
        })
        .catch(err => {
          console.log(err);
          this.enitSigninIntrgral = false;
        });
    }
  }
};
</script>
