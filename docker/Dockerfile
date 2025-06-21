# Usar una imagen base con Java
FROM openjdk:17-jdk-alpine

# Copiar el archivo jar al contenedor
COPY Arteum-0.0.1-SNAPSHOT.jar arteum.jar

# Exponer el puerto que usamos en Spring Boot
EXPOSE 443

# Comando para ejecutar la aplicación
ENTRYPOINT ["java","-jar","/arteum.jar"]
