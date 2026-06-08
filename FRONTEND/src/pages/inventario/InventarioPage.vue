<template>
  <q-page padding>
    <div class="row items-center justify-between q-mb-md">
      <h4 class="text-primary q-my-none">Inventario Consolidado</h4>
    </div>

    <!-- Filtro Superior -->
    <q-card flat bordered class="q-mb-md bg-grey-1">
      <q-card-section class="row items-center q-gutter-md">
        <q-input
          v-model="nitBusqueda"
          label="NIT de Empresa"
          outlined
          dense
          class="col-grow"
          @keyup.enter="buscarPorNit"
        >
          <template v-slot:append>
            <q-icon name="search" />
          </template>
        </q-input>

        <q-btn color="primary" label="Buscar" @click="buscarPorNit" :loading="loading" />
        <q-btn color="secondary" outline label="Ver Todos" @click="loadInventarioGlobal" />
        
        <q-space />
        
        <!-- Botones de Reporte (Disponibles para ADMIN) -->
        <q-btn
          v-if="authStore.isAdmin"
          color="accent"
          icon="picture_as_pdf"
          label="Descargar PDF"
          @click="descargarPdf"
          :loading="downloading"
          :disable="!nitBusqueda"
        >
          <q-tooltip v-if="!nitBusqueda">Primero busca un NIT de Empresa</q-tooltip>
        </q-btn>
        
        <q-btn
          v-if="authStore.isAdmin"
          color="positive"
          icon="mail"
          label="Enviar Correo"
          @click="emailDialog = true"
          :disable="!nitBusqueda"
        >
          <q-tooltip v-if="!nitBusqueda">Primero busca un NIT de Empresa</q-tooltip>
        </q-btn>
      </q-card-section>
    </q-card>

    <!-- Tabla de Inventario -->
    <q-table
      :rows="inventario"
      :columns="columns"
      row-key="productoCodigo"
      :loading="loading"
      flat
      bordered
      class="bg-white"
    >
      <template v-slot:body-cell-cantidad="props">
        <q-td :props="props">
          <q-badge :color="props.row.cantidad > 10 ? 'positive' : (props.row.cantidad > 0 ? 'warning' : 'negative')">
            {{ props.row.cantidad }} unidades
          </q-badge>
        </q-td>
      </template>

      <!-- Columna de Acciones visible solo para ADMIN -->
      <template v-slot:body-cell-acciones="props" v-if="authStore.isAdmin">
        <q-td :props="props">
          <q-btn flat round dense color="primary" icon="edit" @click="editarStock(props.row)">
            <q-tooltip>Ajustar Stock</q-tooltip>
          </q-btn>
        </q-td>
      </template>
    </q-table>

    <!-- Dialogo para enviar PDF por correo -->
    <q-dialog v-model="emailDialog">
      <q-card style="min-width: 350px">
        <q-card-section>
          <div class="text-h6">Enviar Reporte PDF</div>
          <div class="text-caption">Empresa NIT: {{ nitBusqueda }}</div>
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            v-model="emailDestino"
            label="Correo Destino *"
            type="email"
            autofocus
            :rules="[val => !!val || 'El correo es requerido', val => /.+@.+\..+/.test(val) || 'Correo inválido']"
          />
          <q-input
            v-model="emailAsunto"
            label="Asunto"
            class="q-mt-md"
          />
          <q-input
            v-model="emailMensaje"
            label="Mensaje Adicional"
            type="textarea"
            class="q-mt-md"
          />
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="Cancelar" v-close-popup />
          <q-btn flat label="Enviar" @click="enviarPorCorreo" :loading="sending" />
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

interface InventarioItem {
  empresaNit: string;
  empresaNombre?: string;
  productoCodigo: string;
  productoNombre: string;
  cantidad: number;
  precioCop?: number;
  precioUsd?: number;
  precioEur?: number;
}

const inventario = ref<InventarioItem[]>([]);
const loading = ref(false);
const downloading = ref(false);

const nitBusqueda = ref('');

// Dialogo de email
const emailDialog = ref(false);
const emailDestino = ref('');
const emailAsunto = ref('Reporte de Inventario LiteThinking');
const emailMensaje = ref('');
const sending = ref(false);

const columns = computed(() => {
  const cols: any[] = [
    { name: 'empresaNit', align: 'left', label: 'NIT Empresa', field: 'empresaNit', sortable: true },
    { name: 'productoCodigo', align: 'left', label: 'Cód. Producto', field: 'productoCodigo', sortable: true },
    { name: 'productoNombre', align: 'left', label: 'Producto', field: 'productoNombre', sortable: true },
    { name: 'cantidad', align: 'center', label: 'Stock Actual', field: 'cantidad', sortable: true },
    { name: 'precioCop', align: 'right', label: 'Precio (COP)', field: 'precioCop', format: (val: number) => val ? `$${val}` : 'N/A' },
    { name: 'precioUsd', align: 'right', label: 'Precio (USD)', field: 'precioUsd', format: (val: number) => val ? `$${val}` : 'N/A' },
    { name: 'precioEur', align: 'right', label: 'Precio (EUR)', field: 'precioEur', format: (val: number) => val ? `€${val}` : 'N/A' },
  ];
  
  if (authStore.isAdmin) {
    cols.push({ name: 'acciones', align: 'center', label: 'Acciones', field: 'acciones' });
  }
  return cols;
});

const loadInventarioGlobal = async () => {
  nitBusqueda.value = '';
  loading.value = true;
  try {
    const response = await api.get<InventarioItem[]>('/inventario');
    inventario.value = response.data;
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error cargando inventario' });
  } finally {
    loading.value = false;
  }
};

const buscarPorNit = async () => {
  if (!nitBusqueda.value.trim()) {
    return loadInventarioGlobal();
  }
  
  loading.value = true;
  try {
    const response = await api.get<InventarioItem[]>(`/inventario/empresa/${nitBusqueda.value}`);
    inventario.value = response.data;
    
    if (inventario.value.length === 0) {
      $q.notify({ color: 'info', message: 'No hay inventario para esta empresa' });
    }
  } catch (error: any) {
    if (error.response?.status === 404) {
      $q.notify({ color: 'warning', message: 'Empresa no encontrada' });
    } else {
      $q.notify({ color: 'negative', message: 'Error consultando inventario' });
    }
  } finally {
    loading.value = false;
  }
};

const descargarPdf = async () => {
  if (!nitBusqueda.value) return;
  
  downloading.value = true;
  try {
    // Solicitamos el archivo como blob
    const response = await api.get(`/inventario/empresa/${nitBusqueda.value}/pdf`, {
      responseType: 'blob'
    });
    
    // Crear objeto URL para el blob nativo y forzar descarga
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `inventario-${nitBusqueda.value}.pdf`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    $q.notify({ color: 'positive', icon: 'file_download', message: 'Descarga completada' });
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error generando PDF' });
  } finally {
    downloading.value = false;
  }
};

const enviarPorCorreo = async () => {
  if (!emailDestino.value || !nitBusqueda.value) return;
  
  sending.value = true;
  try {
    await api.post('/inventario/enviar-pdf', {
      empresaNit: nitBusqueda.value,
      destinatario: emailDestino.value,
      asunto: emailAsunto.value,
      mensajeAdicional: emailMensaje.value
    });
    
    $q.notify({ color: 'positive', icon: 'mail', message: `Reporte enviado a ${emailDestino.value}` });
    emailDialog.value = false;
  } catch (error) {
    $q.notify({ color: 'negative', message: 'Error enviando el correo' });
  } finally {
    sending.value = false;
  }
};

const editarStock = (item: InventarioItem) => {
  $q.dialog({
    title: 'Ajustar Stock',
    message: `Ingrese el nuevo stock para el producto ${item.productoNombre}:`,
    prompt: {
      model: String(item.cantidad),
      type: 'number'
    },
    cancel: true,
    persistent: true
  }).onOk(async (data) => {
    const nuevoStock = parseInt(data);
    if (isNaN(nuevoStock) || nuevoStock < 0) {
      $q.notify({ color: 'warning', message: 'El stock no puede ser negativo' });
      return;
    }
    
    try {
      await api.put(`/inventario/empresa/${item.empresaNit}/producto/${item.productoCodigo}`, {
        cantidad: nuevoStock
      });
      $q.notify({ color: 'positive', message: 'Stock actualizado correctamente' });
      
      // Recargar la data
      if (nitBusqueda.value) {
        buscarPorNit();
      } else {
        loadInventarioGlobal();
      }
    } catch (error) {
      $q.notify({ color: 'negative', message: 'Error actualizando el stock' });
    }
  });
};

onMounted(() => {
  loadInventarioGlobal();
});
</script>
