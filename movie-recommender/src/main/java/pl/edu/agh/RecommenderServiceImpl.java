package pl.edu.agh;

import common.Movie;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import movierecommender.RecommenderRequest;
import movierecommender.RecommenderResponse;
import movierecommender.RecommenderServiceGrpc;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class RecommenderServiceImpl extends RecommenderServiceGrpc.RecommenderServiceImplBase {

    private static final Tracer tracer = TelemetryConfig.getTracer("RecommenderService");

    @Override
    public StreamObserver<RecommenderRequest> getRecommendedMovie(StreamObserver<RecommenderResponse> responseObserver) {
        Span span = tracer.spanBuilder("RecommenderService.getRecommendedMovie").startSpan();
        Scope scope = span.makeCurrent();

        return new StreamObserver<>() {
            final List<Movie> movies = new ArrayList<>();

            @Override
            public void onNext(RecommenderRequest request) {
                span.addEvent("Received movie",
                        io.opentelemetry.api.common.Attributes.of(io.opentelemetry.api.common.AttributeKey.stringKey("movie.title"), request.getMovie().getTitle()));
                movies.add(request.getMovie());
            }

            @Override
            public void onError(Throwable t) {
                span.setStatus(StatusCode.ERROR, "Error receiving movie list");
                span.recordException(t);
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Internal server error")
                        .asRuntimeException());
                span.end();
                scope.close();
            }

            @Override
            public void onCompleted() {
                if (movies.isEmpty()) {
                    span.setStatus(StatusCode.ERROR, "No movies received to recommend");
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Sorry, found no movies to recommend !")
                            .asRuntimeException());
                } else {
                    Movie recommended = findMovieForRecommendation(movies);
                    span.addEvent("Sending recommendation",
                            io.opentelemetry.api.common.Attributes.of(io.opentelemetry.api.common.AttributeKey.stringKey("recommended.title"), recommended.getTitle()));

                    responseObserver.onNext(RecommenderResponse.newBuilder()
                            .setMovie(recommended)
                            .build());
                    responseObserver.onCompleted();
                }
                span.end();
                scope.close();
            }
        };
    }

    private Movie findMovieForRecommendation(List<Movie> movies) {
        int random = new SecureRandom().nextInt(movies.size());
        return movies.get(random);
    }
}
