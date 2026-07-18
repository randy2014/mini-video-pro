FROM nginx:1.27-alpine

COPY dist /usr/share/nginx/html
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost/ || exit 1
