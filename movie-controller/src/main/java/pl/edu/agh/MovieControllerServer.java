package pl.edu.agh;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.opentelemetry.api.OpenTelemetry;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.io.IOException;

public class MovieControllerServer {
    public static final int MOVIE_CONTROLLER_SERVICE_PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {
        OpenTelemetry openTelemetry = TelemetryConfig.initOpenTelemetry("movie-controller-service");

        Server server = ServerBuilder.forPort(MOVIE_CONTROLLER_SERVICE_PORT)
                .addService(new MovieControllerServiceImpl())
                .build();

        server.start();
        System.out.println("Server started, listening on " + MOVIE_CONTROLLER_SERVICE_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();

            System.out.println("Shutting down OpenTelemetry...");
            TelemetryConfig.shutdown();

            System.out.println("Shutdown complete.");
        }));

        server.awaitTermination();
    }
}
