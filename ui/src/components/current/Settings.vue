<template>
  <fish-tabs value="password">
    <fish-tab-pane label="Change Password" index="password">
      <fish-form ref="form" @submit.native.prevent="submitHandler" class="change-password">
        <fish-field label="Old password" name="oldPassword" :rules="[{required: true}]">
          <fish-input type="password" v-model="form.oldPassword"></fish-input>
        </fish-field>
        <fish-field label="New password" name="newPassword" :rules="[{required: true}]">
          <fish-input type="password" v-model="form.newPassword"></fish-input>
        </fish-field>
        <fish-field label="Confirm new password" name="confirmPassword" :rules="[{required: true}]">
          <fish-input type="password" v-model="form.confirmPassword"></fish-input>
        </fish-field>
        <fish-button type="primary" @click="submitHandler" :loading="submitting">Update password</fish-button>
      </fish-form>
    </fish-tab-pane>
  </fish-tabs>
</template>
<script>
  import request from '../../request'

  export default {
    data () {
      return {
        submitting: false,
        form: {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
      }
    },
    methods: {
      submitHandler () {
        this.$refs.form.validate((valid) => {
          if (valid) {
            // this.$emit('ok', this.form)
            this.submitting = true
            request(`/api/current/password`, {method: 'PUT', body: JSON.stringify(this.form)}).then(() => {
              this.$message.success('Password update successful')
              this.submitting = false
              setTimeout(() => {
                this.$router.replace('/login')
              }, 800)
            }).catch(() => {
              this.submitting = false
            })
          }
        })
      }
    }
  }
</script>
<style>
  .change-password.fish.form .field input {
    width: 400px!important;
  }
</style>
