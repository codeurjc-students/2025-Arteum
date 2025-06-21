#!/bin/bash

# 1. Construye la imagen Docker de tu app
docker build -t etentor2021/arteum:latest .

# 2. Subir la imagen a Docker Hub
docker push etentor2021/arteum:latest

# 3. Levantar la app y la base de datos usando Docker Compose
docker-compose up --build