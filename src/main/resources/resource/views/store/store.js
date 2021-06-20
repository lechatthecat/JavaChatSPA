import Vue from 'vue'
import Vuex from 'vuex'
import store_auth from './store_auth.js';

Vue.use(Vuex)

const state = {
  sidebarShow: 'responsive',
  footerShow: true,
  sidebarMinimize: false,
  categoryStringId: null,
  nav: [{}],
}

const mutations = {
  toggleSidebarDesktop (state) {
    const sidebarOpened = [true, 'responsive'].includes(state.sidebarShow)
    state.sidebarShow = sidebarOpened ? false : 'responsive'
  },
  toggleSidebarMobile (state) {
    const sidebarClosed = [false, 'responsive'].includes(state.sidebarShow)
    state.sidebarShow = sidebarClosed ? true : 'responsive'
  },
  set (state, [variable, value]) {
    state[variable] = value
  },
  navInitialize (state) {
    state.nav = [{}];
  },
  removeCreateBoard (state) {
    if ("_children" in state.nav[0]) {
      const lastElement = state.nav[0]._children[state.nav[0]._children.length - 1];
      if (lastElement.name === "CreateBoard") {
        state.nav[0]._children.pop();
        state.nav[0]._children.pop();
      }
    }
  },
  addCreateBoard (state) {
    if ("_children" in state.nav[0]) {
      const lastElement = state.nav[0]._children[state.nav[0]._children.length - 1];
      if (lastElement.name !== "CreateBoard") {
        state.nav[0]._children.push({
          _name: 'CSidebarNavTitle',
          _children: ['Create Board']
        });
        state.nav[0]._children.push({
          _name: 'CSidebarNavItem',
          name: 'CreateBoard',
          to: '/board_create',
          icon: 'cil-pencil'
        });
      }
    }
  },
}

const actions = {}

export default new Vuex.Store({
  modules: {
    store_auth: store_auth
  },
  state,
  mutations,
  actions
})