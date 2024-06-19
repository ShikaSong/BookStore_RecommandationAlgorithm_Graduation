<template>
  <div>
    <el-input style="width: 200px" placeholder="查询名称" v-model="name"></el-input>
    <el-button type="primary" style="margin-left: 10px" @click="load(1)">查询</el-button>
    <el-button type="info" @click="reset">重置</el-button>

    <el-table :data="tableData" stripe :header-cell-style="{ backgroundColor: 'aliceblue', color: '#666' }">
<!--      <el-table-column prop="id" label="序号" width="70" align="center"></el-table-column>-->
      <el-table-column prop="no" label="订单编号"></el-table-column>
      <el-table-column  label=""  align="center">
        <template slot-scope="scope">
          <el-image :src="scope.row.goodsImurl" fit= "scale-down" :preview-src-list="[scope.row.goodsImurl]">
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </el-image>
          <div style="font-size: 15px; font-weight: bolder">{{scope.row.name}}</div>
        </template>
      </el-table-column>
      <el-table-column prop="money" label="订单金额" show-overflow-tooltip>
        <template slot-scope="scope">
          <span style="font-size: 15px; font-weight: bold;color: #dd4141">￥{{scope.row.money}}</span>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="创建时间"></el-table-column>
      <el-table-column prop="payTime" align="center" :formatter="defaultValueFormatter" label="付款时间"></el-table-column>
      <el-table-column prop="state" label="订单状态"></el-table-column>
      <el-table-column fixed="right" label="操作" align="center" width="150">
        <template v-slot="scope">
          <el-button v-if="scope.row.state === '未支付'" size="mini" type="primary" plain @click="buy(scope.row)">支付</el-button>
          <el-button v-if="scope.row.state === '未支付'" size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          <el-button v-if="scope.row.state === '已支付'" size="mini" type="success" plain @click="">查看图书</el-button>
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

  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: "Cart",
  data() {
    return {
      name: '',
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: false,
      selectedRow: [],
      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      defaultValueFormatter: (row, column) => {
        if (row.payTime) {
          return row.payTime
        } else {
          return '---'
        }
      }
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    reset() {
      this.name = ''
      this.load()
    },
    load(pageNum) {
      if (pageNum)  this.pageNum = pageNum
      this.loading = true
      this.$request.get('/orders/selectByPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
          userid: this.user.id,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
        this.loading = false
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    del(id) {
      this.$confirm('此操作将永久删除该订单, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
      ).then(() => {
        this.$request.delete('/orders/delete/'+id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    buy(row) {
      window.open(process.env.VUE_APP_BASEURL +'/alipay/pay?id='+row.id)
    }
  },

}
</script>

<style scoped>

</style>