import base from '../base'
const state = Object.assign({}, base.state)
const getters = Object.assign({}, base.getters('role'))
const actions = Object.assign({}, base.actions('role', 'roles'))
const mutations = Object.assign({}, base.mutations('role'))

export default {
  state,
  getters,
  actions,
  mutations
}
