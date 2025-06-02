# Use OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY . .

# Package the app using the Maven wrapper
RUN ./mvnw clean package -DskipTests

# Expose the port your Spring Boot app runs on
EXPOSE 8080

# Set Google credentials path for runtime
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/src/main/resources/credentials.json

# Run the Spring Boot app
CMD ["java", "-jar", "target/ChurchVisitorApp-0.0.1-SNAPSHOT.jar"]