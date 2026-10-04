FROM eclipse-temurin:26
RUN mkdir /opt/app
COPY target/*.jar /opt/app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "/opt/app/app.jar"]