package pl.edu.agh;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.io.IOException;

public class MovieStoreServer {
    public static final int MOVIE_SERVICE_PORT = 50052;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(MOVIE_SERVICE_PORT)
                .addService(new MovieStoreServiceImpl())
                .build();

        server.start();
        System.out.println("Server started, listening on " + MOVIE_SERVICE_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            TelemetryConfig.shutdown();
            System.out.println("Successfully stopped the server and telemetry");
        }));

        server.awaitTermination();
    }
}
