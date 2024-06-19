<template>
  <div>
    <div style="margin: 10px 0 20px 0; font-size: 30px; font-weight: bold">{{ book.title }}</div>
    <div style="margin: 10px 0; font-size: 13px; color: #666">{{ book.author }}  <span style="margin-left: 20px">{{ book.time }}</span></div>
    <div style=' box-shadow: 0 0 10px rgba(0, 0, 0, .12); padding: 10px;border-radius: 5%; '>
      <b><i v-html="book.categories" ></i></b>
    </div>
    <div style="display: flex;justify-content: center;">
      <el-image style="top: 15px" :src="book.imurl" fit= "scale-down" :preview-src-list="[book.imurl]">
        <div slot="placeholder" class="image-slot">
          加载中<span class="dot">...</span>
        </div>
      </el-image>
    </div>
    <el-divider></el-divider>
    <div class="text-container">
      <p v-for="(paragraph, index) in book.description.split('.')" :key="index" class="paragraph">{{ paragraph }}</p>
    </div>
    <el-container>
      <el-header style="color: #cccccc"></el-header>
      <el-main>
        <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'white', color: '#666' }">
          <el-table-column label="精彩点评(评论加载可能需要几分钟，请耐心等待)">
            <template v-slot="scope">
              <div style="display: flex; align-items: center;flex-wrap: wrap">
                <div style="font-size: 10px; font-weight: bolder">评论id：{{scope.row.id}}</div>
                <div style="font-size: 10px; font-weight: bolder;color: #121c52">评论人编号：{{scope.row.reviewerId}}</div>
                <div style="font-size: 10px; font-weight: bolder;color: #25418b">书籍亚马逊号：{{scope.row.asin}}</div>
                <div style="font-size: 10px; font-weight: bolder;color: #1f7842">评论人姓名：{{scope.row.reviewerName}}</div>
/*                <div style="font-size: 10px; font-weight: bolder;color: rgba(186,27,50,0.64)">评分：{{scope.row.overall}}</div>*/
                <el-rate
                    v-model="scope.row.overall"
                    disabled
                    :colors="colors"
                    show-score
                    text-color="#ff9900"
                    score-template="{value}">
                </el-rate>
                <div style="font-size: 10px; font-weight: bolder;color: rgba(102,48,6,0.64)">总结：{{scope.row.summary}}</div>
                <div style="font-size: 10px; font-weight: bolder;color: rgba(92,102,6,0.64)">评论时间：{{scope.row.reviewTime}}</div>
                <div style="font-size: 10px; font-weight: bolder;color: rgba(184,146,12,0.64)">有帮助：{{scope.row.helpful}}/{{scope.row.helpfulAll}}</div>
              </div>
              <div>{{scope.row.reviewText}}</div>
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
      </el-main>
    </el-container>

    <el-dialog title="写评论" :visible.sync="fromVisible" width="90%">
      <el-form :model="form" label-width="80px" style="padding-right: 20px" :rules="rules" ref="formRef">
        <el-form-item label="总分" prop="overall">
          <el-rate
              v-model="form.overall"
              :colors="colors"
              show-score
          >
          </el-rate>
        </el-form-item>
        <el-form-item label="总结" prop="summary">
          <el-input type="textarea" v-model="form.summary" placeholder="总结"></el-input>
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
    <el-button style="position: absolute;bottom: 25%;z-index:999; right: 2%" icon="el-icon-edit" type="warning" plain @click="handleAdd"></el-button>
    <el-button style="position: absolute;bottom: 20%;z-index:999; right: 2%" :icon="starIcon" type="success" plain @click="star"></el-button>
    <el-button style="position: absolute;bottom: 15%;z-index:999; right: 2%" icon="el-icon-shopping-cart-2" type="danger" plain @click="addToCart"></el-button>
    <div htmml></div>
  </div>
</template>
<style scoped>
.image-slot {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #cccccc;
}
.dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #f3d19e;
}
.text-container {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
}

.paragraph {
  font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Oxygen-Sans, Ubuntu, Cantarell, "Helvetica Neue", sans-serif;
  font-size: 16px;
  line-height: 1.75em;
  color: #333;
  margin-bottom: 1.5em;
  letter-spacing: 0.01em;
  text-indent: 1em;
  word-wrap: break-word;
  overflow-wrap: break-word;
}
</style>
<script>
import E from "wangeditor";
import hljs from "highlight.js";

export default {
  name: 'booksDetail',
  data() {
    return {
      colors: ['#99A9BF', '#F7BA2A', '#FF9900'],
      book: {},
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 15,  // 每页显示的个数
      reviewerId: '',
      asin: '',
      total: 0,
      fromVisible: false,
      fromVisible1: false,
      editor: null,
      reviewText: '',
      rules: {
        overall: [
          { required: true, message: '请输入评分', trigger: 'blur' },
        ],
        summary: [
          { required: true, message: '请输入总结', trigger: 'blur' },
        ],
      },
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      form: {},
      isStar:false,
      isBuy:false,
      starIcon:"el-icon-star-off"
    }
  },
  created() {
    let id = this.$route.query.id
    if (!id) {
      return
    }
    this.$request.get('/books/selectById/' + id).then(res => {
      this.book = res.data
      this.asin = this.book.asin
      this.load()
      if (this.user.interests) {
        let interests = this.user.interests.split(' ')
        if (interests.indexOf(this.book.id.toString()) !== -1) {
          this.isStar = true
          this.starIcon = "el-icon-star-on"
        }}
    })
  },
  methods: {
    load(pageNum) {  // 分页查询
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/reviews/selectByPage', {
        timeout:1000000,
        params: {
          asin: this.asin,
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          reviewerId: this.reviewerId,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    closeDialog() {
      // 销毁编辑器
      this.editor.destroy()
      this.editor = null
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
          this.form.reviewText = this.editor.txt.html()
          this.form.asin = this.asin
          this.$request({
            url: this.form.id ? '/reviews/update' : '/reviews/add',
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
    star(){
      // if
      // this.$message.success('收藏成功')
      if(this.isStar){
        this.$confirm('取消收藏,以后可能就找不到了, 是否继续?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }
        ).then(() => {
        this.user["interests"]=  this.user["interests"].split(" ").filter(item => item !== this.book.id.toString()).join(" ")
        this.isStar = false
        this.$request({
          url: this.user.id ? '/user/update': '/user/add',
          method: this.user.id ? 'PUT' : 'POST',
          data: this.user
        }).then(res => {
          if (res.code === '200') {  // 表示成功保存
            this.$message.success('取消收藏')
            localStorage.setItem('honey-user', JSON.stringify(this.user))
            this.starIcon = 'el-icon-star-off'
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })}).catch(() => {
          // this.$message({
          //   type: 'info',
          //   message: '未取消收藏'
          // });
        })
      }else{

        this.user["interests"]=  this.user["interests"]+=" "+this.book.id
        this.isStar = true
        this.$request({
          url: this.user.id ? '/user/update': '/user/add',
          method: this.user.id ? 'PUT' : 'POST',
          data: this.user
        }).then(res => {
          if (res.code === '200') {  // 表示成功保存
            this.$message.success('收藏成功')
            localStorage.setItem('honey-user', JSON.stringify(this.user))
            this.starIcon = 'el-icon-star-on'
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }

    },
    //添加到购物车
    addToCart(){
      if(!this.isStar){
        this.star()
      }
      this.form = {}
      this.form.name = this.book.title?this.book.title:this.book.categories.split(',')[0]
      this.form.goodsId = this.book.id
      this.form.goodsImurl = this.book.imurl
      this.form.money = this.book.price
      this.form.state = "未支付"
      this.form.category = this.book.categories
      this.$request({
        url: this.form.id ? '/orders/update': '/orders/add',
        method: this.form.id ? 'PUT' : 'POST',
        data: this.form
      }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('订单添加成功')
          this.load(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    }
  }
}
</script>
