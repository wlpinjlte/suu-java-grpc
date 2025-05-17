package pl.edu.agh;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import moviecontroller.MovieRequest;
import moviecontroller.MovieResponse;
import moviecontroller.MovieControllerServiceGrpc;

import movierecommender.RecommenderRequest;
import movierecommender.RecommenderServiceGrpc;
import moviestore.MovieStoreRequest;
import moviestore.MovieStoreServiceGrpc;
import userpreferences.UserPreferencesRequest;
import userpreferences.UserPreferencesResponse;
import userpreferences.UserPreferencesServiceGrpc;
import movierecommender.RecommenderResponse;

public class MovieControllerServiceImpl extends
        MovieControllerServiceGrpc.MovieControllerServiceImplBase {
    public static final int MOVIE_STORE_PORT = 50052;
    public static final int USER_PREFERENCES_PORT = 50053;
    public static final int MOVIE_RECOMMENDER_PORT = 50054;
    @Override
    public void getMovie(MovieRequest request,
                         StreamObserver<MovieResponse> responseObserver) {
        String userId = request.getUserid();
        MovieStoreServiceGrpc.MovieStoreServiceBlockingStub
                movieStoreClient =
                MovieStoreServiceGrpc
                        .newBlockingStub(getChannel(MOVIE_STORE_PORT));
        UserPreferencesServiceGrpc.UserPreferencesServiceStub
                userPreferencesClient = UserPreferencesServiceGrpc
                .newStub(getChannel(USER_PREFERENCES_PORT));
        RecommenderServiceGrpc.RecommenderServiceStub
                recommenderClient =
                RecommenderServiceGrpc
                        .newStub(getChannel(MOVIE_RECOMMENDER_PORT));

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<RecommenderRequest> recommenderRequestObserver = recommenderClient.getRecommendedMovie(
                new StreamObserver<>() {
                     public void onNext(RecommenderResponse value) {
                         responseObserver.onNext(MovieResponse
                                 .newBuilder()
                                 .setMovie(value.getMovie()).build());
                         System.out.println("Recommended movie " + value.getMovie());
                     }
                     public void onError(Throwable t) {
                         responseObserver.onError(t);
                         latch.countDown();
                     }
                     public void onCompleted() {
                         responseObserver.onCompleted();
                         latch.countDown();
                     }
                }
            );
        StreamObserver<UserPreferencesRequest> streamObserver = userPreferencesClient.getShortlistedMovies(
            new StreamObserver<>() {
                  public void onNext(UserPreferencesResponse value){
                      recommenderRequestObserver
                          .onNext(RecommenderRequest.newBuilder()
                                  .setUserid(userId)
                                  .setMovie(value.getMovie()).build());
                  }

                  public void onError(Throwable t) {
                  }
                  @Override
                  public void onCompleted() {
                      recommenderRequestObserver.onCompleted();
                  }
            }
        );
        movieStoreClient.getMovies(MovieStoreRequest.newBuilder()
                .setGenre(request.getGenre())
                .build()
        ).forEachRemaining(response -> {
            streamObserver
                .onNext(UserPreferencesRequest.newBuilder()
                        .setUserid(userId).setMovie(response.getMovie())
                        .build());
        });

        streamObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private ManagedChannel getChannel(int port) {
        return ManagedChannelBuilder.forAddress("localhost", port)
                .usePlaintext()
                .build();
    }
}