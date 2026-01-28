# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime Environment
FROM openjdk:27-ea-trixie
WORKDIR /app
# Copy the built jar from the first stage
COPY --from=build /app/target/*.jar flashsale-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]