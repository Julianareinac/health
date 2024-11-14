# Usa una imagen base de Java
FROM openjdk:17

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR de la aplicación en el contenedor
COPY target/health-0.0.1-SNAPSHOT.jar health.jar

# Exponer el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Comando por defecto para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "health.jar"]