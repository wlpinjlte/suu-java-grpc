package pl.edu.agh;

import common.Movie;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import userpreferences.UserPreferencesRequest;
import userpreferences.UserPreferencesResponse;
import userpreferences.UserPreferencesServiceGrpc;

import java.security.SecureRandom;

public class UserPreferencesServiceImpl extends UserPreferencesServiceGrpc.UserPreferencesServiceImplBase {

    @Override
    public StreamObserver<UserPreferencesRequest> getShortlistedMovies(StreamObserver<UserPreferencesResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(UserPreferencesRequest request) {
                if (isEligible(request.getMovie())) {
                    responseObserver.onNext(UserPreferencesResponse.newBuilder()
                            .setMovie(request.getMovie())
                            .build());
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Internal server error")
                        .asRuntimeException());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    private boolean isEligible(Movie movie) {
        return new SecureRandom().nextInt() % 4 != 0;
    }
}
