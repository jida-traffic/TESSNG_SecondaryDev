<template>
  <!-- <router-link
    class="nav-link"
    :to="myLink"
    @click.native="changeLink"
    v-if="!isExternal(link)"
    :class="{'router-link-active': current === item.text}"
    :exact="exact">
    <reco-icon :icon="`${item.icon}`" />
    {{ item.text }}
  </router-link> -->
  <a
    class="nav-link"
    v-if="!isExternal(link)"
    :class="{'router-link-active': current === item.text}"
    :exact="exact"
    @click="changeLink"
  >
    <reco-icon :icon="`${item.icon}`" />
    {{ item.text }}
  </a>
  <a
    v-else
    :href="link"
    class="nav-link external"
    :target="isMailto(link) || isTel(link) ? null : '_blank'"
    :rel="isMailto(link) || isTel(link) ? null : 'noopener noreferrer'"
  >
    <reco-icon :icon="`${item.icon}`" />
    {{ item.text }}
    <OutboundLink/>
  </a>
</template>

<script>
import { defineComponent, computed, toRefs, inject, onMounted, watch, ref } from 'vue'
import { isExternal, isMailto, isTel, ensureExt } from '@theme/helpers/utils'
import { RecoIcon } from '@vuepress-reco/core/lib/components'
import { useInstance } from '@theme/helpers/composable'

export default defineComponent({
  components: { RecoIcon },

  props: {
    item: {
      required: true
    },
    typeKey: String,
    current: String
  },

  setup (props, ctx) {
    const instance = useInstance()

    const { item, typeKey } = toRefs(props)
    // 现在选择
    let myLink = ref('')
    const userLinks = inject('userLinks')
    const link = computed(() => ensureExt(item.value.link))

    const exact = computed(() => {
      if (instance.$site.locales) {
        return Object.keys(instance.$site.locales).some(rootLink => rootLink === link.value)
      }
      return link.value === '/'
    })

    // 根据路由改变修改链接跳转
    watch(() => instance.$route.path, (toPath) => {
      let downSelects = {}
      let path = instance.$route.path.split('/')
      let position = path.indexOf('document')
      if (position >= 0 && (position+2) < path.length) {
        downSelects['version'] = path[position+1]
        downSelects['language'] = path[position+2]
      }
      if (!Object.keys(downSelects).length > 0) {
        downSelects['language'] = userLinks.value[0].items[0].text
        downSelects['version'] = userLinks.value[1].items[0].text
      }
      let tempPath = item.value.link
      if (typeKey.value=== 'version') {
        let findItem = userLinks.value[0].items.filter((i) => i.text === downSelects.language)
        if (findItem.length > 0) {
          let langPath = findItem[0].link
          tempPath = `/document${item.value.link}${langPath}`
        }
      } else if (typeKey.value=== 'language') {
        let findItem = userLinks.value[1].items.filter((i) => i.text === downSelects.version)
        if (findItem.length > 0) {
          let versionPath = findItem[0].link
          tempPath = `/document${versionPath}${item.value.link}`
        }
      }
      myLink.value = ensureExt(tempPath)
    },{ immediate: true, deep: true })
    onMounted(() => {
    })

    // 增加代码
    const changeLink = () => {
      instance.$router.push(myLink.value)
    }
    return { link, exact, isExternal, isMailto, isTel, changeLink, myLink }
  }
})
</script>
