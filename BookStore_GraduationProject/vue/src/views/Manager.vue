<template>
  <div>
    <el-container style="height: 100vh">
        <el-header>

          <i class="el-icon-search" style="font-size: 26px" @click="fromVisible = !fromVisible"></i>
          <el-breadcrumb separator-class="el-icon-arrow-right" style="margin-left: 20px">
            <el-breadcrumb-item :to="{ path: '/' }">主页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
          </el-breadcrumb>

          <div style="flex: 1; width: 0; display: flex; align-items: center; justify-content: flex-end">
            <i class="el-icon-service" style="font-size: 26px" @click=""></i>
            <i class="el-icon-quanping" style="font-size: 26px" @click="handleFull"></i>
            <el-dropdown placement="bottom">
              <div style="display: flex; align-items: center; cursor: default">
                <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="" style="width: 40px; height: 40px; border-radius: 50%; margin: 0 5px">
                <span>{{ user.name }}</span>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="$router.push('/person')">个人信息</el-dropdown-item>
                <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
                <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

        </el-header>
       <el-main style="padding: 0; border: none; margin: 0">
          <router-view @update:user="updateUser" />
        </el-main>
      <el-footer  width=100% style="min-height: 50px; background-color: #001529; position: relative ">
        <el-menu  mode="horizontal" router background-color="#001529" text-color="rgba(255, 255, 255, 0.65)"
                 active-text-color="#fff" style="border: none;display: flex;justify-content: space-evenly" :default-active="$route.path">
          <el-menu-item index="/home" style="width: 120px" >
            <i class="el-icon-s-home"></i>
            <span slot="title">主页</span>
          </el-menu-item>
          <el-menu-item index="/Recommender" style="width: 120px" >
            <i class="el-icon-reading"></i>
            <span slot="title">推荐</span>
          </el-menu-item>
          <el-submenu index="info" >
            <template slot="title">
              <i class="el-icon-menu"></i>
              <span>我的管理</span>
            </template>
            <el-menu-item index="/user" v-if="user.role === '管理员'"><i class="el-icon-user"></i>
              <span slot="title">用户管理</span></el-menu-item>
            <el-menu-item index="/books" v-if="user.role === '管理员'"><i class="el-icon-files"></i>
              <span slot="title">书籍管理</span></el-menu-item>
            <el-menu-item index="/reviews" v-if="user.role === '管理员'"><i class="el-icon-files"></i>
              <span slot="title">评论管理</span></el-menu-item>
            <el-menu-item index="/orders" v-if="user.role === '管理员'"><i class="el-icon-s-management"></i>
              <span slot="title">订单管理</span></el-menu-item>
            <el-menu-item index="/news" v-if="user.role === '管理员'"><i class="el-icon-coordinate"></i>
              <span slot="title">游戏信息</span></el-menu-item>
            <el-menu-item index="/notice" v-if="user.role === '管理员'"><i class="el-icon-data-board"></i>
              <span slot="title">系统公告</span></el-menu-item>
            <el-menu-item index="/logs" v-if="user.role === '管理员'"><i class="el-icon-coin"></i>
              <span slot="title">系统日志</span></el-menu-item>
            <el-menu-item index="/charts" v-if="user.role === '管理员'"><i class="el-icon-data-line"></i>
              <span slot="title">数据统计</span></el-menu-item>
            <el-menu-item index="/interest"><i class="el-icon-star-on"></i>
              <span slot="title">感兴趣</span></el-menu-item>
            <el-menu-item index="/cart"><i class="el-icon-s-order"></i>
              <span slot="title">我的订单</span></el-menu-item>
          </el-submenu>
        </el-menu>

      </el-footer>
    </el-container>

    <el-dialog :visible.sync="fromVisible" width="100%"  >
      <el-container  style="height: 80vh; border: 1px solid #eee">
        <el-header style="text-align: right;height:100px">
<!--/*          <div style="display: flex; justify-content: center"><h4 style="color: #333333; height: 2px">书籍小店</h4></div>*/-->
          <hr style=" height: 2px">
          <el-input style="width: 200px" placeholder="输入书名查询" v-model="title"></el-input>
          <el-button-group>
            <el-button type="primary" :loading=loading style="margin-left: 10px" icon="el-icon-search" @click="load(1)"></el-button>
            <el-button type="info" @click="reset" icon="el-icon-delete"></el-button>
          </el-button-group>
        </el-header>
        <el-main>

          <el-table empty-text="" :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" >
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
                <el-button @click="showContent(scope.row)" size="large">更多</el-button>
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
    <el-dialog title="内容" :visible.sync="fromVisible1" width="90%">
      <!--      //游戏详情页-->

      <el-card class="w-e-text">
        <h4>{{ card }} </h4>
        <el-divider content-position="right">{{time}}</el-divider>
        <div style="background-color: #333;
            color: #fff;
            padding: 20px;
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.5);">{{detail}}</div>
        <el-image :src="imurl" fit= "scale-down" :preview-src-list="[imurl]">
          <div slot="placeholder" class="image-slot">
            加载中<span class="dot">...</span>
          </div>
        </el-image>
        <div v-html="description"></div>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="fromVisible1 = false">返 回</el-button>
        <el-button type="info" @click.native="$router.push('/booksDetail?id=' + id);fromVisible1=!fromVisible1;fromVisible=!fromVisible">评论</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import user from "@/views/manager/User";

export default {
  name: 'HomeView',
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      username: '',
      title: '',
      total: 0,
      fromVisible: false,
      fromVisible1: false,
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
      loading: false,
      bid:'',
      categories:''
    }
  },
  created() {
    this.load()
  },
  mounted() {   // 页面加载完成之后触发
    if (!this.user.id) {   // 当前的浏览器没有用户信息
      this.$router.push('/login')
    }
  },
  methods: {
    updateUser(user) {   // 获取子组件传过来的数据  更新当前页面的数据
      this.user = JSON.parse(JSON.stringify(user))  // 让父级的对象跟子级的对象毫无关联
    },
    logout() {
      localStorage.removeItem('honey-user')  // 清除当前的token和用户数据
      this.$router.push('/login')
    },
    handleFull() {
      document.documentElement.requestFullscreen()
    },
    showContent(row) {
      this.description = row.description
      this.card = row.title
      this.imurl = row.imurl
      this.detail = row.categories
      this.time = row.time
      this.author = row.author
      this.fromVisible1 = true
      this.id = row.id
      // console.log(row)
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
          title: this.title,
          categories: this.categories
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

<style>
.el-menu--inline {
  background-color: #000c17 !important;
}
.el-menu--inline .el-menu-item {
  background-color: #000c17 !important;
  padding-left: 49px !important;
}
.el-menu-item:hover, .el-submenu__title:hover {
  color: #fff !important;
}
.el-submenu__title:hover i {
  color: #fff !important;
}
.el-menu-item:hover i {
  color: #fff !important;
}
.el-menu-item.is-active {
  background-color: #1890ff !important;
  border-radius: 5px !important;
  width: calc(100% - 8px);
  margin-left: 4px;
}
.el-menu-item.is-active i, .el-menu-item.is-active .el-tooltip{
  margin-left: -4px;
}
.el-menu-item {
  height: 40px !important;
  line-height: 40px !important;
}
.el-submenu__title {
  height: 40px !important;
  line-height: 40px !important;
}
.el-submenu .el-menu-item {
  min-width: 0 !important;
}
.el-menu--inline .el-menu-item.is-active {
  padding-left: 45px !important;
}
/*.el-submenu__icon-arrow {*/
/*  margin-top: -5px;*/
/*}*/

.el-aside {
  transition: width .3s;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
}
.logo-title {
  margin-left: 5px;
  font-size: 20px;
  transition: all .3s;   /* 0.3s */
}
.el-header {
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  display: flex;
  align-items: center;
}
</style>
