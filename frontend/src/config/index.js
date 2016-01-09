let base = {
  api: {
    url: 'http://localhost'
  }
};

const development = {
  api: {
    url: 'http://localhost:8080/api'
  }
};

const production = {
  api: {
    url: '/api'
  }
};

if (__DEV__) {
  base = Object.assign({}, base, development);
}

if (__PROD__) {
  base = Object.assign({}, base, production);
}

export default base;
