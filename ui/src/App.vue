<template>
  <div>
    <main-layout v-if="$route.path !== '/login'">
      <div id="app">
        <router-view/>
      </div>
    </main-layout>
    <div id="app" v-else>
      <router-view/>
    </div>
  </div>
</template>

<script>
import MainLayout from './components/MainLayout.vue'

export default {
  components: {MainLayout},
  name: 'app'
}
</script>

<style>
</style>
