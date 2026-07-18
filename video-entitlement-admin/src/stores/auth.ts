import { create } from 'zustand';

interface AuthState {
  token: string | null;
  username: string | null;
  permissions: string[];
  setAuth: (token: string, username: string, permissions: string[]) => void;
  clearAuth: () => void;
  isLoggedIn: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  token: localStorage.getItem('adminToken'),
  username: localStorage.getItem('adminUser'),
  permissions: [],
  setAuth: (token, username, permissions) => {
    localStorage.setItem('adminToken', token);
    localStorage.setItem('adminUser', username);
    set({ token, username, permissions });
  },
  clearAuth: () => {
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
    set({ token: null, username: null, permissions: [] });
  },
  isLoggedIn: () => !!get().token,
}));
