import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import api from '../api/axios.instance';

// Interfaz para definir la estructura del Usuario en el frontend
export interface UserInfo {
  email: string;
  rol: 'ADMIN' | 'EXTERNO';
  // Puedes agregar más campos si el backend los devuelve en el login (ej: nombre, empresaId, etc)
}

// Interfaz para la respuesta del endpoint de Login (/api/v1/auth/login)
interface AuthResponse {
  token: string;
  type: string;
  email: string;
  nombre: string;
  rol: 'ADMIN' | 'EXTERNO';
  expiresIn: number;
}

export const useAuthStore = defineStore('auth', () => {
  // --------------------------------------------------------------------------
  // ESTADO (State)
  // --------------------------------------------------------------------------
  
  // Inicializamos el token leyendo de localStorage (persistencia básica)
  const token = ref<string | null>(localStorage.getItem('token') || null);
  
  // Inicializamos el usuario leyendo de localStorage (parsing JSON)
  const getUserFromStorage = (): UserInfo | null => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch (e) {
      console.error('Error parsing user from localStorage', e);
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      return null;
    }
  };

  // Inicializamos el usuario leyendo de localStorage de forma segura
  const user = ref<UserInfo | null>(getUserFromStorage());

  // --------------------------------------------------------------------------
  // GETTERS (Computed properties)
  // --------------------------------------------------------------------------
  
  // Retorna true si hay un token, asumiendo que el usuario está logueado
  const isAuthenticated = computed(() => !!token.value);
  
  // Retorna true solo si el rol del usuario es ADMIN
  const isAdmin = computed(() => user.value?.rol === 'ADMIN');
  
  // Retorna true si el rol es EXTERNO
  const isExterno = computed(() => user.value?.rol === 'EXTERNO');

  // --------------------------------------------------------------------------
  // ACCIONES (Actions)
  // --------------------------------------------------------------------------
  
  /**
   * Acción para iniciar sesión. Llama a la API, recibe el token y rol, 
   * actualiza el estado y persiste en localStorage.
   */
  const login = async (email: string, password: string): Promise<void> => {
    try {
      const response = await api.post<AuthResponse>('/auth/login', {
        email,
        password,
      });

      // Extraer datos de la respuesta
      const data = response.data;
      const newToken = data.token;
      
      const newUser: UserInfo = {
        email: data.email,
        rol: data.rol
      };

      // Actualizar el estado (Store)
      token.value = newToken;
      user.value = newUser;

      // Persistir en LocalStorage para mantener la sesión al recargar la página
      localStorage.setItem('token', newToken);
      localStorage.setItem('user', JSON.stringify(newUser));

    } catch (error) {
      console.error('Error durante el inicio de sesión', error);
      // Limpiar estado en caso de error (medida de seguridad)
      logout();
      throw error; // Re-lanzar para que el componente (ej. Login.vue) pueda mostrar un mensaje de error
    }
  };

  /**
   * Acción para cerrar sesión. Limpia el estado y el LocalStorage.
   */
  const logout = (): void => {
    // Limpiar estado
    token.value = null;
    user.value = null;

    // Remover del LocalStorage
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  // Exponer el estado y las acciones para ser consumidos en los componentes
  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    isExterno,
    login,
    logout
  };
});
