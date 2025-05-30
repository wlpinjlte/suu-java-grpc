package pl.edu.agh;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import pl.edu.agh.telemetry.TelemetryConfig;

import java.io.IOException;

public class RecommenderServer {

    public static final int PORT = 50054;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(PORT)
                .addService(new RecommenderServiceImpl())
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
