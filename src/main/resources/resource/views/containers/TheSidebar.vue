<template>
  <CSidebar 
    fixed 
    :minimize="minimize"
    :show="show"
    @update:show="(value) => $store.commit('set', ['sidebarShow', value])"
  >
    <CSidebarBrand class="d-md-down-none" to="/">
      <span class="logo-lg header-logo c-sidebar-brand-full"><b>Chat</b>Board</span>
      <span class="logo-lg header-logo c-sidebar-brand-minimized"><b>C</b>B</span>
    </CSidebarBrand>

    <CRenderFunction flat :content-to-render="global_nav" />

    <CSidebarMinimizer
      class="d-md-down-none"
      @click.native="$store.commit('set', ['sidebarMinimize', !minimize])"
    />
  </CSidebar>
</template>

<script>
import imported_nav from './_nav'

export default {
  name: 'TheSidebar',
  data() {
    return {
      categories: [],
      nav: imported_nav,  
    };
  },
  async created() {
    this.jwtToken = "Bearer " + this.getCookie("chat_board_login_token");
    this.xsrf = this.getCookie("XSRF-TOKEN");
    if (this.nav[0]._children.length === 0) {
      await this.addMenusInSideNav(this.nav);
    }
    //if (this.isSignedIn()) {
      console.log("route");
      console.log(this.$router);
      console.log(this.$router.currentRoute);
      if (["Boards", "Chat"].indexOf(this.$router.currentRoute.name) > -1) {
        this.addCreateBoardButton(this.nav);
      }
    //}
    this.$store.commit('set', ['nav', this.nav]);
  },
  computed: {
    show () {
      return this.$store.state.sidebarShow;
    },
    minimize () {
      return this.$store.state.sidebarMinimize;
    },
    global_nav () {
      return this.$store.state.nav;
    }
  },
  methods: {
    setBoardCategories () {
      return axios
        .get("/get_categories");
    },
    async addMenusInSideNav(nav) {
      // add profile link
      if (this.isSignedIn()) {
        nav[0]._children.push({
          _name: 'CSidebarNavTitle',
          _children: ['Profile']
        });
        nav[0]._children.push({
          _name: 'CSidebarNavItem',
          name: 'Profile',
          to: '/profile',
          icon: 'cil-home',
        });
      }
      nav[0]._children.push({
        _name: 'CSidebarNavTitle',
        _children: ['Boards']
      });
      const res = await this.setBoardCategories();
      this.categories = res.data;
      this.categories.map(category => {
        nav[0]._children.push({
          _name: 'CSidebarNavItem',
          name: category.name,
          to: '/boards/' + category.urlName + '/0',
          icon: category.icon
        });
      });
    },
    addCreateBoardButton(nav) {
      //if (this.isSignedIn()) {
        const lastElement = nav[0]._children[nav[0]._children.length - 1];
        if (lastElement.name !== "CreateBoard") {
          nav[0]._children.push({
            _name: 'CSidebarNavTitle',
            _children: ['Create Board']
          });
          nav[0]._children.push({
            _name: 'CSidebarNavItem',
            name: 'CreateBoard',
            to: '/board_create',
            icon: 'cil-pencil'
          });
        }
      //}
    }
  }
}
</script>
