<template>
  <div class="dropdown-wrapper" :class="{ open }">
    <a class="dropdown-title" @click="toggle">
      <span class="title">
        <reco-icon :icon="`${item.icon}`" />
        <!-- 添加代码 -->
        {{ current }} 
      </span>
      <span class="arrow" :class="open ? 'down' : 'right'"></span>
    </a>

    <DropdownTransition>
      <ul class="nav-dropdown" v-show="open">
        <li
          class="dropdown-item"
          :key="subItem.link || index"
          v-for="(subItem, index) in item.items"
        >
          <h4 v-if="subItem.type === 'links'">{{ subItem.text }}</h4>

          <ul
            class="dropdown-subitem-wrapper"
            v-if="subItem.type === 'links'"
          >
            <li
              class="dropdown-subitem"
              :key="childSubItem.link"
              v-for="childSubItem in subItem.items"
            ><NavLink :item="childSubItem"/></li>
          </ul>

          <NavLink v-else :item="subItem" :typeKey="item.key" :current="current"/>
        </li>
      </ul>
    </DropdownTransition>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted,watch } from 'vue'
import { RecoIcon } from '@vuepress-reco/core/lib/components'
import NavLink from '@theme/components/NavLink'
import DropdownTransition from '@theme/components/DropdownTransition'
/** 
 * 添加代码
**/
import { useInstance } from '@theme/helpers/composable'

export default defineComponent({
  components: { NavLink, DropdownTransition, RecoIcon },

  props: {
    item: {
      required: true
    }
  },

  setup (props, ctx) {
    const open = ref(false)

    const toggle = () => {
      open.value = !open.value
    }

    /** 
     * 右侧下拉
     * 添加代码
    **/
    // 当前的Link
    const instance = useInstance()
    const current = ref('')
    watch(() => instance.$route.path, (toPath) => {
      let downSelects = {}
      let tempPath = instance.$route.path.split('/')
      let position = tempPath.indexOf('document')
      if (position >= 0 && (position+2) < tempPath.length) {
        downSelects['version'] = tempPath[position+1]
        downSelects['language'] = tempPath[position+2]
      }
      if (Object.keys(downSelects).length > 0) {
        current.value = downSelects[props.item.key]
      } else {
        // 如果路径没有选择的直接赋值第一个
        if (props.item?.items && props.item.items.length > 0) {
          current.value = props.item.items[0].text ? props.item.items[0].text : item.text
        } else {
          current.value = item.text
        }
      }
    },{ immediate: true, deep: true })

    return { open, toggle, current }
  }
})
</script>

<style lang="stylus">
.dropdown-wrapper
  cursor pointer
  .dropdown-title
    display block
    &:hover
      border-color transparent
    .arrow
      vertical-align middle
      margin-top -1px
      margin-left 0.4rem
  .nav-dropdown
    .dropdown-item
      color inherit
      line-height 1.7rem
      h4
        margin 0.45rem 0 0
        border-top 1px solid var(--border-color)
        padding 0.45rem 1.5rem 0 1.25rem
      .dropdown-subitem-wrapper
        padding 0
        list-style none
        .dropdown-subitem
          font-size 0.9em
      a
        display block
        line-height 1.7rem
        position relative
        border-bottom none
        font-weight 400
        margin-bottom 0
        padding 0 1.5rem 0 1.25rem
        &:hover
          color $accentColor
        &.router-link-active
          color $accentColor
          &::after
            content ""
            width 0
            height 0
            border-left 5px solid $accentColor
            border-top 3px solid transparent
            border-bottom 3px solid transparent
            position absolute
            top calc(50% - 2px)
            left 9px
      &:first-child h4
        margin-top 0
        padding-top 0
        border-top 0

@media (max-width: $MQMobile)
  .dropdown-wrapper
    &.open .dropdown-title
      margin-bottom 0.5rem
    .nav-dropdown
      transition height .1s ease-out
      overflow hidden
      .dropdown-item
        h4
          border-top 0
          margin-top 0
          padding-top 0
        h4, & > a
          font-size 15px
          line-height 2rem
        .dropdown-subitem
          font-size 14px
          padding-left 1rem

@media (min-width: $MQMobile)
  .dropdown-wrapper
    height 1.8rem
    &:hover .nav-dropdown
      // override the inline style.
      display block !important
    .dropdown-title .arrow
      // make the arrow always down at desktop
      border-left 4px solid transparent
      border-right 4px solid transparent
      border-top 6px solid var(--text-color-sub)
      border-bottom 0
    .nav-dropdown
      display none
      // Avoid height shaked by clicking
      height auto !important
      box-sizing border-box;
      max-height calc(100vh - 2.7rem)
      overflow-y auto
      position absolute
      top 100%
      right 0
      background-color var(--background-color)
      padding 0.6rem 0
      box-shadow: var(--box-shadow);
      text-align left
      border-radius $borderRadius
      white-space nowrap
      margin 0
</style>
