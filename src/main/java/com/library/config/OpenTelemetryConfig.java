package com.library.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    // 1. Custom Metric: Counter to track successful book borrowings
    @Bean
    public Counter bookBorrowCounter(MeterRegistry meterRegistry) {
        return Counter.builder("library.books.borrowed.total")
                .description("Total number of books borrowed")
                .register(meterRegistry);
    }

    // 2. Custom Tracer Bean for creating custom Spans
    @Bean
    public Tracer openTelemetryTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.library.tracer");
    }
}