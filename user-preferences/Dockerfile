FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/user-preferences-1.0-SNAPSHOT.jar ./user-preferences.jar
# optional: add a non-root user
USER 1000
ENTRYPOINT ["java","-jar","user-preferences.jar"]