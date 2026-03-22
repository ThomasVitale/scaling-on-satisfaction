package com.thomasvitale.demo.web;

import com.thomasvitale.demo.generation.PromptProvider;
import com.thomasvitale.demo.story.EvaluationService;
import com.thomasvitale.demo.story.StoryFragmentRepository;
import com.thomasvitale.demo.story.StoryFragmentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/story")
class StoryController {

    private static final Logger logger = LoggerFactory.getLogger(StoryController.class);

    private final StoryFragmentService storyFragmentService;
    private final StoryFragmentRepository storyFragmentRepository;
    private final EvaluationService evaluationService;

    StoryController(StoryFragmentService storyFragmentService,
                    StoryFragmentRepository storyFragmentRepository,
                    EvaluationService evaluationService) {
        this.storyFragmentService = storyFragmentService;
        this.storyFragmentRepository = storyFragmentRepository;
        this.evaluationService = evaluationService;
    }

    /**
     * Cheap endpoint polled every 5 seconds by all clients.
     * Returns only the current active part number — a single integer read from the DB.
     */
    @GetMapping("/part")
    PartResponse getPart() {
        return new PartResponse(storyFragmentService.getActivePart());
    }

    /**
     * Called by the client only when the part number changes.
     * Returns the full story content and fragment ID for voting.
     */
    @GetMapping("/content")
    ContentResponse getContent() {
        int activePart = storyFragmentService.getActivePart();
        return storyFragmentService.getActiveFragment()
                .map(f -> new ContentResponse(activePart, PromptProvider.TOTAL_PARTS, f.content(), f.id(),
                        f.style() != null ? f.style().name().toLowerCase() : null,
                        f.model() != null ? f.model().name().toLowerCase() : null))
                .orElse(new ContentResponse(activePart, PromptProvider.TOTAL_PARTS, null, null, null, null));
    }

    /**
     * Records a user satisfaction vote as an OpenTelemetry span (used as a Flagger metric).
     */
    @PostMapping("/vote")
    ResponseEntity<Void> vote(@RequestBody VoteRequest request) {
        var fragment = storyFragmentRepository.findById(request.fragmentId());
        if (fragment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        logger.info("Received vote for fragment {}", fragment.get().id());
        evaluationService.evaluate(fragment.get(), request.vote());
        return ResponseEntity.ok().build();
    }

    record PartResponse(int part) {}

    record ContentResponse(int part, int totalParts, String content, UUID fragmentId, String style, String model) {}

    record VoteRequest(@NotNull UUID fragmentId, @NotBlank String vote) {}

}
