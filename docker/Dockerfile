# Usa una imagen base con Java
FROM openjdk:17-jdk-alpine

# Copia el archivo jar al contenedor
COPY Arteum-0.0.1-SNAPSHOT.jar arteum.jar

# Expone el puerto que usa Spring Boot (por defecto 8080)
EXPOSE 443

# Comando para ejecutar la app
ENTRYPOINT ["java","-jar","/arteum.jar"]
