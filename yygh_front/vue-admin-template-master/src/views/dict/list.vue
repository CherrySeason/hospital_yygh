<template>
      
  <div class="app-container">
    <el-form
      :inline="true"
      class="demo-form-inline"
      style="margin-bottom: 10px"
    >
         <el-button type="primary" icon="el-icon-download" @click="exportData"
        >导出</el-button
      >
         <el-button icon="el-icon-upload2" @click="uploadData">导入</el-button>
    </el-form>

    <!-- 上传弹框层 -->
    <el-dialog title="导入" :visible.sync="dialogImportVisible" width="480px">
      <el-form label-position="right" label-width="170px">
        <el-form-item label="文件">
          <el-upload
            :multiple="false"
            :on-success="onUploadSuccess"
            :action="'http://localhost:8202/admin/cmn/dict/importDict'"
            class="upload-demo"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传xls文件，且不超过500kb
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogImportVisible = false"> 取消 </el-button>
      </div>
    </el-dialog>

            <el-table
      :data="dictList"
      style="width: 100%"
      row-key="id"
      border
      lazy
      :load="getChildData"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
                  <el-table-column label="名称" width="230" align="left">
                    <template slot-scope="scope">
                      <span>{{ scope.row.name }}</span>             </template
        >
                    </el-table-column
      >

                  <el-table-column label="编码" width="220">
                    <template slot-scope="{ row }">
                              {{ row.dictCode }}             </template
        >
                    </el-table-column
      >
                  <el-table-column label="值" width="230" align="left">
                    <template slot-scope="scope">
                      <span>{{ scope.row.value }}</span>             </template
        >
                    </el-table-column
      >
                  <el-table-column label="创建时间" align="center">
                    <template slot-scope="scope">
                      <span>{{ scope.row.createTime }}</span>
                      </template
        >
                    </el-table-column
      >
              </el-table
    >
        
  </div>
</template>


<script>
import dictApi from "@/api/dict";

export default {
  data() {
    return {
      dictList: [], //数据词典数据
      dialogImportVisible: false,
    };
  },

  created() {
    this.getDictList(1);
  },

  methods: {
    //上传数据词典数据
    uploadData() {
      this.dialogImportVisible = true;
    },

    //上传成功调用
    onUploadSuccess(file) {
      console.log(file);
      this.$message.success("上传成功");
      this.dialogImportVisible = false;
      this.getDictList(1);
    },

    //导出数据词典数据
    exportData() {
      window.location.href = "http://localhost:8202/admin/cmn/dict/exportDict";
    },

    //获取数据词典数据
    getDictList(id) {
      dictApi
        .getDictListById(id)
        .then((res) => {
          if (res.code == 200) {
            this.dictList = res.data;
          }
        })
        .catch((e) => {
          this.$message.error("获取词典失败");
        });
    },

    //是否有子节点
    getChildData(tree, treeNode, resolve) {
      dictApi
        .getDictListById(tree.id)
        .then((res) => {
          resolve(res.data);
        })
        .catch((e) => {
          this.$message.error("获取词典子节点失败");
        });
    },
  },
};
</script>

