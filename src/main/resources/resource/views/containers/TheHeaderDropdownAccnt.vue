<template>
  <CDropdown
    v-if="isSignedIn()"
    inNav
    class="c-header-nav-items chat-board-default-header"
    placement="bottom-end"
    add-menu-classes="pt-0"
  >
    <template #toggler>
      <CHeaderNavLink>
        <div class="c-avatar">
          <img
            v-bind:src="getUserImage()"
            class="c-avatar-img "
          />
        </div>
      </CHeaderNavLink>
    </template>
    <CDropdownHeader tag="div" class="text-center" color="light">
      <strong>Account</strong>
    </CDropdownHeader>
    <CDropdownItem href="#" @click="logout">
      <CIcon name="cil-lock-locked" /> Logout
    </CDropdownItem>
    <div v-if="showsLoadingMark">
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