<template>
    
  <div class="app-container">
        
    <div id="main">
            <!-- 公共头 -->
            <myheader />       
      <div class="main-container">
                <el-scrollbar class="page-component__scroll">
                  <!-- 内容区域 -->
                  <nuxt />    
        </el-scrollbar>
              
      </div>
              <!-- 公共底 -->
            <myfooter />     
    </div>
      
  </div>
</template>
<script>
import "~/assets/css/app.css";
import "~/assets/css/chunk.css";
import "~/assets/css/iconfont.css";
import "~/assets/css/main.css";

import myheader from "./myheader";
import myfooter from "./myfooter";
import hospApi from "@/api/hosp";
export default {
  data(){
    return{
      state:"",
    }
  },
  components: {
    
    myheader,
    myfooter,
  },

  methods: {
    querySearchAsync(queryString, cb) {
      this.searchObj = [];
      if (queryString == "") return;
      hospApi.getByHosname(queryString).then((response) => {
        for (let i = 0, len = response.data.length; i < len; i++) {
          response.data[i].value = response.data[i].hosname;
        }
        cb(response.data);
      });
    },
    //在下拉框选择某一个内容，执行下面方法，跳转到详情页面中
    handleSelect(item) {
      window.location.href = "/hospital/" + item.hoscode;
    },
  },
};
</script>
