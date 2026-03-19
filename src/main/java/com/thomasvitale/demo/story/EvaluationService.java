package com.thomasvitale.demo.story;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Service;

@Service
public class EvaluationService {

    private static final AttributeKey<String> EVALUATION_NAME        = AttributeKey.stringKey("gen_ai.evaluation.name");
    private static final AttributeKey<String> EVALUATION_SCORE_LABEL = AttributeKey.stringKey("gen_ai.evaluation.score.label");
    private static final AttributeKey<Double> EVALUATION_SCORE_VALUE = AttributeKey.doubleKey("gen_ai.evaluation.score.value");
    private static final AttributeKey<String> RESPONSE_ID            = AttributeKey.stringKey("gen_ai.response.id");
    private static final AttributeKey<String> STORY_NAME             = AttributeKey.stringKey("story.name");
    private static final AttributeKey<String> STORY_MODEL            = AttributeKey.stringKey("story.model");
    private static final AttributeKey<Long>   STORY_PART             = AttributeKey.longKey("story.part");
    private static final AttributeKey<String> STORY_STYLE            = AttributeKey.stringKey("story.style");
    private static final AttributeKey<String> STORY_THEME            = AttributeKey.stringKey("story.theme");

    private final Tracer tracer;

    public EvaluationService(Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Records a user satisfaction evaluation as an OpenTelemetry span event.
     *
     * @param fragment the story fragment being evaluated
     * @param vote     "thumbs_up" or "thumbs_down"
     */
    public void evaluate(StoryFragment fragment, String vote) {
        Span span = tracer.spanBuilder("evaluate UserSatisfaction").startSpan();
        try (Scope ignored = span.makeCurrent()) {
            span.setAttribute(STORY_NAME, getStoryName(fragment));

            AttributesBuilder eventAttrs = Attributes.builder()
                .put(EVALUATION_NAME, "UserSatisfaction")
                .put(EVALUATION_SCORE_LABEL, vote)
                .put(EVALUATION_SCORE_VALUE, "thumbs_up".equals(vote) ? 1.0 : 0.0)
                .put(STORY_MODEL, fragment.model().name())
                .put(STORY_PART, (long) fragment.part())
                .put(STORY_THEME, fragment.theme().name())
                .put(STORY_STYLE, fragment.style().name());

            if (fragment.responseId() != null) {
                eventAttrs.put(RESPONSE_ID, fragment.responseId());
            }

            span.addEvent("gen_ai.evaluation.result", eventAttrs.build());
        } finally {
            span.end();
        }
    }

    private String getStoryName(StoryFragment fragment) {
        return switch (fragment.theme()) {
            case MOON -> switch (fragment.style()) {
                case DRY   -> "story-app-1a";
                case FUNNY -> "story-app-1b";
            };
            case CIRCUS -> switch (fragment.model()) {
                case SMALL -> "story-app-2a";
                case LARGE -> "story-app-2b";
            };
        };
    }

}
