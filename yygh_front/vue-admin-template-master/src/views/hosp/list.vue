<template>
  <div class="app-container">
    <el-form :inline="true" class="demo-form-inline">
      <el-form-item>
        <el-select
          v-model="searchObj.provinceCode"
          placeholder="请选择省"
          @change="provinceChanged"
        >
          <el-option
            v-for="item in provinceList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-select
          v-model="searchObj.cityCode"
          placeholder="请选择市"
          @change="cityChanged"
        >
          <el-option
            v-for="item in cityList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-input v-model="searchObj.hosname" placeholder="医院名称" />
      </el-form-item>

      <el-button type="primary" icon="el-icon-search" @click="fetchData()"
        >查询</el-button
      >
      <el-button type="default" @click="resetData()">清空</el-button>
    </el-form>

    <!-- banner列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">{{
          (page - 1) * limit + scope.$index + 1
        }}</template>
      </el-table-column>

      <el-table-column label="医院logo">
        <template slot-scope="scope">
          <img
            :src="'data:image/jpeg;base64,' + scope.row.logoData"
            width="80"
          />
        </template>
      </el-table-column>

      <el-table-column prop="hosname" label="医院名称" />
      <el-table-column prop="param.hostypeString" label="等级" width="90" />
      <el-table-column prop="param.fullAddress" label="详情地址" />
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">{{
          scope.row.status === 0 ? "未上线" : "已上线"
        }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />

      <el-table-column label="操作" width="230" align="center">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status == 1"
            type="danger"
            size="mini"
            @click="updateStatus(scope.row.id, 0)"
            >下线</el-button
          >
          <el-button
            v-if="scope.row.status == 0"
            type="primary"
            size="mini"
            @click="updateStatus(scope.row.id, 1)"
            >上线</el-button
          >
          <router-link :to="'/hospSet/hosp/show/' + scope.row.id">
            <el-button type="primary" size="mini" style="margin-left: 10px"
              >查看</el-button
            >
          </router-link>
          <router-link :to="'/hospSet/hosp/schedule/' + scope.row.hoscode">
            <el-button type="primary" size="mini" style="margin-left: 10px"
              >排班</el-button
            >
          </router-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      :page-sizes="[5, 10, 20, 30, 40, 50, 100]"
      style="padding: 30px 0;  text-align: center"
      layout="sizes, prev, pager, next, jumper, ->, total, slot"
      @current-change="fetchData"
      @size-change="changeSize"
    />
  </div>
</template>

<script>
import hospitalApi from "@/api/hosp";
import dictApi from "@/api/dict";

export default {
  data() {
    return {
      listLoading: true, // 数据是否正在加载
      list: null, // banner列表：医院数据
      total: 0, // 数据库中的总记录数
      page: 1, // 默认页码
      limit: 10, // 每页记录数
      searchObj: {}, // 查询表单对象
      provinceList: [],
      cityList: [],
      districtList: []
    };
  },

  created() {
    this.$forceUpdate(); //解决不显示问题
    this.fetchData(this.page);

    //在页面加载前查询所有省
    hospitalApi
      .getByDictCode("Province")
      .then(res => {
        // console.log(res);
        this.provinceList = res.data;
      })
      .catch(e => {
        this.$message.error("获取省份数据失败");
      });
  },

  methods: {
    //改变上线状态
    updateStatus(id, status) {
      hospitalApi
        .updateStatus(id, status)
        .then(res => {
          console.log(res);
          if (res.code == 200) {
            this.fetchData(this.page); //保持当前页
            this.$message.success("修改状态成功");
          }
        })
        .catch(e => {
          this.$message.error("修改状态失败");
          this.fetchData(1); //回到第一页
        });
    },

    //改变每页显示多少数据
    changeSize(size) {
      this.limit = size;
      this.fetchData(1);
    },

    //重置搜索框
    resetData() {
      this.searchObj = {};
      this.listLoading = true;
      setTimeout(() => {
        this.llistLoading = false;
        this.fetchData(1);
      }, 500);
    },

    //选择城市
    cityChanged() {
      this.$forceUpdate(); //解决城市不显示问题
      this.fetchData(1);
    },

    //选择省份，显示这个省份中的所有城市
    provinceChanged() {
      //初始化数据
      this.cityList = [];
      this.districtList = [];
      this.searchObj.cityCode = null;
      dictApi
        .getDictListById(this.searchObj.provinceCode)
        .then(res => {
          //   console.log(res);
          if (res.code == 200) {
            this.cityList = res.data;
          }
        })
        .catch(e => {
          this.$message.error("获取城市数据失败");
        });
    },

    // 默认第一页
    fetchData(page = 1) {
      //添加当前页参数
      this.page = page;
      this.listLoading = true;
      hospitalApi
        .getHospList(this.page, this.limit, this.searchObj)
        .then(res => {
          console.log(res);
          if (res.code == 200) {
            this.total = res.data.totalElements; //总记录数
            this.list = res.data.content; //医院数据
            this.$forceUpdate(); //解决不显示问题
            setTimeout(() => {
              this.listLoading = false;
            }, 300);
          }
        })
        .catch(e => {
          this.$message.error("获取医院数据失败");
        });
    }
  }
};
</script>

<style></style>
