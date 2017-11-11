// import http from 'axios'
import 'whatwg-fetch'

function checkStatus (response) {
  // console.log(response)
  if (response.status >= 200 && response.status < 300) {
    return response
  } else {
    let error = new Error(response.statusText)
    error.response = response
    throw error
  }
}

export default function request (url, options = {}) {
  return new Promise((resolve, reject) => {
    url = `${process.env.API_ROOT}${url}`
    if (!options) options = {}
    options['headers'] = {
      'Content-Type': 'application/json;charset:utf-8;',
      'Authorization': `Bearer ${sessionStorage.getItem('token') || ''}`
    }
    options['mode'] = options['mode'] || 'cors'
    // options['credentials'] = 'include'
    // console.log(options)

    fetch(url, options)
      .then(checkStatus)
      .then((response) => {
        try {
          // console.log(response)
          response.json().then((data) => {
            const ret = {
              data: data,
              total: 0
            }
            if (response.headers.get('x-total-count')) {
              ret.total = parseInt(response.headers.get('x-total-count'))
            }
            resolve(ret)
          }).catch((e) => reject(e))
        } catch (e) {
          reject(e)
        }
      }).catch(function (error) {
        console.log(':::', window.$message, '>>>', error.response)
        window.$message.warning(error.response.headers['statusText'] || 'request error!')
        // reject(error)
      })
  })
}
