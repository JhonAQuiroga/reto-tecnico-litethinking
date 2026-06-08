import { route } from 'quasar/wrappers';
import {
  createMemoryHistory,
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from 'vue-router';
import routes from './routes';
import { useAuthStore } from '../stores/auth.store';

export default route(function ({ store }) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory);

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  // --------------------------------------------------------------------------
  // NAVIGATION GUARD GLOBAL
  // --------------------------------------------------------------------------
  Router.beforeEach((to, from, next) => {
    // Obtenemos el store de autenticación instanciado
    const authStore = useAuthStore(store as any);
    const isAuthenticated = authStore.isAuthenticated;
    
    // Verificamos si la ruta requiere estar autenticado
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    // Verificamos si la ruta exige un rol específico
    const requiredRoles = to.meta.roles as string[] | undefined;

    if (requiresAuth && !isAuthenticated) {
      // Si la ruta está protegida y no hay sesión activa -> Redirect al login
      next({ name: 'login' });
    } else if (requiresAuth && isAuthenticated && requiredRoles) {
      // Si la ruta está protegida, hay sesión, pero exige un rol específico...
      const userRole = authStore.user?.rol;
      
      if (userRole && requiredRoles.includes(userRole)) {
        // El usuario tiene un rol permitido
        next();
      } else {
        // El usuario NO tiene el rol requerido para esta ruta
        console.warn(`Intento de acceso denegado a ruta protegida (${to.path}). Rol requerido: ${requiredRoles}, Rol actual: ${userRole}`);
        next({ name: 'acceso-denegado' });
      }
    } else if (to.name === 'login' && isAuthenticated) {
      // Si el usuario ya está logueado y trata de ir al login -> Redirect al home
      next({ name: 'home' });
    } else {
      // Cualquier otro caso: permite la navegación (rutas públicas o sin restricción de rol)
      next();
    }
  });

  return Router;
});
