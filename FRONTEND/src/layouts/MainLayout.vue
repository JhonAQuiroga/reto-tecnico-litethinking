<template>
  <q-layout view="lHh Lpr lFf">
    <!-- BARRA SUPERIOR -->
    <q-header elevated class="bg-primary text-white">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          LiteThinking - Portal de Gestión
        </q-toolbar-title>

        <!-- Información de usuario y botón de logout -->
        <div v-if="authStore.isAuthenticated" class="row items-center q-gutter-md">
          <div class="text-subtitle2">
            {{ authStore.user?.email }}
            <q-badge color="secondary" align="top" class="q-ml-sm">
              {{ authStore.user?.rol }}
            </q-badge>
          </div>
          <q-btn flat dense icon="logout" label="Salir" @click="handleLogout" />
        </div>
      </q-toolbar>
    </q-header>

    <!-- SIDEBAR (MENU LATERAL) -->
    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      bordered
      class="bg-grey-1"
    >
      <q-list>
        <q-item-label header>
          Menú Principal
        </q-item-label>

        <!-- RUTA: INICIO (Disponible para todos) -->
        <q-item clickable v-ripple to="/" exact>
          <q-item-section avatar>
            <q-icon name="home" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Inicio</q-item-label>
          </q-item-section>
        </q-item>

        <!-- RUTA: EMPRESAS (Disponible para todos, mutación en el componente) -->
        <q-item clickable v-ripple to="/empresas">
          <q-item-section avatar>
            <q-icon name="business" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Empresas</q-item-label>
            <q-item-label caption v-if="authStore.isExterno">Modo Lectura</q-item-label>
          </q-item-section>
        </q-item>

        <!-- RUTA: PRODUCTOS (Solo ADMIN) -->
        <q-item v-if="authStore.isAdmin" clickable v-ripple to="/productos">
          <q-item-section avatar>
            <q-icon name="category" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Productos</q-item-label>
            <q-item-label caption>Catálogo General</q-item-label>
          </q-item-section>
        </q-item>

        <!-- RUTA: INVENTARIO (Disponible para todos) -->
        <q-item clickable v-ripple to="/inventario">
          <q-item-section avatar>
            <q-icon name="inventory" />
          </q-item-section>
          <q-item-section>
            <q-item-label>Inventario</q-item-label>
            <q-item-label caption>Stock y Reportes</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-drawer>

    <!-- CONTENEDOR PRINCIPAL DE PÁGINAS -->
    <q-page-container>
      <!-- Transition para suavizar la carga entre páginas -->
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth.store';

const leftDrawerOpen = ref(false);
const authStore = useAuthStore();
const router = useRouter();

const toggleLeftDrawer = () => {
  leftDrawerOpen.value = !leftDrawerOpen.value;
};

const handleLogout = () => {
  authStore.logout();
  router.push({ name: 'login' });
};
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
