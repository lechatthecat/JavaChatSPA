<template>
  <CFooter 
    :fixed="false"
    v-if="footerShow"
    >
    <div style="color:#808080;font-size: 12px;">
      <span class="ml-1 pointer" @click="$router.push({ name: 'AboutUs' })">&copy; {{new Date().getFullYear()}} TestTestTest.</span>
    </div>
    <div class="ml-1 pointer" style="color:#808080;font-size: 12px;">
      <span @click="$router.push({ name: 'PrivacyPolicy' });">Privacy Policy</span>
    </div>
    <div class="ml-1 pointer" style="color:#808080;font-size: 12px;">
      <span @click="$router.push({ name: 'TermOfUse' });">Term of use</span>
    </div>
    <div class="mfs-auto" style="color:#808080;font-size: 12px;">
      <span class="mr-1" target="_blank">Powered by</span>
      <a style="color: rgb(128, 128, 128);" href="https://coreui.io/vue">CoreUI for Vue</a>
    </div> 
  </CFooter>
</template>

<script>
export default {
  name: 'TheFooter',
  computed: {
    footerShow () {
      return this.$store.state.footerShow; 
    },
  },
}
</script>
