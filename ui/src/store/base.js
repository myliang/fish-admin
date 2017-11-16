import request from '../request'
import querystring from 'querystring'

const pageRows = 5

const state = {
  item: null,
  items: [],
  total: 0,
  loading: false,
  pagination: null,
  queryPayload: {}
}
const filters = {
  saveBefore (state, payload) {},
  queryAfter (data) {}
}
const getters = (moduleName) => {
  let ret = {}
  Object.keys(state).forEach((stateName) => {
    let getterMethod = moduleName + stateName[0].toUpperCase() + stateName.substring(1)
    // console.log('getter: ', getterMethod)
    ret[getterMethod] = (state) => state[stateName]
  })
  return ret
}
const actions = (moduleName, modulePath) => {
  let ret = {}
  ret[`${moduleName}Create`] = ({commit, dispatch, state}, payload = {}) => {
    filters.saveBefore(state, payload)
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}`, {method: 'POST', body: JSON.stringify(payload)}).then(({data, total}) => {
        // commit(`${moduleName}Create`, data)
        dispatch(`${moduleName}Query`, state.queryPayload)
        resolve()
      })
    })
  }
  ret[`${moduleName}Update`] = ({commit, dispatch, state}, payload = {}) => {
    filters.saveBefore(state, payload)
    const id = payload['id']
    delete payload['id']
    // console.log('id: ', id, ', payload: ', payload)
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`, {method: 'PUT', body: JSON.stringify(payload)}).then(({data, total}) => {
        // commit(`${moduleName}Update`, data)
        dispatch(`${moduleName}Query`, state.queryPayload)
        resolve()
      })
    })
  }
  ret[`${moduleName}Delete`] = ({commit, dispatch, state}, id) => {
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`, {method: 'DELETE'}).then(({data, total}) => {
        // commit(`${moduleName}Delete`, data)
        dispatch(`${moduleName}Query`, state.queryPayload)
        resolve()
      })
    })
  }
  ret[`${moduleName}Get`] = ({commit, dispatch, state}, payload = {}) => {
    const { id } = payload
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`).then(({data, total}) => {
        commit(`${moduleName}Get`, data)
        resolve()
      })
    })
  }
  ret[`${moduleName}Query`] = ({commit, dispatch, state}, payload = {}) => {
    return new Promise((resolve, reject) => {
      commit(`${moduleName}Loading`)
      commit(`${moduleName}QueryPayload`, payload)
      request(`/api/${modulePath}?rows=${pageRows}&${querystring.stringify(payload)}`).then(({data, total}) => {
        commit(`${moduleName}Query`, {data, total, payload})
        resolve()
      })
    })
  }
  ret[`${moduleName}All`] = ({commit, dispatch, state}) => {
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/all`).then(({data}) => {
        commit(`${moduleName}All`, data)
        resolve()
      })
    })
  }
  return ret
}
const mutations = (moduleName) => {
  let ret = {}
  ret[`${moduleName}Loading`] = (state) => {
    state.loading = true
  }
  ret[`${moduleName}QueryPayload`] = (state, payload) => {
    state.queryPayload = Object.assign({}, payload)
  }
  ret[`${moduleName}Create`] = (state, data) => {
    state.item = data
  }
  ret[`${moduleName}Update`] = (state, data) => {
    state.item = data
  }
  ret[`${moduleName}Delete`] = (state, data) => {
    state.item = data
  }
  ret[`${moduleName}Get`] = (state, data) => {
    state.item = data
  }
  ret[`${moduleName}Query`] = (state, {data, total, payload}) => {
    // console.log(data, ', total:', total, ', payload: ', payload)
    filters.queryAfter(state, data)
    state.items = data
    state.total = total
    state.loading = false
    state.pagination = { total: total, current: payload['page'] || 1, rows: pageRows }
  }
  ret[`${moduleName}All`] = (state, data) => {
    state.items = data
  }
  return ret
}

export default {
  state,
  filters,
  getters,
  actions,
  mutations
}
