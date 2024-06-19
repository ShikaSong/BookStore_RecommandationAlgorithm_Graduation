<template style="height: 100%; background-color: #0d1a2b">
  <div style="">
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
          <div ref="scrollBox" style=" color: #6a5c4d;display: flex; justify-content: stretch; position: absolute; bottom: 2%;left: 4%;height: 200px;width: 60%;overflow: auto;z-index: 99">
             <b>{{i.description}} </b>
          </div>
          <div style="display: flex; justify-content: stretch; position: absolute; bottom: 2%;left: 4%;z-index: 999">
            <div style="font-size: 25px; font-weight: bold; color: #dd4141;display: inline; margin-left: 2px"><i class="el-icon-price-tag"></i> ￥{{i.price}}</div>
<!--            <div style="font-size: 25px; font-weight: bold; color: #a7900e">销售排名{{i.salesrank}}</div>-->
<!--            <div style="display: flex; align-items: flex-start; ">-->
<!--              <el-tag size="mini" style = "display: inline " type="success">官方直营</el-tag>-->
<!--              <el-tag size="mini" style = "display: inline " type="info">原版保障</el-tag>-->
<!--            </div>-->
<!--            <el-tag size="mini" style = "display: inline " type="warning">盗版包赔</el-tag>-->
            <el-button-group>
              <el-button type="danger" @click.native="$router.push('/booksDetail?id=' + i.id)" size="mini" icon="el-icon-chat-line-round"  @click="">详情页</el-button>
<!--              <el-button type="danger" size="mini" icon="el-icon-shopping-cart-2" @click="addToCart">立即购买</el-button>-->
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
  name: "Recommender",
  data () {
    return {

      user: JSON.parse(localStorage.getItem('honey-user') || '{}'),
      count: 0,
      books_recommendation: [],
      book_list: []
    }
  },created() {
      this.books_recommendation =  this.user["recommendation"].split(",")
  },mounted() {
    setTimeout(() => {
      this.scrollToBottomSmoothly();
    }, 200); // 延迟一段时间（例如：200毫秒）后开始滚动，以便让页面元素渲染完成
  },
  methods: {
    load () {
      if(this.count < 10){
        // console.log( this.book_list)
        setTimeout(() => {
          this.load_book(this.books_recommendation[this.count])
          this.count += 1
          this.$nextTick(() => {
            this.$refs.scrollBox.forEach((scrollBox, index) => {
              setTimeout(() => {
                this.scrollToBottomSmoothly(scrollBox);
              }, 200 + (index * 100)); // 每个盒子之间的延迟时间可以根据需要调整
            });
          });
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
    scrollToBottomSmoothly(scrollBoxElement) {
      this.$nextTick(() => {
        const targetScrollTop = scrollBoxElement.scrollHeight - scrollBoxElement.clientHeight;

        const smoothScroll = () => {
          if (scrollBoxElement.scrollTop < targetScrollTop) {
            requestAnimationFrame(smoothScroll);
            scrollBoxElement.scrollTop += Math.ceil((targetScrollTop - scrollBoxElement.scrollTop) / 500);
          }
        };

        smoothScroll();
      });
    },
  },
}
</script>

<style scoped>
.book-wrapper{
  position: relative;
  width: 480px;
  height: 800px;
  display: flex;
  flex-wrap: nowrap;
  justify-content: center;
  flex-direction: column;
  border: #d0c6b6 1px solid;
  /*overflow: auto;*/
}
</style>