/* eslint-disable import/first */
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

const authLogin = () => {
  let token = sessionStorage.getItem('token')
  return token !== undefined && token !== null
}

import Login from '../components/Login.vue'
import Index from '../components/Index.vue'
import Settings from '../components/current/Settings.vue'
import UserIndex from '../components/user/UserIndex.vue'
import RoleIndex from '../components/role/RoleIndex.vue'

const router = new Router({
  mode: 'history',
  base: '/_fish/',
  routes: [
    {path: '/', component: Index, meta: {auth: true}},
    {path: '/settings', component: Settings, meta: {auth: true}},
    {path: '/users', component: UserIndex, meta: {auth: true}},
    {path: '/roles', component: RoleIndex, meta: {auth: true}},
    {path: '/login', component: Login, meta: {auth: false}}
  ]
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.auth)) {
    if (!authLogin()) {
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
