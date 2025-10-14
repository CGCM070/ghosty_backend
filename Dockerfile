# ==========================================
# Etapa 1: Build
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Hacer mvnw ejecutable
RUN chmod +x mvnw

# Descargar dependencias (esta capa se cachea)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar y empaquetar (sin tests)
RUN ./mvnw clean package -DskipTests

# ==========================================
# Etapa 2: Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR compilado desde la etapa build
COPY --from=build /app/target/ghosty-0.0.1-SNAPSHOT.jar app.jar

# Cambiar ownership al usuario no-root
RUN chown -R appuser:appgroup /app

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE 8080

# Variable de entorno para profile de Spring
ENV SPRING_PROFILES_ACTIVE=prod

# Health check (opcional, útil para Render)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", \
    "/app/app.jar"]
