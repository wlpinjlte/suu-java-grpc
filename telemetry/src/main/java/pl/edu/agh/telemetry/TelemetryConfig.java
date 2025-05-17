package pl.edu.agh.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

import java.time.Duration;

public class TelemetryConfig {

    private static OpenTelemetrySdk openTelemetrySdk;

    public static OpenTelemetry initOpenTelemetry(String serviceName) {
        if (openTelemetrySdk != null) {
            return openTelemetrySdk;
        }

        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
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
            openTelemetrySdk.getSdkTracerProvider().shutdown();
        }
    }
}
