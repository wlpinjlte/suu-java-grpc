FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/movie-finder-client-1.0-SNAPSHOT.jar ./movie-finder-client.jar
# optional: add a non-root user
USER 1000
ENTRYPOINT ["java","-jar","movie-finder-client.jar"]