import base from '../base'
const state = Object.assign({}, base.state)
const getters = Object.assign({}, base.getters('user'))
const actions = Object.assign({}, base.actions('user', 'users'))
const mutations = Object.assign({}, base.mutations('user'))

export default {
  state,
  getters,
  actions,
  mutations
}
