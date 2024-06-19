<template style="height: 100%; background-color: #0d1a2b">
  <div style="position: relative">
    <ul class="infinite-list" v-infinite-scroll="load" style="overflow:auto; list-style-type: none;padding: 0; border: none; margin: 0;height: 95vh;background-color: #333333; display: flex; flex-wrap: wrap;justify-content:space-evenly">
      <li v-for="i in book_list" class="infinite-list-item">
        <div class="book-wrapper">
          <!--          <h4 style="overflow:auto; list-style-type: none;padding: 0; border: none; margin: 0; color: #cccccc">{{ i.title }}</h4>-->
          <el-image style="" :src="i.imurl" fit= "contain" :preview-src-list="[i.imurl]">
            <div slot="placeholder" class="image-slot">
              加载中<span class="dot">...</span>
            </div>
          </el-image>
          <!--          <el-button type="warning" size="large" icon="el-icon-star-on" circle></el-button>-->
          <!--          <i style="color: #f3d19e;font-size: 25px;" class="el-icon-star-on"></i>-->

          <!--          <el-button type="success" size="large" icon="el-icon-check" circle></el-button>-->
          <!--          <el-button type="primary" size="large" icon="el-icon-edit" circle></el-button>-->
          <!--          <el-button type="danger" size="large" round>购买</el-button>-->
          <!--          <el-button type="info" icon="el-icon-chat-dot-round" plain>评论</el-button>-->
          <!--          <el-tag-->
          <!--              v-for="tag in i.categories.split(',')"-->
          <!--              :key="tag.name"-->
          <!--              >-->
          <!--            {{tag}}-->
          <!--          </el-tag>-->
          <el-button style="position: absolute; top: 4%;left: 4%; z-index: 999;opacity: 0.8" icon="el-icon-delete" type="danger" circle plain @click="del(i)"></el-button>
          <div style="display: flex; justify-content: stretch; position: absolute; bottom: 2%;left: 4%">
            <div style="font-size: 25px; font-weight: bold; color: #dd4141;display: inline; margin-left: 2px"><i class="el-icon-price-tag"></i> ￥{{i.price}}</div>
            <!--            <div style="font-size: 25px; font-weight: bold; color: #a7900e">销售排名{{i.salesrank}}</div>-->
            <!--            <div style="display: flex; align-items: flex-start; ">-->
            <!--              <el-tag size="mini" style = "display: inline " type="success">官方直营</el-tag>-->
            <!--              <el-tag size="mini" style = "display: inline " type="info">原版保障</el-tag>-->
            <!--            </div>-->
            <!--            <el-tag size="mini" style = "display: inline " type="warning">盗版包赔</el-tag>-->
            <el-button-group>
              <el-button type="danger" size="mini" @click.native="$router.push('/booksDetail?id=' + i.id)" icon="el-icon-chat-line-round"  @click="">评论</el-button>
              <el-button type="danger" size="mini" icon="el-icon-shopping-cart-2" @click="">立即购买</el-button>
            </el-button-group>
          </div>
          <!--         <p style="color: #cccccc">{{ i.description}}</p>-->

        </div>
      </li>
    </ul>
  </div>

</template>

<script>
export default {
  name: "Interest",
  data () {
    return {

      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      count: 0,
      books_interest: [],
      book_list: []
    }
  },created() {
    this.books_interest =  this.user["interests"].split(" ")
  },
  methods: {
    load () {
      if(this.count < 20){
        // console.log( this.user)
        setTimeout(() => {
          this.load_book(this.books_interest[this.count])
          this.count += 1
          console.log(this.count)
        }, 1000)
      }else{
        this.$emit('load-end')
      }
    },
    load_book(id){
      if (!id) {
        return
      }
      this.$request.get('/books/selectById/' + id).then(res => {
        this.book_list.push(res.data)
      })
    },
    del(i){
      let index = this.book_list.indexOf(i)
      this.books_interest.splice(index, 1);
      this.book_list.splice(index, 1);
      this.user["interests"] = this.books_interest.join(" ")
      this.$request({
        url: this.user.id ? '/user/update': '/user/add',
        method: this.user.id ? 'PUT' : 'POST',
        data: this.user
      }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('保存成功')
          localStorage.setItem('honey-user', JSON.stringify(this.user))
          this.count -= 1
          // this.load()
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    }
  }
}
</script>

<style scoped>
.book-wrapper{
  position: relative;
  width: 150px;
  height: 200px;
  display: flex;
  flex-wrap: nowrap;
  justify-content: center;
  flex-direction: column;
  border: #d0c6b6 1px solid;
  /*overflow: auto;*/
}
</style>