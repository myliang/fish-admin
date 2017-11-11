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
          {title: 'name', key: 'name'},
          {title: 'Permissions', key: 'permissions'},
          {title: 'Created at', key: 'createdAt'},
          {title: 'Operate', key: 'operate', render: (h, record, column) => h('a', 'edit')}
        ]
      }
    },
    mounted () {
      // load data
      this.loadData()
    },
    computed: {
      ...mapGetters({
        data: 'roleItems',
        pagination: 'rolePagination',
        loading: 'roleLoading'
      })
    },
    methods: {
      loadData () {
        this.$store.dispatch('roleQuery', {})
      }
    }
  }
</script>
