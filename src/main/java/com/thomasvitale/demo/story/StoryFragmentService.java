package com.thomasvitale.demo.story;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;

@Service
public class StoryFragmentService {

    private final ActiveStoryPartRepository activeStoryPartRepository;
    private final StoryFragmentRepository storyFragmentRepository;
    private final StoryProperties storyProperties;

    public StoryFragmentService(ActiveStoryPartRepository activeStoryPartRepository, StoryFragmentRepository storyFragmentRepository, StoryProperties storyProperties) {
        this.activeStoryPartRepository = activeStoryPartRepository;
        this.storyFragmentRepository = storyFragmentRepository;
        this.storyProperties = storyProperties;
    }

    public int getActivePart() {
        return activeStoryPartRepository.findById(true)
                .map(ActiveStoryPart::part)
                .orElse(1);
    }

    public Optional<StoryFragment> getActiveFragment() {
        if (StoryTheme.MOON.equals(storyProperties.getTheme())) {
            return activeStoryPartRepository.findById(true)
                .flatMap(active -> storyFragmentRepository.findByThemeAndPartAndStyle(
                    storyProperties.getTheme(), 
                    getActivePart(), 
                    storyProperties.getStyle()));
        }

        return activeStoryPartRepository.findById(true)
                .flatMap(active -> storyFragmentRepository.findByThemeAndPartAndModel(
                    storyProperties.getTheme(),
                    getActivePart(),
                    storyProperties.getModel()));
    }
    
}
