# Use a modern OpenJDK image
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Copy the compiled jar into the container
COPY target/*.jar app.jar
# Expose the tracking service port
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]