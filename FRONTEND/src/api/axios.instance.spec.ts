import { describe, it, expect, vi, beforeEach } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';
import api from './axios.instance';
import { useAuthStore } from '../stores/auth.store';

describe('Axios Interceptors', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    localStorage.clear();
  });

  it('debería inyectar el token en la cabecera si el usuario está logueado', async () => {
    const authStore = useAuthStore();
    const mockToken = 'test-jwt-token';
    authStore.token = mockToken;

    // Simulamos una petición a Axios a través de un mock del adaptador interno
    // Pero la forma más fácil de probar el interceptor es llamando a la función onFulfilled directamente
    const requestInterceptor = (api.interceptors.request as any).handlers[0].fulfilled;
    
    const config = { headers: {} };
    const resultConfig = await requestInterceptor(config);

    expect(resultConfig.headers.Authorization).toBe(`Bearer ${mockToken}`);
  });

  it('NO debería inyectar el token si el usuario NO está logueado', async () => {
    const authStore = useAuthStore();
    authStore.token = null;

    const requestInterceptor = (api.interceptors.request as any).handlers[0].fulfilled;
    
    const config = { headers: {} };
    const resultConfig = await requestInterceptor(config);

    expect(resultConfig.headers.Authorization).toBeUndefined();
  });

  it('debería hacer logout si recibe un error 401', async () => {
    const authStore = useAuthStore();
    authStore.token = 'old-token'; // Fake logueado
    
    const responseInterceptorError = (api.interceptors.response as any).handlers[0].rejected;
    
    const errorObject = {
      response: {
        status: 401
      }
    };

    try {
      await responseInterceptorError(errorObject);
    } catch (e) {
      // Ignorar el rechazo de la promesa, lo importante es comprobar el efecto secundario
    }

    // Comprobar que el interceptor limpió el estado llamando a logout()
    expect(authStore.token).toBeNull();
  });
});
