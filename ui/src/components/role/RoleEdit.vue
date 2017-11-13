<template>
  <fish-form ref="form" @submit.native.prevent="submitHandler">
    <fish-fields>
      <fish-field span="12" label="Name" name="name" :rules="[{required: true}]">
        <fish-input v-model="form.name"></fish-input>
      </fish-field>
      <fish-field span="12" label="Permission">
        <fish-tree-select :data="permissions" multiple v-model="form.permissions">
        </fish-tree-select>
      </fish-field>
    </fish-fields>
    <fish-button type="primary" @click="submitHandler">Submit</fish-button>
  </fish-form>
</template>
<script>
  export default {
    name: 'role-edit',
    props: {
      permissions: { type: Object },
      record: { type: Object }
    },
    data () {
      const { name, permissions } = this.record || {}
      return {
        form: {
          name,
          permissions
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
