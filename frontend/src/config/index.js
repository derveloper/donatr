let base = {
  api: {
    url: 'http://localhost'
  }
}

const development = {
  api: {
    url: 'http://172.28.0.99:8080/api'
  }
}

const production = {
  api: {
    url: 'http://api.donatr.local'
  }
}

if (__DEV__) {
  base = Object.assign({}, base, development)
}

if (__PROD__) {
  base = Object.assign({}, base, production)
}

export default base
