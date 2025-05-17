package pl.edu.agh;

import common.Movie;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import movierecommender.RecommenderRequest;
import movierecommender.RecommenderResponse;
import movierecommender.RecommenderServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;

public class RecommenderServiceImpl extends RecommenderServiceGrpc.RecommenderServiceImplBase {
    @Override
    public StreamObserver<RecommenderRequest> getRecommendedMovie(StreamObserver<RecommenderResponse> responseObserver) {
        return new StreamObserver<>() {
            final List<Movie> movies = new ArrayList<>();

            public void onNext(RecommenderRequest request) {
                movies.add(request.getMovie());
            }

            public void onError(Throwable t) {
                responseObserver.onError(Status.INTERNAL
                        .withDescription("Internal server error")
                        .asRuntimeException());
            }

            public void onCompleted() {
                if (movies.size() > 0) {
                    responseObserver.onNext(RecommenderResponse.newBuilder()
                            .setMovie(findMovieForRecommendation(movies))
                            .build());
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription("Sorry, found no movies to recommend !")
                            .asRuntimeException());
                }
            }
        };
    }
    private Movie findMovieForRecommendation(List<Movie> movies) {
        int random = new SecureRandom().nextInt(movies.size());
        return movies.stream().skip(random).findAny()
                .orElse(null);
    }
}
