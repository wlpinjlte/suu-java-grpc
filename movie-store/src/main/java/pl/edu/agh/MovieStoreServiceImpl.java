package pl.edu.agh;

import common.Genre;
import common.Movie;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import moviestore.MovieStoreRequest;
import moviestore.MovieStoreResponse;
import moviestore.MovieStoreServiceGrpc;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MovieStoreServiceImpl extends MovieStoreServiceGrpc.MovieStoreServiceImplBase {

    private static final Tracer tracer = TelemetryConfig.getTracer("MovieStoreService");

    @Override
    public void getMovies(MovieStoreRequest request, StreamObserver<MovieStoreResponse> responseObserver) {
        Span span = tracer.spanBuilder("MovieStoreService.getMovies")
                .setAttribute("request.genre", request.getGenre().name())
                .startSpan();
        try (Scope scope = span.makeCurrent()) {
            List<Movie> allMovies = Arrays.asList(
                    Movie.newBuilder().setTitle("No country for old men").setDescription("Western crime thriller").setRating(8.1f).setGenre(Genre.ACTION).build(),
                    Movie.newBuilder().setTitle("Bourne Ultimatum").setDescription("Action thriller").setRating(8.0f).setGenre(Genre.ACTION).build(),
                    Movie.newBuilder().setTitle("The taxi driver").setDescription("Psychological thriller").setRating(8.2f).setGenre(Genre.THRILLER).build(),
                    Movie.newBuilder().setTitle("The Hangover").setDescription("Hilarious ride").setRating(7.7f).setGenre(Genre.COMEDY).build(),
                    Movie.newBuilder().setTitle("Raiders of the Lost Arc").setDescription("Expedition in search of the lost arc").setRating(8.4f).setGenre(Genre.ACTION).build(),
                    Movie.newBuilder().setTitle("Cast Away").setDescription("Survival story").setRating(7.8f).setGenre(Genre.DRAMA).build(),
                    Movie.newBuilder().setTitle("Gladiator").setDescription("Period drama").setRating(8.5f).setGenre(Genre.DRAMA).build(),
                    Movie.newBuilder().setTitle("Jaws").setDescription("Shark thrills").setRating(8.0f).setGenre(Genre.THRILLER).build(),
                    Movie.newBuilder().setTitle("Inception").setDescription("Sci-fi action").setRating(8.8f).setGenre(Genre.ACTION).build()
            );

            List<Movie> filteredMovies = allMovies.stream()
                    .filter(movie -> movie.getGenre().equals(request.getGenre()))
                    .collect(Collectors.toList());

            span.setAttribute("filtered.movie.count", filteredMovies.size());

            filteredMovies.forEach(movie -> {
                span.addEvent("Sending movie",
                        io.opentelemetry.api.common.Attributes.of(
                                io.opentelemetry.api.common.AttributeKey.stringKey("movie.title"), movie.getTitle()
                        ));
                responseObserver.onNext(MovieStoreResponse.newBuilder().setMovie(movie).build());
            });

            responseObserver.onCompleted();
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Exception while retrieving movies");
            span.recordException(e);
            responseObserver.onError(Status.INTERNAL.withDescription("Error fetching movies").asRuntimeException());
        } finally {
            span.end();
        }
    }
}
