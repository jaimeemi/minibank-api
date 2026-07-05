# ==========================================
# Etapa 1: Build de la aplicación
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos el archivo de configuración de dependencias
COPY pom.xml .

# Descargamos las dependencias en cache para no repetir el proceso si el pom no cambia
RUN mvn dependency:go-offline -B

# Copiamos el código fuente de la API
COPY src ./src

# Compilamos y generamos el JAR omitiendo los tests (ya los validamos localmente)
RUN mvn clean package -DskipTests
#------------------------------------------------
# ==========================================
# Etapa 2: Entorno de ejecucion (Runtime)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Creamos un usuario de sistema sin privilegios por buenas prácticas de seguridad
RUN addgroup -S minibank && adduser -S minibank -G minibank

# Copiamos el artefacto compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Cambiamos la propiedad de los archivos al usuario sin privilegios
RUN chown -R minibank:minibank /app

# Cambiamos al usuario sin privilegios
USER minibank

# Exponemos el puerto configurado en el application.yml
EXPOSE 9000

# Comando óptimo para ejecutar la aplicación dentro del contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]

LABEL authors="emel"

