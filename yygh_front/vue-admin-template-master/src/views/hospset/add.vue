<template>

  <div class="app-container">
                <el-form
      label-width="120px"
      :model="hospitalSet"
      :rules="addHospitalSetRules"
      ref="addHospitalSetForm"
    >
               <el-form-item label="医院名称" prop="hosname">
                    <el-input v-model="hospitalSet.hosname" />
                 </el-form-item
      >
               <el-form-item label="医院编号" prop="hoscode">
                    <el-input v-model="hospitalSet.hoscode" />
                 </el-form-item
      >
               <el-form-item label="api基础路径" prop="apiUrl">
                    <el-input v-model="hospitalSet.apiUrl" />
                 </el-form-item
      >
               <el-form-item label="联系人姓名" prop="contactsName">
                    <el-input v-model="hospitalSet.contactsName" />
                 </el-form-item
      >
               <el-form-item label="联系人手机" prop="contactsPhone">
                    <el-input v-model="hospitalSet.contactsPhone" />
                 </el-form-item
      >
               <el-form-item>
                    <el-button
          type="primary"
          @click="saveOrUpdate('addHospitalSetForm')"
          >保存</el-button
        >
                   <el-button type="default" @click="resetForm">重置</el-button>
                 </el-form-item
      >
            </el-form
    >
       
  </div>
</template>

<script>
import hospset from "@/api/hospset";

export default {
  data() {
    return {
      hospitalSet: {
        hosname: "",
        hoscode: "",
        apiUrl: "",
        contactsName: "",
        contactsPhone: "",
      },
    };
  },

  computed: {
    addHospitalSetRules() {
      return {
        hosname: {
          required: true,
          message: "请输入医院名称",
          trigger: "blur",
        },
        hoscode: {
          required: true,
          message: "请输入医院编号",
          trigger: "blur",
        },
        apiUrl: {
          required: true,
          message: "请输入医院api地址",
          trigger: "blur",
        },
        contactsName: {
          required: true,
          message: "请输入联系人名称",
          trigger: "blur",
        },
        contactsPhone: {
          required: true,
          message: "请输入电话号码",
          trigger: "blur",
        },
      };
    },
  },

  async created() {
    //回显路由id，如果路由id不为空
    if (this.$route.params && this.$route.params.id) {
      const id = this.$route.params.id;
      this.getHospSet(this.$route.params.id);
    } else {
      this.resetForm();
    }
  },

  methods: {
    //根据id回显信息
    getHospSet(id) {
      hospset
        .getHospSetById(id)
        .then((res) => {
          // console.log(res);
          if (res.code == 200) {
            this.hospitalSet = res.data;
          }
        })
        .catch((e) => {
          return this.$message.error("查询失败");
        });
    },

    //重置
    resetForm() {
      this.hospitalSet = {};
    },

    //保存或更新
    saveOrUpdate(addHospitalSetForm) {
      var routeId = this.$route.params.id;
      console.log(routeId);
      this.$refs[addHospitalSetForm].validate((valid) => {
        if (!valid) {
          return this.$message.error("医院信息有误,请检查后重新提交");
        } else {
          if (routeId != null) {
            //不等于null代表是更新医院信息
            hospset
              .updateHospSet(this.hospitalSet)
              .then((res) => {
                // console.log(res);
                if (res.code == 200) {
                  this.$message.success("更新成功");
                  this.$router.push({ path: "/hospset/list" });
                }
              })
              .catch((e) => {
                return this.$message.error("更新失败");
              });
          } else {
            //等于null代表是新添加页面
            hospset
              .addHospSet(this.hospitalSet)
              .then((res) => {
                if (res.code == 200) {
                  this.$message.success("添加成功!");
                  this.$router.push({ path: "/hospset/list" });
                }
              })
              .catch((e) => {
                return this.$message.error("添加失败");
              });
          }
        }
      });
    },
  },
};
</script>
