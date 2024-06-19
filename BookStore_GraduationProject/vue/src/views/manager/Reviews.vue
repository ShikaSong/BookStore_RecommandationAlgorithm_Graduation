<template>
  <div>
    <div>
      <el-input style="width: 200px" placeholder="查询评论人编号" v-model="reviewerId"></el-input>
      <el-input style="width: 200px; margin: 0 5px" placeholder="查询图书asin"  v-model="asin"></el-input>
      <el-button type="primary" @click="load(1)">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>
    <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column label="书籍">
        <template v-slot="scope">
          <div style="display: flex; align-items: center;flex-wrap: wrap">
            <div style="font-size: 10px; font-weight: bolder">评论id：{{scope.row.id}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: #121c52">评论人编号：{{scope.row.reviewerId}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: #dd4141">书籍亚马逊号：{{scope.row.asin}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: #1f7842">评论人姓名：{{scope.row.reviewerName}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(171,127,232,0.64)">评分：{{scope.row.overall}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(102,48,6,0.64)">总结：{{scope.row.summary}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(92,102,6,0.64)">评论时间：{{scope.row.reviewTime}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(184,146,12,0.64)">有帮助：{{scope.row.helpful}}/{{scope.row.helpfulAll}}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="reviewText" label="评论"></el-table-column>
      <el-table-column label="操作" align="center" width="180">
        <template v-slot="scope">
          <el-button size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div style="margin: 10px 0">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="评论信息" :visible.sync="fromVisible" width="90%">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="评论人编号" prop="reviewerId">
          <el-input v-model="form.reviewerId" placeholder="评论人编号"></el-input>
        </el-form-item>
        <el-form-item label="书籍亚马逊号" prop="asin">
          <el-input v-model="form.asin" placeholder="书籍亚马逊号"></el-input>
        </el-form-item>
        <el-form-item label="评论人姓名" prop="reviewerName">
          <el-input v-model="form.reviewerName" placeholder="评论人姓名"></el-input>
        </el-form-item>
        <el-form-item label="有帮助的人数" prop="helpful">
          <el-input v-model="form.helpful" placeholder="有帮助的人数"></el-input>
        </el-form-item>
        <el-form-item label="总人数" prop="helpfulAll">
          <el-input type="textarea" v-model="form.helpfulAll" placeholder="总人数"></el-input>
        </el-form-item>
        <el-form-item label="总分" prop="overall">
          <el-input type="textarea" v-model="form.overall" placeholder="总分"></el-input>
        </el-form-item>
        <el-form-item label="总结" prop="summary">
          <el-input type="textarea" v-model="form.summary" placeholder="总结"></el-input>
        </el-form-item>
        <el-form-item label="评论时间" prop="reviewTime">
          <el-input type="textarea" v-model="form.reviewTime" placeholder="评论时间"></el-input>
        </el-form-item>
        <el-form-item label="评论" prop="reviewText">
          <div id="editor"></div>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="评论" :visible.sync="fromVisible1" width="80%">
      <el-card class="w-e-text">
        <div v-html="reviewText"></div>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="fromVisible1 = false">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import E from "wangeditor";
import hljs from "highlight.js";

export default {
  name: "Reviews",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 15,  // 每页显示的个数
      reviewerId: '',
      asin: '',
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        reviewerId: [
          { required: true, message: '请输入评论人编号', trigger: 'blur' },
        ]
      },
      ids: [],
      editor: null,
      reviewText: '',
      fromVisible1: false,
    }
  },
  created() {
    this.load()
  },
  methods: {
    showDescription(reviewText) {
      this.reviewText = reviewText
      this.fromVisible1 = true
    },
    closeDialog() {
      // 销毁编辑器
      this.editor.destroy()
      this.editor = null
    },
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确认批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/reviews/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)
    },
    del(id) {
      this.$confirm('您确认删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/reviews/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {})
    },
    setRichText() {
      this.$nextTick(() => {
        this.editor = new E(`#editor`)
        this.editor.highlight = hljs
        this.editor.config.uploadImgServer = this.$baseUrl + '/file/editor/upload'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadImgParams = {
          type: 'img',
        }
        this.editor.config.uploadVideoServer = this.$baseUrl + '/file/editor/upload'
        this.editor.config.uploadVideoName = 'file'
        this.editor.config.uploadVideoHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadVideoParams = {
          type: 'video',
        }
        this.editor.create()  // 创建
      })
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
      this.setRichText()
      setTimeout(() => {
        this.editor.txt.html(row.reviewText)  // 设置富文本内容
      }, 0)
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗

      this.setRichText()
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          // 获取编辑框的内容
          let reviewText = this.editor.txt.html()
          this.form.reviewTextt = reviewText
          this.$request({
            url: this.form.id ? '/reviews/update': '/reviews/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    reset() {
      this.reviewerId = ''
      this.asin = ''
      this.load()
    },
    load(pageNum) {  // 分页查询
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/reviews/selectByPage', {
        timeout:1000000,
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          reviewerId: this.reviewerId,
          asin: this.asin,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>