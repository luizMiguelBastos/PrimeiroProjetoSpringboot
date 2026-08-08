# ETAPA 1: Construção (Build)
# Voltamos a usar a imagem pura e oficial do Java 25
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app


COPY . .


RUN chmod +x mvnw


RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:25-jdk
WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]