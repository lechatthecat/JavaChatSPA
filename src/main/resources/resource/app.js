require('./bootstrap');

// vue modules
import Vue from 'vue'
import store from '@/views/store'

// Component files
import App from '@/views/App'

// bootstrap-vue
import BootstrapVue from 'bootstrap-vue'
Vue.use(BootstrapVue);

// Vue router
import router from '@/routes/index.js'

// Other libraries
import 'core-js/stable'
import { iconsSet as icons } from '@/views/icons/icons.js'
import CoreuiVue from '@coreui/vue'

Vue.config.performance = true;
Vue.use(CoreuiVue);
Vue.prototype.$log = console.log.bind(console);

Vue.mixin({
  methods: {
    getCookie: function (cname) {
      const name = cname + "=";
      const decodedCookie = decodeURIComponent(document.cookie);
      const ca = decodedCookie.split(";");
      for (let i = 0; i < ca.length; i++) {
        const c = ca[i];
        while (c.charAt(0) == " ") {
          c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
          return c.substring(name.length, c.length);
        }
      }
      return "";
    },
    isSignedIn: function () {
      if (_.isEmpty(this.$auth.user())) {
        return false;
      }
      return true;
    },
  }
});

// auth
// Set Vue authentication
import auth                  from '@websanova/vue-auth/dist/v2/vue-auth.common.js';
import driverAuthBearer      from '@websanova/vue-auth/src/drivers/auth/bearer.js';
import driverHttpAxios       from '@websanova/vue-auth/src/drivers/http/axios.1.x.js';
import driverRouterVueRouter from '@websanova/vue-auth/src/drivers/router/vue-router.2.x.js';

import axios from 'axios';
axios.defaults.baseURL = `/api`;
axios.defaults.headers.common['Accept'] = 'application/json';
axios.defaults.withCredentials = true;
Vue.axios = axios;
Vue.router = router;
const isHttps = (window.location.protocol === 'https:');
const hostName = window.location.hostname;
Vue.use(auth, {
  plugins: {
    http: Vue.axios,
    router: Vue.router,
  },
  drivers: {
      http: driverHttpAxios,
      auth: driverAuthBearer,
      router: driverRouterVueRouter,
  },
  options: {
    tokenDefaultKey: 'chat_board_login_token',
    tokenDefaultName: 'chat_board_login_token',
    stores: ['cookie'],
    cookie: { Path: '/', Domain: hostName, Secure: isHttps, SameSite: 'lax' },
    rolesVar: 'role',
    rolesKey: 'name',
    registerData: {url: 'create_user', method: 'POST', fetchUser: true},
    loginData: {url: 'login', method: 'POST', fetchUser: true},
    logoutData: {url: 'logout', method: 'POST', redirect: '/', makeRequest: true},
    fetchData: {url: 'user', method: 'GET', enabled: true},
    refreshData: {url: 'refresh', method: 'GET', enabled: true, interval: 20160}
  }
});

Date.prototype.yyyymmddHHMMss = function() {
  const mm = this.getUTCMonth() + 1; // getMonth() is zero-based
  const dd = this.getUTCDate();
  const HH = this.getUTCHours();
  const MM = this.getUTCMinutes();
  const ss = this.getUTCSeconds();

  const dateString = [this.getUTCFullYear(),
          (mm>9 ? '' : '0') + mm,
          (dd>9 ? '' : '0') + dd
        ].join('/');
  const hourString = [
    (HH>9 ? '' : '0') + HH,
    (MM>9 ? '' : '0') + MM,
    (ss>9 ? '' : '0') + ss,
  ].join(':');
  const dateTimeString = dateString + " " + hourString;
  return dateTimeString;
};
Date.prototype.addHours = function(h) {
  this.setTime(this.getTime() + (h*60*60*1000));
  return this;
}
Date.prototype.minusHours = function(h) {
  this.setTime(this.getTime() - (h*60*60*1000));
  return this;
}

const app = new Vue({
  el: '#app',
  router,
  auth,
  store,
  icons,
  template: '<App/>',
  render: h => h(App),
});

export default app;
