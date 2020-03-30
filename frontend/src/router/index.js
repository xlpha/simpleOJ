import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/problems',
    name: 'Problems',
    component: () => import('../views/ProblemList')
  },
  {
    path: "/problems/:id",
    name: "ProblemDetail",
    component: ()=>import("../views/ProblemDetail")
  },
  {
    path: "/submission/:id",
    name: "SubmissionDetail",
    component: ()=>import("../views/SubmissionDetail")
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
