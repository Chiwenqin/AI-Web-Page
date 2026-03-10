<template>
  <component :is="type" v-bind="linkProps()">
    <slot />
  </component>
</template>

<script setup>
import { computed } from 'vue'
import { isExternal } from '@/utils/validate'

const props = defineProps({
  to: {
    type: [String, Object],
    required: true
  },
  meta: {
    type: Object,
    default: () => {}
  }
})

const linkUrl = computed(() => {
  return props.meta.link || props.to
})

const isExt = computed(() => {
  return isExternal(linkUrl.value)
})

const type = computed(() => {
  if (isExt.value || props.meta.newWindow) {
    return 'a'
  }
  return 'router-link'
})

function linkProps() {
  if (isExt.value || props.meta.newWindow) {
    let href = linkUrl.value
    if (typeof href === 'object') {
      href = href.path
    }
    return {
      href: href,
      target: '_blank',
      rel: 'noopener'
    }
  }
  return {
    to: props.to
  }
}
</script>
