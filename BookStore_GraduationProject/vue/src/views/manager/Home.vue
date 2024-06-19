<template>
  <div>
    <div style="box-shadow: 0 0 10px rgba(0,0,0,.1); padding: 10px 20px; border-radius: 5px; margin-bottom: 10px">
      你好啊，{{ user.name }}，祝你拥有快乐的每一天！
    </div>
    <div style="display: flex; margin: 15px 0; ">
      <el-card style="width: 50%; margin-right: 10px; height: 250px; overflow: auto">
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold">系统公告</div>
        <el-timeline style="padding: 0">
          <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time" placement="top">
            <el-card>
              <h4>{{ item.title }}</h4>
              <div v-html="item.content"></div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <el-card style="width: 50%;height: 250px; overflow: auto">
        <div style="margin-bottom: 15px; font-size: 20px; font-weight: bold;">系统公告</div>
        <el-collapse v-model="activeName" accordion>
          <el-collapse-item  v-for="(item, index) in notices" :key="item.id" :name="index + ''">
            <template slot="title">
              <div style="display: flex; align-items: center; width: 100%">
                <h4 style="flex: 1">{{ item.title }}</h4>
                <div style="width: 150px; color: #888">{{ item.time }}</div>
              </div>
            </template>
            <div v-html="item.content"></div>
          </el-collapse-item>
        </el-collapse>
      </el-card>
    </div>
    <div style="display: flex">
      <el-card style="width: 100%;">
        <div slot="header" class="clearfix">
          <span>基于推荐算法的网上图书销售系统的设计与实现正在设计实现中</span>
        </div>
        <div>
          <div style="margin-top: 20px">
            <div style="margin: 10px 0"><strong>主题</strong></div>
            <el-tag
                v-for="tag in dynamicTags"
                :key="tag.name"
                closable
                :type="tag.type"
                :disable-transitions="false"
                @click="handleClick(tag)"
                @close="handleClose(tag)"
                style="height: 30px"
                :effect="tag.effect"
            >
              {{tag.name}}
            </el-tag>
            <el-input
                class="input-new-tag"
                v-if="inputVisible"
                v-model="inputValue"
                ref="saveTagInput"
                size="small"
                @keyup.enter.native="handleInputConfirm"
                @blur="handleInputConfirm"
            >
            </el-input>
            <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 自定义</el-button>
          </div>
        </div>
      </el-card>
    </div>
    <el-table show-header=false style="" fit=false size="mini" :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }" >
      <el-table-column style=""   align="center">
        <template slot-scope="scope">
          <el-image :src="scope.row.imurl" fit= "scale-down" :preview-src-list="[scope.row.imurl]">
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </el-image>
<!--          <div style="font-size: 15px; font-weight: bolder">{{scope.row.title}}</div>-->
        </template>
      </el-table-column>
      <el-table-column :label="categories">
        <template slot-scope="scope">

          <div style="font-size: 10px; font-weight: bolder">{{scope.row.categories.split(',')[0]}}</div>
        </template>

      </el-table-column>
      <el-table-column prop="description"  fixed="right">
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
    <div style="margin: 10px 0">
      <el-pagination
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total">
      </el-pagination>
    </div>
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
        <el-button type="info" @click.native="$router.push('/booksDetail?id=' + id)">评论</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      notices: [],
      activeName: '0',
      categories:'marriage',
      tableData:[],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      form: {},
      imurl: '',
      title: '',
      dynamicTags: [{ name: 'marriage', type: '',effect:'dark' },
        { name: 'children', type: 'success',effect:'' },
        { name: 'exercise', type: 'info',effect:'' },
        { name: 'computer', type: 'warning',effect:'' },
        { name: 'science', type: 'danger',effect:'' }],
      inputVisible: false,
      inputValue: '',
      activeTag: 0,
      fromVisible1: false,
      loading:false,
      // asin:'',
      card: '',
      detail:'',
      author:'',
      description:'',
      time:'',
      id:''
    }
  },
  created() {
    this.loadNotice()
    this.load()
  },
  methods: {
    loadNotice() {
      this.$request.get('/notice/selectUserData').then(res => {
        this.notices = res.data
      })
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
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },
    handleClick(tag) {
      this.categories = tag.name;
      this.dynamicTags[this.activeTag].effect = "light";
      this.activeTag = this.dynamicTags.indexOf(tag);
      this.dynamicTags[this.activeTag].effect = "dark";
      this.load(this.pageNum)

    },
    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        let tag = { name: '', type: '',effect:'' }
        tag.name = inputValue;
        tag.type = this.dynamicTags[this.activeTag].type;
        this.dynamicTags.push(tag);
      }
      this.inputVisible = false;
      this.inputValue = '';
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
    },
  }
}
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>