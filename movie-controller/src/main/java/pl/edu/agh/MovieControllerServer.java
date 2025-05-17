package pl.edu.agh;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MovieControllerServer {
    public static final int MOVIE_CONTROLLER_SERVICE_PORT = 50051;

    public static void main(String[] args)
            throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(MOVIE_CONTROLLER_SERVICE_PORT)
                .addService(new MovieControllerServiceImpl())
                .build();
        server.start();
        System.out.println("Server started, listening on " + MOVIE_CONTROLLER_SERVICE_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            System.out.println("Successfully stopped the server");
        }));
        server.awaitTermination();
    }
}
