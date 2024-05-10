<template>
  <header class="navbar">
    <SidebarButton @toggle-sidebar="$emit('toggle-sidebar')"/>
    <div class="nav-home-link">
      <router-link
        :to="$localePath"
        class="home-link">
        <img
          class="logo"
          v-if="$themeConfig.logo"
          :src="$withBase(currentMode !== 'dark' ? $themeConfig.logo : $themeConfig.logoDark)"
          :alt="$siteTitle"
          @click="jumpToWeb"
        >
      </router-link>
      <div class="nav-home-text">
        <span
          ref="siteName"
          class="site-name"
          v-if="$siteTitle">{{ $siteTitle }}</span>
        <Mode @changeMode="changeMode"/>
      </div>
    </div>
    <div
      class="links"
      :style="linksWrapMaxWidth ? {
        'max-width': linksWrapMaxWidth + 'px'
      } : {}">
      <!-- 二次开发跳转链接 -->
      <div class="second-link" @click="jumpTo()">
        <img src="/link-icon.png" />
        二次开发工具包
      </div>
      <!-- <AlgoliaSearchBox
        v-if="isAlgoliaSearch"
        :options="algolia"/>
      <SearchBox v-else-if="$themeConfig.search !== false && $frontmatter.search !== false"/> -->
      <NavLinks class="can-hide"/>
    </div>
  </header>
</template>

<script>
import { defineComponent, ref, onMounted, computed } from 'vue'
import AlgoliaSearchBox from '@AlgoliaSearchBox'
import SearchBox from '@SearchBox'
import SidebarButton from '@theme/components/SidebarButton'
import NavLinks from '@theme/components/NavLinks'
import Mode from '@theme/components/Mode'
import { useInstance } from '@theme/helpers/composable'

export default defineComponent({
  components: { SidebarButton, NavLinks, SearchBox, AlgoliaSearchBox, Mode },

  setup (props, ctx) {
    const currentMode = ref('light')
    const instance = useInstance()
    console.log('instance', instance)
    const linksWrapMaxWidth = ref(null)
    const toolbagAddress = computed(() => {
      return instance.$toolbagAddress || ''
    })
    const algolia = computed(() => {
      return instance.$themeLocaleConfig.algolia || instance.$themeConfig.algolia || {}
    })

    const isAlgoliaSearch = computed(() => {
      return algolia.value && algolia.value.apiKey && algolia.value.indexName
    })

    function css (el, property) {
      // NOTE: Known bug, will return 'auto' if style value is 'auto'
      const win = el.ownerDocument.defaultView
      // null means not to return pseudo styles
      return win.getComputedStyle(el, null)[property]
    }

    onMounted(() => {
      const MOBILE_DESKTOP_BREAKPOINT = 719 // refer to config.styl
      const NAVBAR_VERTICAL_PADDING =
        parseInt(css(instance.$el, 'paddingLeft')) +
        parseInt(css(instance.$el, 'paddingRight'))

      const handleLinksWrapWidth = () => {
        if (document.documentElement.clientWidth < MOBILE_DESKTOP_BREAKPOINT) {
          linksWrapMaxWidth.value = null
        } else {
          linksWrapMaxWidth.value =
            instance.$el.offsetWidth -
            NAVBAR_VERTICAL_PADDING -
            (instance.$refs.siteName && instance.$refs.siteName.offsetWidth || 0)
        }
      }

      handleLinksWrapWidth()
      window.addEventListener('resize', handleLinksWrapWidth, false)
      window.addEventListener('storage', (e) => {
        console.log("storage值发生变化后触发:", e)
      })
    })

    const jumpTo = () => {
      window.open(instance.$themeConfig.toolbagAddress + 'simulation')
    }
    const jumpToWeb = () => {
      window.open(instance.$themeConfig.toolbagAddress)
    }
    const changeMode = (mode) => {
      currentMode.value = mode
    }
 
    return { linksWrapMaxWidth, algolia, isAlgoliaSearch, css , jumpTo,changeMode, currentMode, toolbagAddress, jumpToWeb}
  }
})
</script>

<style lang="stylus">
$navbar-vertical-padding = 0.7rem
$navbar-horizontal-padding = 1.5rem

.navbar
  padding $navbar-vertical-padding $navbar-horizontal-padding
  line-height $navbarHeight - 1.4rem
  box-shadow var(--box-shadow)
  background var(--background-color)
  a, span, img
    display inline-block
  .nav-home-link
    display flex
    align-items center
  .home-link
    display flex
    align-items center
  .nav-home-text
    display flex
    align-items center
  .color-picker 
    line-height 1
    margin-left 2rem
  .logo
    height $navbarHeight - 1.6rem
    min-width $navbarHeight - 1.6rem
    margin-right 1.5rem
    vertical-align top
  .site-name
    font-size 1.6rem
    font-weight 600
    color var(--text-color)
    position relative
  .links
    padding-left 1.5rem
    box-sizing border-box
    white-space nowrap
    font-size 0.9rem
    position absolute
    right $navbar-horizontal-padding
    top $navbar-vertical-padding
    display flex
    background-color var(--background-color)
    .search-box
      flex: 0 0 auto
      vertical-align top
.second-link
  display flex
  color rgba(59, 144, 255, 1)
  font-size 18px
  font-weight 500
  align-items center
  margin-right 1.5rem
  cursor pointer
  img
    width 24px
    height 24px
    margin-right .5rem
@media (max-width: $MQMobile)
  .navbar
    padding-left 4rem
    .can-hide
      display none
    .links
      padding-left .2rem
</style>
