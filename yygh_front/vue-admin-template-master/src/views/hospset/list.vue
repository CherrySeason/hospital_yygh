<template>
  <div class="app-container">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-input v-model="searchObj.hosname" placeholder="医院名称" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="searchObj.hoscode" placeholder="医院编号" />
      </el-form-item>
      <el-button type="primary" icon="el-icon-search" @click="getList()"
        >查询</el-button
      >
      <el-button @click="getReset()">清空</el-button>
      <el-button type="danger" @click="removeRows()">批量删除</el-button>
    </el-form>

    <!-- 表单数据 -->
    <el-table
      :data="list"
      stripe
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" width="50" label="序号" />
      <el-table-column prop="hosname" label="医院名称" />
      <el-table-column prop="hoscode" label="医院编号" />
      <el-table-column prop="apiUrl" label="api基础路径" width="200" />
      <el-table-column prop="contactsName" label="联系人姓名" />
      <el-table-column prop="contactsPhone" label="联系人手机" />
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          {{ scope.row.status === 1 ? "可用" : "不可用" }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button
            type="danger"
            size="mini"
            icon="el-icon-delete"
            @click="removeDataById(scope.row.id)"
          ></el-button>
          <el-button
            v-if="scope.row.status == 1"
            type="warning"
            size="mini"
            @click="lockHostSet(scope.row.id, 0)"
            >锁定</el-button
          >
          <el-button
            v-if="scope.row.status == 0"
            type="primary"
            size="mini"
            @click="lockHostSet(scope.row.id, 1)"
            >解锁</el-button
          >
          <router-link
            :to="'/hospSet/edit/' + scope.row.id"
            style="margin-left: 10px"
          >
            <el-button
              type="primary"
              size="mini"
              icon="el-icon-edit"
            ></el-button>
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      :current-page="current"
      :page-size="limit"
      :total="total"
      style="padding: 30px 0;  text-align: center"
      layout="total, prev, pager, next, jumper"
      @current-change="getList"
    />
  </div>
</template>

<script>
//引入接口定义的js文件
import hospset from "@/api/hospset";

export default {
  data() {
    return {
      current: 1, //当前页
      limit: 10, //每页显示记录数
      searchObj: {}, //条件封装对象
      total: 0, //总记录数
      list: [], //每页数据集合
      loading: false, //重置搜索框后，页面出现加载的效果
      multipleSelection: [] // 批量选择中选择的记录列表
    };
  },
  async created() {
    this.getList(this.current);
  },
  methods: {
    //医院设置列表
    async getList(page = 1) {
      //添加当前页参数
      this.current = page;
      hospset
        .getHospSetList(this.current, this.limit, this.searchObj)
        .then(res => {
          console.log(res);
          this.total = res.data.total;
          this.list = res.data.records;
        }) //请求成功
        .catch(error => {
          console.log(error);
        }); //请求错误
    },

    //重置搜索框
    async getReset() {
      this.searchObj = {};
      this.loading = true;
      setTimeout(() => {
        this.loading = false;
        this.getList(1);
      }, 500);
    },

    //根据id删除医院信息
    async removeDataById(id) {
      this.$confirm("此操作将永远删除该医院信息，是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        //点击确实后执行
        hospset
          .deleteHospSet(id)
          .then(res => {
            if (res.code == 200) {
              this.$message.success("删除成功");
            }
            this.getList(this.current); //不管删除是否成功都要刷新页面
          })
          .catch(() => {
            this.getList(this.current);
          });
      });
    },

    //当表格复选框选项发生变化的时候触发
    async handleSelectionChange(selection) {
      console.log(selection.length);
      this.multipleSelection = selection;
    },

    //批量删除
    async removeRows(multipleSelection) {
      console.log(this.multipleSelection);
      if (this.multipleSelection.length == 0) {
        return this.$message.error("请选择删除对象");
      }
      this.$confirm("此操作将永远删除该医院信息，是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        const idList = [];
        this.multipleSelection.forEach(n => {
          idList.push(n.id);
        });
        hospset
          .deleteSetSelectedHospSet(idList)
          .then(res => {
            console.log(res);
            if (res.code == 200) {
              this.$message.success("删除成功");
            }
            this.getList(this.current);
          })
          .catch(() => {
            this.getList(1);
          });
      });
    },

    //锁定和解锁
    async lockHostSet(id, status) {
      hospset
        .lockHospSet(id, status)
        .then(res => {
          // console.log(res);
          if (res.code == 200) {
            this.$message.success("修改锁定状态成功!");
          }
          this.getList(this.current);
        })
        .catch(() => {
          this.getList(1);
        });
    }
  }
};
</script>
