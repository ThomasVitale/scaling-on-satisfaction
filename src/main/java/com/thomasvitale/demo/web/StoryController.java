package com.thomasvitale.demo.web;

import com.thomasvitale.demo.generation.PromptProvider;
import com.thomasvitale.demo.story.EvaluationService;
import com.thomasvitale.demo.story.StoryFragmentRepository;
import com.thomasvitale.demo.story.StoryFragmentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
                .map(f -> new ContentResponse(activePart, PromptProvider.TOTAL_PARTS, f.content(), f.id()))
                .orElse(new ContentResponse(activePart, PromptProvider.TOTAL_PARTS, null, null));
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
        evaluationService.evaluate(fragment.get(), request.vote());
        return ResponseEntity.ok().build();
    }

    record PartResponse(int part) {}

    record ContentResponse(int part, int totalParts, String content, UUID fragmentId) {}

    record VoteRequest(@NotNull UUID fragmentId, @NotBlank String vote) {}

}
