package pl.edu.agh;

import common.Movie;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import userpreferences.UserPreferencesRequest;
import userpreferences.UserPreferencesResponse;
import userpreferences.UserPreferencesServiceGrpc;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.security.SecureRandom;

public class UserPreferencesServiceImpl extends UserPreferencesServiceGrpc.UserPreferencesServiceImplBase {

    private static final Tracer tracer = TelemetryConfig.getTracer("UserPreferencesService");

    @Override
    public StreamObserver<UserPreferencesRequest> getShortlistedMovies(StreamObserver<UserPreferencesResponse> responseObserver) {
        Span span = tracer.spanBuilder("UserPreferencesService.getShortlistedMovies")
                .startSpan();
        Scope scope = span.makeCurrent();

        return new StreamObserver<>() {
            @Override
            public void onNext(UserPreferencesRequest request) {
                try {
                    Movie movie = request.getMovie();
                    span.addEvent("Received movie", io.opentelemetry.api.common.Attributes.of(
                            io.opentelemetry.api.common.AttributeKey.stringKey("movie.title"), movie.getTitle()
                    ));

                    if (isEligible(movie)) {
                        responseObserver.onNext(UserPreferencesResponse.newBuilder()
                                .setMovie(movie)
                                .build());
                        span.addEvent("Movie shortlisted");
                    } else {
                        span.addEvent("Movie not shortlisted");
                    }
                } catch (Exception e) {
                    span.recordException(e);
                    span.setStatus(StatusCode.ERROR, "Error processing movie");
                    responseObserver.onError(Status.INTERNAL
                            .withDescription("Internal server error")
                            .asRuntimeException());
                }
            }

            @Override
            public void onError(Throwable t) {
                span.recordException(t);
                span.setStatus(StatusCode.ERROR, "Stream error");
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Internal server error")
                        .asRuntimeException());
                scope.close();
                span.end();
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
                scope.close();
                span.end();
            }
        };
    }

    private boolean isEligible(Movie movie) {
        return new SecureRandom().nextInt() % 4 != 0;
    }
}
