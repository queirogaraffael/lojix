FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app
COPY --from=build /app/target/supergestor-0.0.1-SNAPSHOT.jar /app/app.jar
RUN chown appuser:appgroup /app/app.jar
USER appuser
EXPOSE 8080
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]
