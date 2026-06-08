import { describe, it, expect, beforeEach, vi } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';
import { useAuthStore } from './auth.store';
import api from '../api/axios.instance';

describe('Auth Store', () => {
  beforeEach(() => {
    // Inicializar un estado de Pinia limpio antes de cada test
    setActivePinia(createPinia());
    // Limpiar localStorage
    localStorage.clear();
    // Limpiar mocks
    vi.clearAllMocks();
  });

  it('debería inicializarse con estado desconectado', () => {
    const authStore = useAuthStore();
    expect(authStore.token).toBeNull();
    expect(authStore.user).toBeNull();
    expect(authStore.isAuthenticated).toBe(false);
    expect(authStore.isAdmin).toBe(false);
  });

  it('debería hacer login y guardar el token', async () => {
    const authStore = useAuthStore();
    const fakeToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.fake.token';
    const fakeEmail = 'admin@litethinking.com';
    
    // Burlar (Mock) la llamada HTTP de Axios
    vi.spyOn(api, 'post').mockResolvedValue({
      data: {
        token: fakeToken,
        type: 'Bearer',
        email: fakeEmail,
        nombre: 'Admin Test',
        rol: 'ADMIN',
        expiresIn: 3600
      }
    });

    await authStore.login(fakeEmail, '123456');

    expect(authStore.token).toBe(fakeToken);
    expect(authStore.user?.email).toBe(fakeEmail);
    expect(authStore.user?.rol).toBe('ADMIN');
    expect(authStore.isAuthenticated).toBe(true);
    expect(authStore.isAdmin).toBe(true);
    
    // Verificar que también se guardó en localStorage
    expect(localStorage.getItem('token')).toBe(fakeToken);
    expect(JSON.parse(localStorage.getItem('user') || '{}').rol).toBe('ADMIN');
  });

  it('debería limpiar el estado al hacer logout', async () => {
    const authStore = useAuthStore();
    
    // Configurar estado inicial logueado
    vi.spyOn(api, 'post').mockResolvedValue({
      data: { token: 'fake-token', email: 'user@test.com', rol: 'EXTERNO' }
    });
    await authStore.login('user@test.com', '123456');
    expect(authStore.isAuthenticated).toBe(true);

    // Ejecutar logout
    authStore.logout();

    // Verificar limpieza
    expect(authStore.token).toBeNull();
    expect(authStore.user).toBeNull();
    expect(authStore.isAuthenticated).toBe(false);
    expect(authStore.isAdmin).toBe(false);
    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });
});
