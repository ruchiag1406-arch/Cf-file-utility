# Use lightweight Java 8 runtime
FROM eclipse-temurin:8-jdk-alpine

# Create app directory
WORKDIR /app

# Copy jar into container
COPY target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
