import { RouteRecordRaw } from 'vue-router';

// Se utiliza la meta-propiedad "requiresAuth" para indicar rutas protegidas
// Se utiliza la meta-propiedad "roles" para indicar los roles autorizados (ej: ['ADMIN'])

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    // El layout principal normalmente requiere estar logueado
    meta: { requiresAuth: true },
    children: [
      { 
        path: '', 
        name: 'home',
        component: () => import('pages/IndexPage.vue') 
      },
      {
        path: 'inventario',
        name: 'inventario',
        component: () => import('pages/inventario/InventarioPage.vue'),
        // Ambas roles pueden ver el inventario (segun reglas del reto)
        meta: { requiresAuth: true, roles: ['ADMIN', 'EXTERNO'] }
      },
      {
        path: 'empresas',
        name: 'empresas',
        component: () => import('pages/empresa/EmpresaListPage.vue'),
        // Ambos roles pueden ver, pero la vista capa la edición si no es ADMIN
        meta: { requiresAuth: true, roles: ['ADMIN', 'EXTERNO'] }
      },
      {
        path: 'productos',
        name: 'productos',
        component: () => import('pages/producto/ProductoListPage.vue'),
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      }
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('pages/LoginPage.vue'),
    // Esta ruta no requiere autenticación
    meta: { requiresAuth: false }
  },
  {
    path: '/acceso-denegado',
    name: 'acceso-denegado',
    component: () => import('pages/Error403Page.vue'),
    meta: { requiresAuth: false }
  },

  // Catch-all: Página no encontrada (404)
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
