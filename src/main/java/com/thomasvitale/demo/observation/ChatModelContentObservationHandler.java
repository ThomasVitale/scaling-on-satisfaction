package com.thomasvitale.demo.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.handler.TracingObservationHandler;
import io.micrometer.tracing.otel.bridge.OtelSpan;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ChatModelContentObservationHandler implements ObservationHandler<ChatModelObservationContext> {

    private final JsonMapper jsonMapper;

    public ChatModelContentObservationHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void onStop(ChatModelObservationContext context) {
        TracingObservationHandler.TracingContext tracingContext = context.get(TracingObservationHandler.TracingContext.class);

        if (tracingContext == null || tracingContext.getSpan() == null) {
            return;
        }

        Span otelSpan = OtelSpan.toOtel(tracingContext.getSpan());

        otelSpan.addEvent("gen_ai.client.inference.operation.details", Attributes.of(
            AttributeKey.stringKey("gen_ai.input.messages"), jsonMapper.writeValueAsString(ChatModelObservationContentProcessor.prompt(context)),
            AttributeKey.stringKey("gen_ai.output.messages"), jsonMapper.writeValueAsString(ChatModelObservationContentProcessor.completion(context))
        ));

    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return context instanceof ChatModelObservationContext;
    }

}
