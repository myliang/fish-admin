import request from '../request'
import querystring from 'querystring'

const state = {
  item: null,
  items: [],
  total: 0,
  loading: false,
  pagination: null
}
const getters = (moduleName) => {
  let ret = {}
  Object.keys(state).forEach((stateName) => {
    let getterMethod = moduleName + stateName[0].toUpperCase() + stateName.substring(1)
    console.log('getter: ', getterMethod)
    ret[getterMethod] = (state) => state[stateName]
  })
  return ret
}
const actions = (moduleName, modulePath) => {
  let ret = {}
  ret[`${moduleName}Create`] = ({commit, dispatch, state}, payload) => {
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}`, {method: 'POST', body: JSON.stringify(payload)}).then(({data, total}) => {
        commit(`${moduleName}Create`, data)
        resolve()
      })
    })
  }
  ret[`${moduleName}Update`] = ({commit, dispatch, state}, id, payload) => {
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`, {method: 'PUT', body: JSON.stringify(payload)}).then(({data, total}) => {
        commit(`${moduleName}Update`, data)
        resolve()
      })
    })
  }
  ret[`${moduleName}Delete`] = ({commit, dispatch, state}, id) => {
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`, {method: 'DELETE'}).then(({data, total}) => {
        commit(`${moduleName}Delete`, data)
        resolve()
      })
    })
  }
  ret[`${moduleName}Get`] = ({commit, dispatch, state}, payload) => {
    const { id } = payload
    return new Promise((resolve, reject) => {
      request(`/api/${modulePath}/${id}`).then(({data, total}) => {
        commit(`${moduleName}Get`, data)
        resolve()
      })
    })
  }
  ret[`${moduleName}Query`] = ({commit, dispatch, state}, payload) => {
    return new Promise((resolve, reject) => {
      commit(`${moduleName}Loading`)
      request(`/api/${modulePath}?limit=20&${querystring.stringify(payload)}`).then(({data, total}) => {
        commit(`${moduleName}Query`, {data, total, payload})
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
    state.items = data
    state.total = total
    state.loading = false
    state.pagination = { total: total, current: payload['page'] || 1, rows: 20 }
  }
  return ret
}

export default {
  state,
  getters,
  actions,
  mutations
}
