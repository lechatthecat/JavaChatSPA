<template>
  <CDropdown
    inNav
    class="c-header-nav-items chat-board-default-header"
    placement="bottom-end"
    add-menu-classes="pt-0"
  >
    <template #toggler>
      <CHeaderNavLink>
        <div class="c-avatar">
          <img
            v-if="isSignedIn()"
            v-bind:src="getUserImage()"
            class="c-avatar-img "
          />
          <img
            v-if="!isSignedIn()"
            src="/public/img/others/blank.svg"
            class="c-avatar-img "
          />
        </div>
      </CHeaderNavLink>
    </template>
    <CDropdownHeader
      tag="div"
      class="text-center"
      color="light">
      <strong>Account</strong>
    </CDropdownHeader>
    <CDropdownItem
    　 v-if="isSignedIn()"
      href="#"
      @click="logout">
      <CIcon name="cil-lock-unlocked" /> Logout
    </CDropdownItem>
    <CDropdownItem
    　v-if="!isSignedIn()"
      href="#"
      @click="$router.push({ path: '/login' });">
      <CIcon name="cil-lock-locked" /> Login
    </CDropdownItem>
    <div v-if="showsLoadingMask">
      <div class="modal-mask justify-content-center align-items-center" style="z-index:1050;"></div>
      <div class="waiting-loader" style="opacity 400ms"></div>
    </div>
  </CDropdown>
</template>

<script>
export default {
  name: 'TheHeaderDropdownAccnt',
  data () {
    return { 
    }
  },
  methods: {
    getUserImage: function () {
      if (!this.isSignedIn()) {
        return "";
      }
      return this.$auth.user().userMainImage;
    },
    logout(){
      this.$auth.logout().then(()=>{
        this.$router.push({ path: "/login" });
      });
    }
  }
}
</script>

<style scoped>
  .c-icon {
    margin-right: 0.3rem;
  }
</style>