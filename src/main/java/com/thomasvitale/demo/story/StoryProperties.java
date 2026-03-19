package com.thomasvitale.demo.story;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.thomasvitale.demo.generation.PromptProvider.StoryModel;
import com.thomasvitale.demo.generation.PromptProvider.StoryStyle;
import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;

@ConfigurationProperties(prefix = "story")
public class StoryProperties {
    
    /**
     * Theme for the story to generate.
     */
    private StoryTheme theme = StoryTheme.MOON;

    /**
     * Style for the story to generate.
     */
    private StoryStyle style = StoryStyle.FUNNY;

    /**
     * Model used to generate the story.
     */
    private StoryModel model = StoryModel.SMALL;

    public StoryTheme getTheme() {
        return theme;
    }

    public void setTheme(StoryTheme theme) {
        this.theme = theme;
    }

    public StoryStyle getStyle() {
        return style;
    }

    public void setStyle(StoryStyle style) {
        this.style = style;
    }

    public StoryModel getModel() {
        return model;
    }

    public void setModel(StoryModel model) {
        this.model = model;
    }

}
