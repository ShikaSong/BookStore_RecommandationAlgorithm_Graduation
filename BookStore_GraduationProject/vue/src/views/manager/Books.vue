<template>
  <div>
    <div>
      <el-input style="width: 200px" placeholder="查询书籍" v-model="title"></el-input>
      <el-button type="primary" style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
    </div>
    <div style="margin: 10px 0">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>
    <el-table  :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"></el-table-column>
      <el-table-column style=""   align="center">
        <template slot-scope="scope">
          <el-image :src="scope.row.imurl" fit= "scale-down" :preview-src-list="[scope.row.imurl]">
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </el-image>
          <div style="font-size: 15px; font-weight: bolder">{{scope.row.title}}</div>
          <div style="display: flex; flex-wrap: wrap">

            <div style="font-size: 10px; font-weight: bolder">序号:{{scope.row.id}}</div>
            <div style="font-size: 10px; font-weight: bolder; color:#112638">亚马逊书号:{{scope.row.asin}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: #dd4141">价格:￥{{scope.row.price}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: #1f7842">上传者:{{scope.row.author}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(171,127,232,0.64)">发布日期:{{scope.row.time?scope.row.time:'null '}}</div>
            <div style="font-size: 10px; font-weight: bolder;color: rgba(102,48,6,0.64)">销售排行:{{scope.row.salesrank}}</div>

          </div></template>
      </el-table-column>
<!--      <el-table-column prop="price" label="价格"></el-table-column>-->
      <el-table-column prop="categories" label="分类"></el-table-column>
      <el-table-column prop="description" label="简介">
        <template v-slot="scope">
          <el-button @click="showDescription(scope.row.description)" size="mini">显示简介</el-button>
          <el-button @click="$router.push('/booksDetail?id=' + scope.row.id)" size="mini">在详情页显示</el-button>
        </template>
      </el-table-column>
<!--      <el-table-column prop="author" label="发布人"></el-table-column>-->
<!--      <el-table-column prop="time" label="发布时间"></el-table-column>-->
<!--      <el-table-column prop="salesrank" label="销售排行"></el-table-column>-->
      <el-table-column style="height: 400px; overflow: auto" prop="boughtTogether" label="同时购买"></el-table-column>
      <el-table-column style="height: 400px; overflow: auto" prop="buyAfterViewing" label="浏览后购买"></el-table-column>
      <el-table-column style="height: 400px; overflow: auto" prop="alsoBought" label="也购买了"></el-table-column>
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

    <el-dialog title="书籍信息" :visible.sync="fromVisible" width="60%" @close="closeDialog" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="标题"></el-input>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="form.price" placeholder="价格"></el-input>
        </el-form-item>
        <el-form-item label="分类" prop="categories">
          <el-input v-model="form.categories" placeholder="分类"></el-input>
        </el-form-item>
        <el-form-item label="asin码" prop="asin">
          <el-input v-model="form.asin" placeholder="asin码"></el-input>
        </el-form-item><el-form-item label="封面" prop="imurl">
          <el-input v-model="form.imurl" placeholder="asin码"></el-input>
        </el-form-item><el-form-item label="同时购买" prop="bought_together">
          <el-input v-model="form.boughtTogether" placeholder="同时购买"></el-input>
        </el-form-item><el-form-item label="浏览后购买" prop="buy_after_viewing">
          <el-input v-model="form.buyAfterViewing" placeholder="浏览后购买"></el-input>
        </el-form-item><el-form-item label="也购买了" prop="also_bought">
          <el-input v-model="form.alsoBought" placeholder="也购买了"></el-input>
        </el-form-item><el-form-item label="销售排行" prop="salesRank">
          <el-input v-model="form.salesrank" placeholder="销售排行"></el-input>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <div id="editor"></div>
        </el-form-item>

      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="简介" :visible.sync="fromVisible1" width="60%">
      <el-card class="w-e-text">
        <div v-html="description"></div>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="fromVisible1 = false">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import E from "wangeditor"
import hljs from 'highlight.js'

export default {
  name: "Books",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 15,  // 每页显示的个数
      username: '',
      title: '',
      categories:'',
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        title: [
          { required: true, message: '请输入书名', trigger: 'blur' },
        ]
      },
      ids: [],
      editor: null,
      description: '',
      fromVisible1: false,
    }
  },
  created() {
    this.load()
  },
  methods: {
    showDescription(description) {
      this.description = description
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
        this.$request.delete('/books/delete/batch', { data: this.ids }).then(res => {
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
        this.$request.delete('/books/delete/' + id).then(res => {
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
        this.editor.txt.html(row.description)  // 设置富文本内容
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
          let description = this.editor.txt.html()
          this.form.descriptiont = description
          this.$request({
            url: this.form.id ? '/books/update': '/books/add',
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
      this.title = ''
      this.load()
    },
    load(pageNum) {  // 分页查询
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/books/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title,
          categories: this.categories,
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