import Vue from 'vue'
import VueRouter from 'vue-router'

// 解决导航栏或者底部导航tabBar中的vue-router在3.0版本以上频繁点击菜单报错的问题。
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/home',  // 重定向到主页
    children: [
      { path: '403', name: 'Auth', meta: { name: '无权限' }, component: () => import('../views/manager/Auth') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'person', name: 'Person', meta: { name: '个人信息' }, component: () => import('../views/manager/Person') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'news', name: 'News', meta: { name: '新闻信息' }, component: () => import('../views/manager/News') },
      { path: 'newsDetail', name: 'NewsDetail', meta: { name: '新闻详情' }, component: () => import('../views/manager/NewsDetail') },
      { path: 'booksDetail', name: 'BooksDetail', meta: { name: '书籍详情' }, component: () => import('../views/manager/BooksDetail') },
      { path: 'notice', name: 'Notice', meta: { name: '系统公告' }, component: () => import('../views/manager/Notice') },
      { path: 'logs', name: 'Logs', meta: { name: '系统日志' }, component: () => import('../views/manager/Logs') },
      { path: 'charts', name: 'Charts', meta: { name: '数据统计' }, component: () => import('../views/manager/Charts') },
      { path: 'orders', name: 'Orders', meta: { name: '订单管理' }, component: () => import('../views/manager/Orders') },
      { path: 'books', name: 'Books', meta: { name: '书籍管理' }, component: () => import('../views/manager/Books') },
      { path: 'recommender', name: 'Recommender', meta: { name: '推荐' }, component: () => import('../views/manager/Recommender') },
      { path: 'reviews', name: 'Reviews', meta: { name: '评论' }, component: () => import('../views/manager/Reviews') },
      { path: 'interest', name: 'Interest', meta: { name: '感兴趣' }, component: () => import('../views/manager/Interest') },
      { path: 'cart', name: 'Cart', meta: { name: '购物车' }, component: () => import('../views/manager/Cart') },]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '/games', name: 'games', meta: { name: '游戏' }, component: () => import('../views/Game') },
  { path: '/book', name: 'BOOK', meta: { name: '书籍' }, component: () => import('../views/BOOK') },
  { path: '/gamesDetail', name: 'gamesDetail', meta: { name: '游戏详情' }, component: () => import('../views/GamesDetail') },
  { path: '*', name: '404', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  // to 是到达的路由信息
  // from 是开源的路由信息
  // next 是帮助我们跳转路由的函数
  let adminPaths = ['/user','/books','/recommender', '/news', '/notice', '/logs', '/charts', '/newsDetail', '/reviews']
  let user = JSON.parse(localStorage.getItem('honey-user') || '{}')
  if(to.path.indexOf('render')>-1){
    //打印提示
    console.log('支付宝支付')
  }
   else if (user.role !== '管理员' && adminPaths.includes(to.path)) {
    // 如果当前登录的用户不是管理员，然后当前的到达的路径是管理员才有权限访问的路径，那这个时候我就让用户去到一个没有权限的页面，不让他访问实际的页面
    next('/403')
  } else {
    next()
  }
})

export default router
