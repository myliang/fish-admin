<template>
  <fish-layout sider="l" class="main-layout">
    <nav slot="header">
      <div class="logo">
        F <i class="fa fa-flash"/> sh
      </div>
      <ul class="nav-right">
        <li>
          <fish-dropdown align="bottom-right">
            <a slot="title">{{userName}}<i class="fa fa-angle-down" style="margin-left: 10px;"></i></a>
            <fish-option index="settings" content="Settings"></fish-option>
            <fish-option index="exit" content="Exit" @click="exit"></fish-option>
          </fish-dropdown>
        </li>
      </ul>
    </nav>
    <div slot="sider">
      <fish-menu mode="inline" v-if="menus" :defaultActive="defaultActiveIndex" @change="menuChangeHandler">
        <template v-for="menu in menus">
          <fish-submenu :index="menu.key" mode="inline" :key="menu.key" v-if="menu.children">
            <template slot="title">{{ menu.title }}</template>
            <fish-option :index="`${menu.key}-${child.key}`" :content="child.title" v-for="child in menu.children"
                         :key="child.key" @click="nextRouter(child.url)"></fish-option>
          </fish-submenu>
          <fish-option :index="menu.key" :content="menu.title" v-else @click="nextRouter(menu.url)"></fish-option>
        </template>
      </fish-menu>
    </div>
    <div slot="content">
      <slot></slot>
    </div>
    <div slot="footer">
      <p>copyright@2017 myliang</p>
    </div>
  </fish-layout>
</template>
<script>
  import request from '../request'

  window.$currentUser = {
    name: '',
    menus: [],
    permissions: {},
    hasPermissions (k, v) {
      if (this.permissions[k] !== undefined) {
        for (let index of this.permissions[k]) {
          if (this.permissions[k][index] === v) return true
        }
      }
      return false
    }
  }
  export default {
    name: 'main-layout',
    props: {
    },
    data () {
      return {
        userName: null,
        menus: null,
        defaultActiveIndex: sessionStorage.getItem('menuActiveIndex') === 'undefined' ? undefined : sessionStorage.getItem('menuActiveIndex')
      }
    },
    mounted () {
      request('/api/current/user').then(({data}) => {
        const {menus, permissions, user} = data
        window.$currentUser.name = this.userName = user.userName
        window.$currentUser.menus = this.menus = menus
        window.$currentUser.permissions = permissions
      })
    },
    methods: {
      exit () {
        sessionStorage.removeItem('token')
        this.$router.replace('/login')
      },
      nextRouter (url) {
        this.$router.push(url)
      },
      menuChangeHandler (index) {
        sessionStorage.setItem('menuActiveIndex', index)
      }
    }
  }
</script>
<style>
  .main-layout {
    color: #666;
  }
  .main-layout > .header {
    box-shadow: 1px 2px 3px rgba(0, 0, 0, 0.05);
    /*border-bottom: 1px solid #e9e9e9;*/
    vertical-align: middle;
    line-height: 60px;
    height: 60px;
    margin-bottom: 1px;
  }
  .main-layout > .has-sider > .content {
    padding: 1em;
  }
  .fish.layout > .sider {
    border-right: 3px solid #e9e9e9;
    font-weight: bold;
  }
  .main-layout .nav-right {
    float: right;
    margin: 0;
    padding: 0;
    list-style: none;
    margin-right: 1em;
  }
  .main-layout .nav-right li {
    padding-left: 1em;
    float: left;
  }
  .main-layout .sider .fish.menu .item, .main-layout .sider .fish.menu.inline .submenu.item.active {
    font-weight: bold;
    color: #6a6c6f;
    margin-right: -3px;
  }

  .logo {
    padding: 0 1em;
    font-weight: bold;
    font-size: 1.8rem;
    color: #566374;
    float: left;
    /*font-style: oblique;*/
    width: 200px;
    text-align: center;
    border-right: 3px solid #e9e9e9;
    /*border-bottom: 1px solid #e9e9e9;*/
    background: #f7f9fa;
    /*box-shadow: 1px 2px 3px rgba(0, 0, 0, 0.1);*/
  }

  .form-search {
    margin-bottom: .8em;
  }
</style>
