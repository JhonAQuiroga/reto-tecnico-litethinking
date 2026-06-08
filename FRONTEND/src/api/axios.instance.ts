import axios, { AxiosError, AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import { useAuthStore } from '../stores/auth.store';

// Crear instancia de Axios con la URL base del backend
// Asegúrate de que coincida con el puerto donde corre tu Spring Boot (por defecto 8080)
const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000, // 10 segundos de timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Inyectar el token JWT en cada petición
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Obtenemos el store (asegurando de llamarlo dentro de este contexto para evitar errores de hidratación)
    const authStore = useAuthStore();
    
    // Si hay token, lo inyectamos en el header Authorization
    if (authStore.token && config.headers) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Manejo global de errores (401, 403, etc.)
api.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  async (error: AxiosError) => {
    const authStore = useAuthStore();

    if (error.response) {
      // 401: No autorizado (Token expirado, inválido o no provisto)
      if (error.response.status === 401) {
        console.warn('Acceso no autorizado - Token inválido o expirado');
        authStore.logout();
        
        // Redirigir al login si estamos en el cliente y no en SSR (dependiendo de la configuración de Quasar)
        if (typeof window !== 'undefined') {
          window.location.href = '/login'; 
        }
      }
      
      // 403: Prohibido (El usuario está logueado pero no tiene el rol necesario)
      if (error.response.status === 403) {
        console.warn('Acceso denegado - Privilegios insuficientes');
        // Opcional: Redirigir a una página de "No Autorizado" (403) o mostrar una notificación global
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
