FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
# Expose the notification service port
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app.jar"]