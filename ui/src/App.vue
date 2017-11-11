<template>
  <main-layout>
    <div id="app">
      <router-view/>
    </div>
  </main-layout>
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
