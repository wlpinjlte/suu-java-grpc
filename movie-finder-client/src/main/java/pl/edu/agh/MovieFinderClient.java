package pl.edu.agh;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import moviecontroller.MovieRequest;
import moviecontroller.MovieResponse;
import moviecontroller.MovieControllerServiceGrpc;

import common.Genre;

public class MovieFinderClient {
    public static final int MOVIE_CONTROLLER_SERVICE_PORT = 50051;
    public static void main(String[] args) {
        String host = System.getenv().getOrDefault("GRPC_SERVER_HOST", "localhost");
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host,
                        MOVIE_CONTROLLER_SERVICE_PORT)
                .usePlaintext()
                .build();
        MovieControllerServiceGrpc.MovieControllerServiceBlockingStub
                movieFinderClient = MovieControllerServiceGrpc
                .newBlockingStub(channel);
        try {
            MovieResponse movieResponse = movieFinderClient
                    .getMovie(MovieRequest.newBuilder()
                            .setGenre(Genre.ACTION)
                            .setUserid("abc")
                            .build());
            System.out.println("Recommended movie " +
                    movieResponse.getMovie());
        } catch (StatusRuntimeException e) {
            System.out.println("Recommended movie not found!");
            e.printStackTrace();
        }
    }
}