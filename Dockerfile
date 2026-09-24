FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests


# Etapa 2: Ejecución
FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]