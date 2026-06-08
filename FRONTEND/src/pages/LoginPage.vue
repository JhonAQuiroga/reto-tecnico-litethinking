<template>
  <q-layout view="lHh Lpr lFf" class="bg-grey-2">
    <q-page-container>
      <q-page class="flex flex-center">
        <q-card class="q-pa-md shadow-2 my_card" bordered>
          <q-card-section class="text-center">
            <div class="text-h4 text-weight-bold text-primary q-mb-sm">LiteThinking</div>
            <div class="text-subtitle1 text-grey-8">Acceso Seguro</div>
          </q-card-section>

          <q-card-section>
            <q-form @submit="onSubmit" class="q-gutter-md">
              <q-input
                filled
                v-model="email"
                type="email"
                label="Correo Electrónico"
                lazy-rules
                :rules="[
                  val => val && val.length > 0 || 'Por favor ingresa tu correo',
                  val => /.+@.+\..+/.test(val) || 'Ingresa un correo válido'
                ]"
              >
                <template v-slot:prepend>
                  <q-icon name="email" />
                </template>
              </q-input>

              <q-input
                filled
                v-model="password"
                type="password"
                label="Contraseña"
                lazy-rules
                :rules="[val => val && val.length > 0 || 'Por favor ingresa tu contraseña']"
              >
                <template v-slot:prepend>
                  <q-icon name="lock" />
                </template>
              </q-input>

              <div>
                <q-btn
                  label="Iniciar Sesión"
                  type="submit"
                  color="primary"
                  class="full-width"
                  :loading="loading"
                  size="lg"
                />
              </div>
            </q-form>
          </q-card-section>

          <q-card-section class="text-center q-pt-none">
            <div class="text-grey-6 text-caption">
              Usa tus credenciales corporativas para acceder.
            </div>
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth.store';
import { useQuasar } from 'quasar';

const $q = useQuasar();
const router = useRouter();
const authStore = useAuthStore();

const email = ref('');
const password = ref('');
const loading = ref(false);

const onSubmit = async () => {
  loading.value = true;
  try {
    await authStore.login(email.value, password.value);
    
    $q.notify({
      color: 'positive',
      icon: 'check_circle',
      message: `¡Bienvenido ${authStore.user?.email}!`
    });

    // Redirigir al inicio después del login exitoso
    router.push({ name: 'home' });

  } catch (error) {
    $q.notify({
      color: 'negative',
      icon: 'error',
      message: 'Credenciales inválidas o error de conexión.'
    });
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.my_card {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
}
</style>
