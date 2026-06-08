<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <h4 class="text-primary q-my-none">Directorio de Empresas</h4>
      
      <!-- Botón visible solo para ADMIN -->
      <q-btn
        v-if="authStore.isAdmin"
        color="primary"
        icon="add"
        label="Nueva Empresa"
        @click="openDialog()"
      />
    </div>

    <!-- Tabla de Empresas -->
    <q-table
      :rows="empresas"
      :columns="columns"
      row-key="nit"
      :loading="loading"
      flat
      bordered
      class="bg-white"
    >
      <template v-slot:body-cell-activa="props">
        <q-td :props="props">
          <q-chip
            :color="props.row.activa ? 'positive' : 'negative'"
            text-color="white"
            dense
            size="sm"
          >
            {{ props.row.activa ? 'ACTIVA' : 'INACTIVA' }}
          </q-chip>
        </q-td>
      </template>

      <!-- Columna de Acciones visible solo para ADMIN -->
      <template v-slot:body-cell-acciones="props" v-if="authStore.isAdmin">
        <q-td :props="props">
          <q-btn flat round dense color="primary" icon="edit" @click="openDialog(props.row)">
            <q-tooltip>Editar</q-tooltip>
          </q-btn>
          <q-btn flat round dense color="negative" icon="delete" @click="confirmDelete(props.row)">
            <q-tooltip>Eliminar</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <!-- Diálogo de Creación/Edición -->
    <q-dialog v-model="dialogVisible" persistent>
      <q-card style="min-width: 350px">
        <q-card-section>
          <div class="text-h6">{{ isEditing ? 'Editar Empresa' : 'Nueva Empresa' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveEmpresa" class="q-gutter-md">
            <q-input
              v-model="formData.nit"
              label="NIT *"
              :disable="isEditing"
              lazy-rules
              :rules="[val => val && val.length > 0 || 'El NIT es obligatorio']"
            />
            <q-input
              v-model="formData.nombre"
              label="Nombre *"
              lazy-rules
              :rules="[val => val && val.length > 0 || 'El nombre es obligatorio']"
            />
            <q-input
              v-model="formData.direccion"
              label="Dirección"
            />
            <q-input
              v-model="formData.telefono"
              label="Teléfono"
            />
            
            <q-toggle
              v-model="formData.activa"
              label="Empresa Activa"
              color="primary"
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn flat label="Guardar" @click="saveEmpresa" :loading="saving" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useQuasar } from 'quasar';
import api from '../../api/axios.instance';
import { useAuthStore } from '../../stores/auth.store';

const $q = useQuasar();
const authStore = useAuthStore();

interface Empresa {
  nit: string;
  nombre: string;
  direccion: string;
  telefono: string;
  activa: boolean;
}

const empresas = ref<Empresa[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saving = ref(false);
const isEditing = ref(false);

const formData = ref<Empresa>({
  nit: '',
  nombre: '',
  direccion: '',
  telefono: '',
  activa: true
});

// Definición dinámica de columnas (solo mostrar acciones si es ADMIN)
const columns = computed(() => {
  const cols: any[] = [
    { name: 'nit', required: true, label: 'NIT', align: 'left', field: 'nit', sortable: true },
    { name: 'nombre', align: 'left', label: 'Razón Social', field: 'nombre', sortable: true },
    { name: 'direccion', align: 'left', label: 'Dirección', field: 'direccion' },
    { name: 'telefono', align: 'left', label: 'Teléfono', field: 'telefono' },
    { name: 'activa', align: 'center', label: 'Estado', field: 'activa', sortable: true }
  ];
  
  if (authStore.isAdmin) {
    cols.push({ name: 'acciones', align: 'center', label: 'Acciones', field: 'acciones' });
  }
  return cols;
});

const loadEmpresas = async () => {
  loading.value = true;
  try {
    const response = await api.get<Empresa[]>('/empresas');
    empresas.value = response.data;
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error cargando las empresas' });
  } finally {
    loading.value = false;
  }
};

const openDialog = (empresa?: Empresa) => {
  if (empresa) {
    isEditing.value = true;
    formData.value = { ...empresa };
  } else {
    isEditing.value = false;
    formData.value = { nit: '', nombre: '', direccion: '', telefono: '', activa: true };
  }
  dialogVisible.value = true;
};

const saveEmpresa = async () => {
  if (!formData.value.nit || !formData.value.nombre) {
    $q.notify({ color: 'warning', message: 'NIT y Nombre son obligatorios' });
    return;
  }

  saving.value = true;
  try {
    if (isEditing.value) {
      await api.put(`/empresas/${formData.value.nit}`, formData.value);
      $q.notify({ color: 'positive', message: 'Empresa actualizada correctamente' });
    } else {
      await api.post('/empresas', formData.value);
      $q.notify({ color: 'positive', message: 'Empresa creada correctamente' });
    }
    dialogVisible.value = false;
    loadEmpresas();
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error al guardar la empresa' });
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (empresa: Empresa) => {
  $q.dialog({
    title: 'Confirmar Eliminación',
    message: `¿Estás seguro de eliminar la empresa ${empresa.nombre}?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await api.delete(`/empresas/${empresa.nit}`);
      $q.notify({ color: 'positive', message: 'Empresa eliminada' });
      loadEmpresas();
    } catch (error) {
      $q.notify({ color: 'negative', message: 'Error al eliminar la empresa' });
    }
  });
};

onMounted(() => {
  loadEmpresas();
});
</script>
