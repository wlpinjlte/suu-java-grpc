FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/movie-store-1.0-SNAPSHOT.jar ./movie-store.jar
# optional: add a non-root user
USER 1000
ENTRYPOINT ["java","-jar","movie-store.jar"]