#
# Use some tag e.g. v1 to build the image otherwise kubernetes will allways pull from 
# public repository (no tag given = :latest)!
#
# docker build -f docker/angular/Dockerfile -t hackathon-angular:v1 .
# docker run --rm --name hackathon-angular -p 8090:80 angular-radial-menu:v1
#
FROM node:12.3.1-alpine AS builder
COPY angular/ ./angular
WORKDIR /angular
RUN npm i
RUN $(npm bin)/ng build --prod

FROM nginx:1.15.8-alpine
RUN rm /etc/nginx/conf.d/default.conf
WORKDIR /
#Copy custom nginx conf -> proxy for helidon microservice
COPY ./docker/angular/nginx.conf /etc/nginx/
# Copy Angular
COPY --from=builder target/angular/angular-radial-menu /usr/share/nginx/html