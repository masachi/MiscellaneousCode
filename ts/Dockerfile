FROM nginx:alpine

COPY ./dist/ /usr/share/nginx/html/
COPY ./unidmrweb.conf /etc/nginx/conf.d/unidmrweb.conf
RUN sed -i 's/ip:.*/ip: "\/",/'  /usr/share/nginx/html/config.js
RUN sed -i 's/port:.*/port: "10011",/'  /usr/share/nginx/html/config.js
