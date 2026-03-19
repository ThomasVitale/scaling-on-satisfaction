package com.thomasvitale.demo.story;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import com.thomasvitale.demo.generation.PromptProvider.StoryModel;
import com.thomasvitale.demo.generation.PromptProvider.StoryStyle;
import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;

public record StoryFragment(
    @Id
    UUID id,
    StoryTheme theme,
    int part,
    StoryStyle style,
    StoryModel model,
    String content,
    String responseId
) {}
