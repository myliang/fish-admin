<template>
  <fish-table :columns="columns" :data="data" :loading="loading" :pagination="pagination"></fish-table>
</template>
<script>
  import {mapGetters} from 'vuex'

  export default {
    data () {
      return {
        columns: [
          {title: '#', type: 'index', width: '50', align: 'center'},
          {title: 'User name', key: 'userName'},
          {title: 'Role Name', key: 'roleName'},
          {title: 'State', key: 'state'},
          {title: 'Created at', key: 'createdAt'},
          {title: 'Operate', key: 'operate', render: (h, record, column) => h('a', {on: {click: this.editHandler.bind(null, record)}}, 'edit')}
        ]
      }
    },
    mounted () {
      // load data
      this.loadData()
    },
    computed: {
      ...mapGetters({
        data: 'userItems',
        pagination: 'userPagination',
        loading: 'userLoading'
      })
    },
    methods: {
      loadData () {
        this.$store.dispatch('userQuery', {})
      },
      editHandler (record) {
        console.log(record)
      }
    }
  }
</script>
