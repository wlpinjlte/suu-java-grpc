package pl.edu.agh.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TelemetryConfig {

    private static OpenTelemetrySdk openTelemetrySdk;

    // private static final String OTEL_EXPORTER_OTLP_ENDPOINT = "http://otel-collector:4317";
    private static final String OTEL_EXPORTER_OTLP_ENDPOINT = "http://otel-collector.movie-appsvc.cluster.local:4317";


    public static OpenTelemetry initOpenTelemetry(String serviceName) {
        if (openTelemetrySdk != null) {
            return openTelemetrySdk;
        }

        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(OTEL_EXPORTER_OTLP_ENDPOINT)
                .setTimeout(Duration.ofSeconds(5))
                .build();

        Resource serviceResource = Resource.getDefault()
                .merge(Resource.create(io.opentelemetry.api.common.Attributes.of(
                        io.opentelemetry.api.common.AttributeKey.stringKey("service.name"), serviceName
                )));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .setResource(serviceResource)
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                .build();

        openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .build();

        return openTelemetrySdk;
    }

    public static Tracer getTracer(String serviceName) {
        return initOpenTelemetry(serviceName).getTracer(serviceName);
    }

    public static void shutdown() {
        if (openTelemetrySdk != null) {
            openTelemetrySdk.getSdkTracerProvider().shutdown().join(10, TimeUnit.SECONDS);
        }
    }
}
