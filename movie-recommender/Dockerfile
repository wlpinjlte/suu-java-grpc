FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/movie-recommender-1.0-SNAPSHOT.jar ./movie-recommender.jar
# optional: add a non-root user
USER 1000
ENTRYPOINT ["java","-jar","movie-recommender.jar"]