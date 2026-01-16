# =========================
# Build stage
# =========================
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn clean install -DskipTests

# 3️⃣ Copy source code
COPY src ./src

# 4️⃣ Package the application
RUN mvn clean package -DskipTests

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:8-jre-alpine
WORKDIR /app

# Copy the packaged jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java","-jar","app.jar"]

