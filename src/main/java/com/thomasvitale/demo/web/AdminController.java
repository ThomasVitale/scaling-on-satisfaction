package com.thomasvitale.demo.web;

import com.thomasvitale.demo.generation.PromptProvider;
import com.thomasvitale.demo.generation.StoryGenerator;
import com.thomasvitale.demo.story.ActiveStoryPart;
import com.thomasvitale.demo.story.ActiveStoryPartRepository;
import com.thomasvitale.demo.story.StoryFragmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin")
class AdminController {

    private static final int REQUIRED_STORY_COUNT = 20;

    private final StoryGenerator storyGenerator;
    private final StoryFragmentRepository storyFragmentRepository;
    private final ActiveStoryPartRepository activeStoryPartRepository;

    AdminController(StoryGenerator storyGenerator,
                    StoryFragmentRepository storyFragmentRepository,
                    ActiveStoryPartRepository activeStoryPartRepository) {
        this.storyGenerator = storyGenerator;
        this.storyFragmentRepository = storyFragmentRepository;
        this.activeStoryPartRepository = activeStoryPartRepository;
    }

    @GetMapping
    String adminPage() {
        return "admin";
    }

    @GetMapping("/api/status")
    @ResponseBody
    StatusResponse status() {
        return buildStatus();
    }

    @PostMapping("/api/advance")
    @ResponseBody
    ResponseEntity<StatusResponse> advance() {
        int currentPart = getCurrentPart();
        if (currentPart < PromptProvider.TOTAL_PARTS) {
            activeStoryPartRepository.save(new ActiveStoryPart(true, currentPart + 1));
        }
        return ResponseEntity.ok(buildStatus());
    }

    @PostMapping("/api/reset")
    @ResponseBody
    ResponseEntity<StatusResponse> reset() {
        activeStoryPartRepository.save(new ActiveStoryPart(true, 0));
        return ResponseEntity.ok(buildStatus());
    }

    /**
     * Triggers story generation asynchronously on a virtual thread.
     * Returns 202 Accepted immediately; clients poll /admin/api/status to track progress.
     */
    @PostMapping("/api/generate")
    @ResponseBody
    ResponseEntity<Void> generate() {
        Thread.ofVirtual().start(() -> storyGenerator.generate());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/api/delete-stories")
    @ResponseBody
    ResponseEntity<StatusResponse> deleteStories() {
        storyFragmentRepository.deleteAll();
        return ResponseEntity.ok(buildStatus());
    }

    private int getCurrentPart() {
        return activeStoryPartRepository.findById(true)
                .map(ActiveStoryPart::part)
                .orElse(0);
    }

    private StatusResponse buildStatus() {
        long count = storyFragmentRepository.count();
        int currentPart = getCurrentPart();
        return new StatusResponse(count, REQUIRED_STORY_COUNT, count >= REQUIRED_STORY_COUNT,
                currentPart, PromptProvider.TOTAL_PARTS);
    }

    record StatusResponse(long storyCount, int requiredCount, boolean ready,
                          int currentPart, int totalParts) {}

}
