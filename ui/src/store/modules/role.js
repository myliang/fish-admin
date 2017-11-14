import base from '../base'
import request from '../../request'

const permissionsToTree = (permissions) => {
  if (permissions === null) return []
  return Object.keys(permissions).map((key) => {
    return {
      key: key,
      title: key,
      children: permissions[key].map(k => { return {key: `${key}-${k}`, title: `${key}-${k}`} })}
  })
}
base.filters = Object.assign(base.filters, {
  saveBefore (state, payload) {
    // console.log(state, ':::', payload)
    if (payload.permissions) {
      let nPermissions = {}
      payload.permissions.forEach((p) => {
        let v = p.split('-')
        if (v.length > 1) {
          nPermissions[v[0]] = nPermissions[v[0]] || []
          nPermissions[v[0]].push(v[1])
        } else {
          nPermissions[p] = state.permissions[p]
        }
      })
      payload.permissions = JSON.stringify(nPermissions)
    }
  },
  queryAfter (state, data) {
    data.forEach((item) => {
      if (item.permissions) {
        // console.log(item.permissions, ':::', state.permissions)
        let pMap = JSON.parse(item.permissions)
        let nPermissions = []
        Object.keys(pMap).forEach((key) => {
          if (state.permissions[key] && pMap[key].length === state.permissions[key].length) {
            nPermissions.push(key)
          } else {
            pMap[key].forEach((k) => {
              nPermissions.push(`${key}-${k}`)
            })
          }
        })
        item.permissions = nPermissions
      }
    })
  }
})

const state = Object.assign({
  permissions: []
}, base.state)
const getters = Object.assign({
  rolePermissions (state) {
    return permissionsToTree(state.permissions)
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
