<template>
  <div class="c-app" :class="putElementCenterVertical?'h100p':''">
    <CWrapper>
      <div class="c-body">
        <main class="c-main">
            <transition name="fade" mode="out-in">
              <router-view :key="$route.path"></router-view>
            </transition>
        </main>
      </div>
      <TheFooter/>
    </CWrapper>
  </div>
</template>

<script>
import TheFooter from './TheFooter'

export default {
  name: 'OnlyFooter',
  components: {
    TheFooter
  },
    data() {
    return {
      putElementCenterVertical: false,
    };
  },
  created () {
    if (["Login", "CreateUser"].indexOf(this.$router.currentRoute.name) > -1) {
        this.putElementCenterVertical = true;
    } else {
        this.putElementCenterVertical = false;
    }
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
