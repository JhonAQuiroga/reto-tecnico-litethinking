import { describe, it, expect, vi, beforeEach } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import LoginPage from './LoginPage.vue';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../stores/auth.store';

vi.mock('quasar', async (importOriginal) => {
  const actual = await importOriginal<typeof import('quasar')>();
  return {
    ...actual,
    useQuasar: () => ({
      notify: vi.fn()
    })
  };
});

const mockRouterPush = vi.fn();
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockRouterPush
  })
}));

describe('LoginPage.vue', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it('debería inicializar con campos vacíos', () => {
    const wrapper = shallowMount(LoginPage);
    expect((wrapper.vm as any).email).toBe('');
    expect((wrapper.vm as any).password).toBe('');
  });

  it('debería llamar a login y redirigir al éxito', async () => {
    const wrapper = shallowMount(LoginPage);
    const authStore = useAuthStore();
    
    // Mockeamos la acción de login del store
    vi.spyOn(authStore, 'login').mockResolvedValue(undefined);

    (wrapper.vm as any).email = 'test@litethinking.com';
    (wrapper.vm as any).password = '123456';

    await (wrapper.vm as any).onSubmit();

    expect(authStore.login).toHaveBeenCalledWith('test@litethinking.com', '123456');
    expect(mockRouterPush).toHaveBeenCalledWith({ name: 'home' });
  });
});
