<template>
  <fish-form inline class="form-search" @submit.native.prevent="submitHandler">
    <fish-field>
      <fish-input v-model="form.name" hint="Name"></fish-input>
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
          name: ''
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
