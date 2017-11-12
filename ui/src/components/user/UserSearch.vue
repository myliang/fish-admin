<template>
  <fish-form inline class="form-search" @submit.native.prevent="submitHandler">
    <fish-field>
      <fish-input v-model="form.userName" hint="User Name"></fish-input>
    </fish-field>
    <fish-field>
      <fish-select v-model="form.state">
        <fish-option :index="state[0]" :content="state[1]" v-for="state in states" :key="state[0]"></fish-option>
      </fish-select>
    </fish-field>
    <fish-button type="primary" @click="submitHandler">Submit</fish-button>
  </fish-form>
</template>
<script>
  export default {
    name: 'user-search',
    props: {
      states: { type: Array }
    },
    data () {
      return {
        form: {
          userName: '',
          state: ''
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
