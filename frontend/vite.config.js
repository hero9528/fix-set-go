import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    allowedHosts: ['localhost', '127.0.0.1', '.trycloudflare.com'],
  },
  preview: {
    host: true,
    port: Number(process.env.PORT ?? 4173),
    strictPort: true,
  },
});
