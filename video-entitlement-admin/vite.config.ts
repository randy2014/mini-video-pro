import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  // 对外访问路径：https://xs2026.site/platform/
  // 网关（mini-novel-gateway）把 /platform/ 前缀剥掉后转发给 video-frontend，
  // 所以资源引用必须是 /platform/assets/...，剥前缀后才能在容器内对回 /assets/...
  base: '/platform/',
  server: {
    port: 3000,
    proxy: {
      '/admin/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
