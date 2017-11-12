<template>
  <div>
    <div class="sign_in">
      <div class="logo">
        F <i class="fa fa-flash"/> sh
      </div>
      <h3>Login</h3>
      <fish-form @submit.native.prevent="submit">
        <fish-field>
          <fish-input :iconLeft="true" icon="fa fa-user" hint="user name" v-model="userName"></fish-input>
        </fish-field>
        <fish-field>
          <fish-input :iconLeft="true" icon="fa fa-lock" hint="password" type="password" v-model="password"></fish-input>
        </fish-field>
        <fish-button type="primary" :loading="loading" @click="submit">Submit</fish-button>
      </fish-form>
      <fish-message type="warning" v-if="error" style="margin-top: 10px;">Error, Please try again</fish-message>
    </div>
  </div>
</template>
<script>
  import 'whatwg-fetch'
  export default {
    data () {
      return {
        logout: false,
        error: false,
        loading: false,
        userName: '',
        password: ''
      }
    },
    mounted () {
      // console.log('mounted')
    },
    methods: {
      submit () {
        if (!/^\s*%/.test(this.userName) && !/^\s*%/.test(this.password)) {
          let body = JSON.stringify({'username': this.userName, 'password': this.password})
          this.loading = true
          fetch(`${process.env.API_ROOT}/login`, {
            method: 'POST',
            mode: 'cors',
            body: body
          }).then((res) => {
            // console.log(res)
            if (res.status === 200) {
              res.json().then((data) => {
                // console.log(data.token, this.userName)
                sessionStorage.setItem('token', data.token)
                // sessionStorage.setItem('user_name', this.userName)
                // console.log(this.$router)
                this.$router.replace('/')
              })
            } else {
              this.error = true
            }
            this.loading = false
            // this.$emit('success')
          }).catch((e) => {
            this.error = true
            this.loading = false
          })
        }
      }
    }
  }
</script>
<style>
  .sign_in {
    position: relative;
    margin: 100px auto;
    width: 350px;
    left: 50px;
    /*border-top: solid 1px #EBEBEB;*/
  }
  .sign_in .logo {
    display: block;
    width: 120px;
    position: absolute;
    top: 42px;
    left: -150px;
    background-color: #f7f9fa;
    text-align: center;
    vertical-align: middle;
    color: #566374;
    font-weight: bold;
    padding: 20px 0;
    font-size: 2rem;
    border-right: 3px solid #e9e9e9;
  }
  .sign_in h3 {
    font-size: 1.2em;
    font-weight: 700;
    color: #39464E;
    margin-top: 10px;
    margin-bottom: 12px;
  }
</style>
