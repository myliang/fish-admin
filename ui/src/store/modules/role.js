import base from '../base'
import request from '../../request'
const state = Object.assign({
  permissions: null
}, base.state)
const getters = Object.assign({
  rolePermissions (state) {
    return state.permissions
  }
}, base.getters('role'))
const actions = Object.assign({
  rolePermissions ({commit, dispatch, state}) {
    return new Promise((resolve, reject) => {
      request(`/api/roles/permissions`).then(({data}) => {
        commit(`rolePermissions`, data)
        resolve()
      })
    })
  }
}, base.actions('role', 'roles'))
const mutations = Object.assign({
  rolePermissions (state, data) {
    state.permissions = data
  }
}, base.mutations('role'))

export default {
  state,
  getters,
  actions,
  mutations
}
