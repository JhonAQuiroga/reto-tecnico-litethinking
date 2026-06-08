<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <h4 class="text-primary q-my-none">Catálogo de Productos</h4>
      
      <!-- Botón visible solo para ADMIN -->
      <q-btn
        v-if="authStore.isAdmin"
        color="primary"
        icon="add"
        label="Nuevo Producto"
        @click="openDialog()"
      />
    </div>

    <!-- Filtro Superior -->
    <q-card flat bordered class="q-mb-md bg-grey-1">
      <q-card-section class="row items-center q-gutter-md">
        <q-input
          v-model="empresaNitBusqueda"
          label="NIT de Empresa para filtrar"
          outlined
          dense
          class="col-grow"
          @keyup.enter="buscarPorEmpresa"
        >
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>

        <q-btn color="primary" label="Filtrar" @click="buscarPorEmpresa" :loading="loading" />
        <q-btn color="secondary" outline label="Ver Todos" @click="loadProductos" />
      </q-card-section>
    </q-card>

    <!-- Tabla de Productos -->
    <q-table
      :rows="productos"
      :columns="columns"
      row-key="codigo"
      :loading="loading"
      flat
      bordered
      class="bg-white"
    >
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
          <div class="text-h6">{{ isEditing ? 'Editar Producto' : 'Nuevo Producto' }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-form @submit="saveProducto" class="q-gutter-md">
            <q-input
              v-model="formData.codigo"
              label="Código de Producto *"
              :disable="isEditing"
              lazy-rules
              :rules="[val => val && val.length > 0 || 'El código es obligatorio']"
            />
            <q-input
              v-model="formData.nombre"
              label="Nombre del Producto *"
              lazy-rules
              :rules="[val => val && val.length > 0 || 'El nombre es obligatorio']"
            />
            <q-input
              v-model="formData.caracteristicas"
              label="Características"
              type="textarea"
            />
            <q-input
              v-model.number="formData.precioCop"
              label="Precio (COP) *"
              type="number"
              :rules="[val => val >= 0 || 'El precio no puede ser negativo']"
            />
            <q-input
              v-model.number="formData.precioUsd"
              label="Precio (USD) *"
              type="number"
              :rules="[val => val >= 0 || 'El precio no puede ser negativo']"
            />
            <q-input
              v-model.number="formData.precioEur"
              label="Precio (EUR) *"
              type="number"
              :rules="[val => val >= 0 || 'El precio no puede ser negativo']"
            />
            <q-input
              v-if="!isEditing"
              v-model.number="formData.stockInicial"
              label="Stock Inicial (Opcional)"
              type="number"
              :rules="[val => !val || val >= 0 || 'El stock inicial no puede ser negativo']"
            />
            <q-input
              v-model="formData.empresaNit"
              label="NIT Empresa Proveedora *"
              lazy-rules
              :rules="[val => val && val.length > 0 || 'El NIT de la empresa es obligatorio']"
            />
          </q-form>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn flat label="Guardar" @click="saveProducto" :loading="saving" />
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

interface Producto {
  codigo: string;
  nombre: string;
  caracteristicas: string;
  precioCop: number;
  precioUsd: number;
  precioEur: number;
  empresaNit: string;
  stockInicial?: number;
}

const productos = ref<Producto[]>([]);
const loading = ref(false);
const dialogVisible = ref(false);
const saving = ref(false);
const isEditing = ref(false);
const empresaNitBusqueda = ref('');

const formData = ref<Producto>({
  codigo: '',
  nombre: '',
  caracteristicas: '',
  precioCop: 0,
  precioUsd: 0,
  precioEur: 0,
  empresaNit: '',
  stockInicial: 0
});

const columns = computed(() => {
  const cols: any[] = [
    { name: 'codigo', required: true, label: 'Código', align: 'left', field: 'codigo', sortable: true },
    { name: 'nombre', align: 'left', label: 'Producto', field: 'nombre', sortable: true },
    { name: 'caracteristicas', align: 'left', label: 'Características', field: 'caracteristicas' },
    { name: 'precioCop', align: 'right', label: 'Precio COP', field: 'precioCop', format: (val: number) => val ? `$${val}` : 'N/A' },
    { name: 'precioUsd', align: 'right', label: 'Precio USD', field: 'precioUsd', format: (val: number) => val ? `$${val}` : 'N/A' },
    { name: 'precioEur', align: 'right', label: 'Precio EUR', field: 'precioEur', format: (val: number) => val ? `€${val}` : 'N/A' },
    { name: 'empresaNit', align: 'left', label: 'Empresa (NIT)', field: 'empresaNit', sortable: true }
  ];
  
  if (authStore.isAdmin) {
    cols.push({ name: 'acciones', align: 'center', label: 'Acciones', field: 'acciones' });
  }
  return cols;
});

const loadProductos = async () => {
  empresaNitBusqueda.value = '';
  loading.value = true;
  try {
    const response = await api.get<Producto[]>('/productos');
    productos.value = response.data;
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error cargando los productos' });
  } finally {
    loading.value = false;
  }
};

const buscarPorEmpresa = async () => {
  if (!empresaNitBusqueda.value.trim()) {
    return loadProductos();
  }
  loading.value = true;
  try {
    const response = await api.get<Producto[]>(`/productos/empresa/${empresaNitBusqueda.value}`);
    productos.value = response.data;
    if (productos.value.length === 0) {
      $q.notify({ color: 'info', message: 'No hay productos para esta empresa' });
    }
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error filtrando productos' });
  } finally {
    loading.value = false;
  }
};

const openDialog = (producto?: Producto) => {
  if (producto) {
    isEditing.value = true;
    formData.value = { ...producto };
  } else {
    isEditing.value = false;
    formData.value = { codigo: '', nombre: '', caracteristicas: '', precioCop: 0, precioUsd: 0, precioEur: 0, empresaNit: '', stockInicial: 0 };
  }
  dialogVisible.value = true;
};

const saveProducto = async () => {
  if (!formData.value.codigo || !formData.value.nombre || !formData.value.empresaNit) {
    $q.notify({ color: 'warning', message: 'Código, Nombre y NIT de empresa son obligatorios' });
    return;
  }

  saving.value = true;
  try {
    if (isEditing.value) {
      await api.put(`/productos/${formData.value.codigo}`, formData.value);
      $q.notify({ color: 'positive', message: 'Producto actualizado correctamente' });
    } else {
      await api.post('/productos', formData.value);
      $q.notify({ color: 'positive', message: 'Producto creado correctamente' });
    }
    dialogVisible.value = false;
    loadProductos();
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error al guardar el producto' });
  } finally {
    saving.value = false;
  }
};

const confirmDelete = (producto: Producto) => {
  $q.dialog({
    title: 'Confirmar Eliminación',
    message: `¿Estás seguro de eliminar el producto ${producto.nombre}?`,
    cancel: true,
    persistent: true
  }).onOk(async () => {
    try {
      await api.delete(`/productos/${producto.codigo}`);
      $q.notify({ color: 'positive', message: 'Producto eliminado' });
      loadProductos();
    } catch (error) {
      $q.notify({ color: 'negative', message: 'Error al eliminar el producto' });
    }
  });
};

onMounted(() => {
  loadProductos();
});
</script>
