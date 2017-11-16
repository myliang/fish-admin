<template>
  <fish-form ref="form" @submit.native.prevent="submitHandler">
    <fish-fields>
      <fish-field span="12" label="User Name" name="userName" :rules="[{required: true}]">
        <fish-input v-model="form.userName"></fish-input>
      </fish-field>
      <fish-field span="12" label="Password (default value: 123456)">
        <fish-input v-model="form.password"></fish-input>
      </fish-field>
    </fish-fields>
    <fish-fields>
      <fish-field span="12" label="Role">
        <fish-select v-model="form.roleId" :disabled="'admin' === form.userName">
          <fish-option :index="role.id" :content="role.name" v-for="role in roles" :key="role.id"></fish-option>
        </fish-select>
      </fish-field>
      <fish-field span="12" label="State" name="state" :rules="[{required: true}]">
        <fish-select v-model="form.state">
          <fish-option :index="state[0]" :content="state[1]" v-for="state in states" :key="state[0]"></fish-option>
        </fish-select>
      </fish-field>
    </fish-fields>
    <fish-button type="primary" @click="submitHandler">Submit</fish-button>
  </fish-form>
</template>
<script>
  export default {
    name: 'user-edit',
    props: {
      states: { type: Array, default: [] },
      roles: { type: Array, default: [] },
      record: { type: Object }
    },
    data () {
      const { userName, state, roleId } = this.record || {}
      return {
        form: {
          userName,
          state,
          roleId
        }
      }
    },
    methods: {
      submitHandler () {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.$emit('ok', this.form)
          }
        })
      }
    }
  }
</script>
