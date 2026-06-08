<template>
  <div class="fullscreen bg-red text-white text-center q-pa-md flex flex-center">
    <div>
      <div style="font-size: 20vh">
        403
      </div>
      <div class="text-h4" style="opacity:.8">
        Acceso Denegado
      </div>
      <div class="text-subtitle1 q-mt-md" style="opacity:.6">
        No tienes permisos suficientes para ver esta página.
      </div>

      <q-btn
        class="q-mt-xl"
        color="white"
        text-color="red"
        unelevated
        to="/"
        label="Volver al Inicio"
        no-caps
      />
    </div>
  </div>
</template>

<script setup lang="ts">
</script>
