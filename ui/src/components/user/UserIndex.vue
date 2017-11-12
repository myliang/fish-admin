<template>
  <div>
    <user-search :states="states" @ok="searchHandler"></user-search>
    <fish-table :columns="columns" :data="data" :loading="loading" :pagination="pagination"></fish-table>
    <fish-modal :visible.sync="editVisible" title="User Information...">
      <user-edit :states="states" :roles="roles" :record="record"></user-edit>
    </fish-modal>
  </div>
</template>
<script>
  import {mapGetters} from 'vuex'
  import UserSearch from './UserSearch.vue'
  import UserEdit from './UserEdit.vue'

  export default {
    components: {
      UserEdit,
      UserSearch},
    data () {
      return {
        editVisible: false,
        record: null,
        states: [['enable', 'Enable'], ['disable', 'Disable']],
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
        loading: 'userLoading',
        roles: 'roleItems'
      })
    },
    methods: {
      loadData () {
        this.$store.dispatch('userQuery', {})
        this.$store.dispatch('roleAll')
      },
      editHandler (record) {
        this.record = record
        this.editVisible = true
      },
      searchHandler (form) {
        this.$store.dispatch('userQuery', form)
      }
    }
  }
</script>
