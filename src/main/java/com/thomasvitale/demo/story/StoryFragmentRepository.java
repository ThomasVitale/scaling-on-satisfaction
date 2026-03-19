package com.thomasvitale.demo.story;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.ListCrudRepository;

import com.thomasvitale.demo.generation.PromptProvider.StoryModel;
import com.thomasvitale.demo.generation.PromptProvider.StoryStyle;
import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;

public interface StoryFragmentRepository extends ListCrudRepository<StoryFragment, UUID> {
    Optional<StoryFragment> findByThemeAndPartAndStyle(StoryTheme storyTheme, int storyPart, StoryStyle style);
    Optional<StoryFragment> findByThemeAndPartAndModel(StoryTheme storyTheme, int storyPart, StoryModel model);
}
