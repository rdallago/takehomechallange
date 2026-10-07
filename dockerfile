# --- Etapa 1: compilar ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
# Los tests no corren aca: necesitan una BD y se ejecutan en CircleCI
RUN mvn clean package -DskipTests -B

# --- Etapa 2: ejecutar ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Usuario sin privilegios: no corremos como root
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/target/*.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]