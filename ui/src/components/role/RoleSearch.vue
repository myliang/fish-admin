<template>
  <fish-form inline class="form-search" @submit.native.prevent="submitHandler">
    <fish-field>
      <fish-input v-model="form.userName" hint="User Name"></fish-input>
    </fish-field>
    <fish-button type="primary" @click="submitHandler">Search</fish-button>
  </fish-form>
</template>
<script>
  export default {
    name: 'role-search',
    props: {
    },
    data () {
      return {
        form: {
          userName: ''
        }
      }
    },
    methods: {
      submitHandler () {
        this.$emit('ok', this.form)
      }
    }
  }
</script>
