<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="sidebar-logo-svg">
          <defs>
            <linearGradient id="sidebarSparkleGrad" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stop-color="#9b5de5" />
              <stop offset="50%" stop-color="#f15bb5" />
              <stop offset="100%" stop-color="#00bbf9" />
            </linearGradient>
          </defs>
          <path fill="url(#sidebarSparkleGrad)" d="M12 2c.4 3.7 3.3 6.6 7 7-3.7.4-6.6 3.3-7 7-.4-3.7-3.3-6.6-7-7 3.7-.4 6.6-3.3 7-7z"/>
          <path fill="url(#sidebarSparkleGrad)" opacity="0.7" d="M19 14c.2 1.8 1.6 3.2 3.5 3.4-1.8.2-3.2 1.6-3.4 3.5-.2-1.8-1.6-3.2-3.5-3.4 1.8-.2 3.2-1.6 3.4-3.5z"/>
          <path fill="url(#sidebarSparkleGrad)" opacity="0.6" d="M5 4c.1 1 .9 1.8 2 1.9-1 .1-1.8.9-1.9 2C5 7 4.2 6.2 3.1 6.1 4.2 6 5 5.2 5 4z"/>
        </svg>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="sidebar-logo-svg">
          <path fill="url(#sidebarSparkleGrad)" d="M12 2c.4 3.7 3.3 6.6 7 7-3.7.4-6.6 3.3-7 7-.4-3.7-3.3-6.6-7-7 3.7-.4 6.6-3.3 7-7z"/>
          <path fill="url(#sidebarSparkleGrad)" opacity="0.7" d="M19 14c.2 1.8 1.6 3.2 3.5 3.4-1.8.2-3.2 1.6-3.4 3.5-.2-1.8-1.6-3.2-3.5-3.4 1.8-.2 3.2-1.6 3.4-3.5z"/>
          <path fill="url(#sidebarSparkleGrad)" opacity="0.6" d="M5 4c.1 1 .9 1.8 2 1.9-1 .1-1.8.9-1.9 2C5 7 4.2 6.2 3.1 6.1 4.2 6 5 5.2 5 4z"/>
        </svg>
        <h1 class="sidebar-title">{{ title }}</h1>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import variables from '@/assets/styles/variables.module.scss'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE
const settingsStore = useSettingsStore()
const sideTheme = computed(() => settingsStore.sideTheme)

// 获取Logo背景色
const getLogoBackground = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-bg)'
  }
  if (settingsStore.navType == 3) {
    return variables.menuLightBg
  }
  return sideTheme.value === 'theme-dark' ? variables.menuBg : variables.menuLightBg
})

// 获取Logo文字颜色
const getLogoTextColor = computed(() => {
  if (settingsStore.isDark) {
    return 'var(--sidebar-text)'
  }
  if (settingsStore.navType == 3) {
    return variables.menuLightText
  }
  return sideTheme.value === 'theme-dark' ? '#fff' : variables.menuLightText
})
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  height: 50px;
  line-height: 50px;
  background: v-bind(getLogoBackground);
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    & .sidebar-logo-svg {
      width: 22px;
      height: 22px;
      vertical-align: middle;
      filter: drop-shadow(0 0 5px rgba(155, 93, 229, 0.45));
    }

    & .sidebar-title {
      display: inline-block;
      margin: 0 0 0 10px;
      color: v-bind(getLogoTextColor);
      font-weight: 600;
      line-height: 50px;
      font-size: 14px;
      font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo-svg {
      margin-right: 0px;
    }
  }
}
</style>