package pl.edu.agh;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.io.IOException;

public class UserPreferencesServer {

    public static final int PORT = 50053;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new UserPreferencesServiceImpl())
                .build();
        server.start();
        System.out.println("Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            TelemetryConfig.shutdown();
            System.out.println("Successfully stopped the server and telemetry");
        }));
        server.awaitTermination();
    }
}
