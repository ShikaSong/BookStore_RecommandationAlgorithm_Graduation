<template>
  <div style="height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #0d1a2b">

    <template>
      <div>
        <div style="display: flex; margin-top: 0; flex-direction: column;">
          <transition name="el-zoom-in-center">
            <div v-show="show2" class="transition-box">
              <div class="figcaption">
                <i class="el-icon-reading"></i>
                <h1>90000+英文原版图书</h1>
                <h2>90000+ English Books</h2>
              </div>
              <img id="image1" src="@/assets/book_shelf.png"/>

            </div>
          </transition>

          <transition name="el-zoom-in-top">
            <div v-show="show2" class="transition-box">
              <div class="figcaption"><i class="el-icon-edit"></i><h1>10,000,000+图书评论</h1>
                <h2>10,000,000+ Book reviews</h2></div>
              <img id="image2" src="@/assets/photo-book-corner.jpg"/>
            </div>
          </transition>

          <transition name="el-zoom-in-bottom">
            <div v-show="show2" class="transition-box">
              <div class="figcaption"><i class="el-icon-data-line"></i><h1 >世界排名第一的图书推荐算法</h1>
                <h2>The state of art of recommender algorithm</h2></div>
              <img id="image3" src="@/assets/书店.png"/>
            </div>
          </transition>
          <transition name="el-fade-in-linear">
            <div v-show="!show2" class="transition-box">
              <div class="figcaption">
                <h1>图书推荐小店</h1>
                <el-button  @click="show2 = !show2" type="primary">小店介绍</el-button>
                <el-button  @click.native="$router.push('/login')" type="info" icon="el-icon-user">登录注册</el-button>
              </div>
          <img src="@/assets/a_man_on_the_chair.jpg"/>
            </div>
          </transition>
          <transition name="el-fade-in-linear">
            <div v-show="!show2" class="transition-box">
              <div class="figcaption">
                <el-button type="text" @click="fromVisible = !fromVisible" icon="el-icon-search">或者书库里查询一下吧</el-button>
              </div>
              <img src="@/assets/passion_lead_us_here.jpg"/>
            </div>
            </transition>
        </div>
      </div>
    </template>


    <el-dialog :visible.sync="fromVisible" width="100%"  >
      <el-container  style="height: 80vh; border: 1px solid #eee">
        <el-header style="text-align: right;height:90px">
          <div style="display: flex; justify-content: center"><h4 style="color: #333333; height: 2px">书籍小店</h4></div>
          <hr style=" height: 2px">
          <el-input style="width: 200px" placeholder="输入书名查询" v-model="title"></el-input>
          <el-button-group>
          <el-button type="primary" :loading=loading style="margin-left: 10px" icon="el-icon-search" @click="load(1)"></el-button>
          <el-button type="info" @click="reset" icon="el-icon-delete"></el-button>
          </el-button-group>
        </el-header>
        <el-main>

          <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" >
            <el-table-column  label=""  align="center">
              <template slot-scope="scope">
                <el-image :src="scope.row.imurl" fit= "scale-down" :preview-src-list="[scope.row.imurl]">
                  <div slot="placeholder" class="image-slot">
                    加载中<span class="dot">...</span>
                  </div>
                </el-image>
                <div style="font-size: 15px; font-weight: bolder">{{scope.row.title}}</div>
              </template>
            </el-table-column>
            <el-table-column label="图书">
              <template slot-scope="scope">

                <div style="font-size: 10px; font-weight: bolder">{{scope.row.categories.split(',')[0]}}</div>
              </template>

            </el-table-column>
            <el-table-column prop="description" label="简介" fixed="right">
              <template v-slot="scope">
                <div style="font-size: 25px; font-weight: bold; color: #dd4141">￥{{scope.row.price}}</div>
                <div style="display: flex; align-items: flex-start; ">
                  <el-tag size="mini" style = "display: inline " type="success">官方直营</el-tag>
                  <el-tag size="mini" style = "display: inline " type="info">原版保障</el-tag>
                </div>
                <el-tag size="mini" style = "display: inline " type="warning">盗版包赔</el-tag>
                <el-button @click="showContent(scope.row)" size="large">显示详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <template>
            <el-skeleton :loading="loading"/>
          </template>
        </el-main>
        <el-footer>
          <div style="margin: 10px 0">
            <el-pagination
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total">
            </el-pagination>
          </div>
        </el-footer>
      </el-container>

    </el-dialog>



    <div class = "floating-button-container" >
      <div class = "floating_button" v-show="show2">
        <el-button type="success" @click="fromVisible = !fromVisible" icon="el-icon-search">查询</el-button>
        <el-button type="info"  @click.native="$router.push('/login')" icon="el-icon-user">登录</el-button>
        <el-button  @click="show2 = !show2" type="primary" icon = "el-icon-right">返回</el-button>

      </div>

    </div>

    <el-dialog title="内容" :visible.sync="fromVisible1" width="90%">

      <el-card class="w-e-text">
        <h4>{{ card }} </h4>
        <el-divider content-position="right">{{time}}</el-divider>
        <div style="background-color: #333;
            color: #fff;
            padding: 20px;
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.5);"><el-tag
            v-for="tag in detail.split(',')"
            :key="tag.name"
            closable
        >
          {{tag}}
        </el-tag></div>
        <el-image :src="imurl" fit= "scale-down" :preview-src-list="[imurl]">
          <div slot="placeholder" class="image-slot">
            加载中<span class="dot">...</span>
          </div>
        </el-image>
        <div v-html="description"></div>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="fromVisible1 = false">返 回</el-button>
        <el-button type="info" @click.native="$router.push('/login')">登录查看更多</el-button>
      </div>
    </el-dialog>



  </div>
</template>
<style>

/*.floating-navigation-container{*/
/*  position: fixed;*/
/*  top: 10px;*/
/*  right: 10px;*/
/*  z-index: 9999;*/
/*}*/
.figcaption{
  position: absolute;
  top: 100px;
  left: 50px;
  color: #fff;
  font-size: 18px;
}
img {
  width: 100%;
}
.transition-box {
  margin-bottom: 0;
  /*width: 200px;*/
  /*height: 100px;*/
  position: relative;
  border-radius: 4px;
  background-color: #010b15;
  text-align: center;
  color: #fff;
  padding: 40px 20px;
  box-sizing: border-box;
  margin-right: 20px;
}
.floating-button-container{
  position: fixed;
  bottom: 80px;  /* 距离底部的距离 */
  right: 1px;   /* 距离右边的距离 */
  width: 100px;   /* 按钮宽度 */
  height: 50px;   /* 按钮高度 */
  z-index: 9999;
}
.floating_button{
  position: absolute;
  top: 30%;
  left: 70%;
  transform: translate(-50%,-50%);
  color: #FFFFFF; /* 白色文本 */
  border: none; /* 无边框 */
  padding: 10px; /* 内边距 */
  font-size: 16px; /* 字体大小 */
}
</style>
<script>
import E from "wangeditor"
import hljs from 'highlight.js'
import 'element-ui/lib/theme-chalk/base.css';
export default {
  name: "News",
  data() {
    return {
      show2:false,
      activeIndex: '1',
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      username: '',
      title: '',
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
        ]
      },
      ids: [],
      editor: null,
      description: '',
      imurl: '',
      card: '',
      detail:'',
      author:'',
      time:'',
      fromVisible1: false,
      loading: false,
    }
  },
  created() {

    this.load()
  },
  methods: {
    showContent(row) {
      this.description = row.description
      this.card = row.title
      this.imurl = row.imurl
      this.detail = row.categories
      this.time = row.time
      this.author = row.author
      this.fromVisible1 = true
    },
    reset() {
      this.title = ''
      this.load()
    },
    load(pageNum) {  // 分页查询
      this.loading=true
      if (pageNum)  this.pageNum = pageNum
      this.$request.get('/books/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.title

        }
      }).then(res => {
        this.loading=false
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