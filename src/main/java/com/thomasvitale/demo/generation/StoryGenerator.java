package com.thomasvitale.demo.generation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import com.thomasvitale.demo.generation.PromptProvider.StoryModel;
import com.thomasvitale.demo.generation.PromptProvider.StoryStyle;
import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;
import com.thomasvitale.demo.story.StoryFragment;
import com.thomasvitale.demo.story.StoryFragmentRepository;

@Service
public class StoryGenerator {

    private static final String MODEL_SMALL = "mistral-small-2506";
    private static final String MODEL_LARGE = "mistral-large-2512";

    private final ChatClient chatClient;
    private final PromptProvider promptProvider;
    private final StoryFragmentRepository storyFragmentRepository;

    public StoryGenerator(ChatClient.Builder chatClientBuilder, PromptProvider promptProvider, StoryFragmentRepository storyFragmentRepository) {
        this.chatClient = chatClientBuilder
            .defaultOptions(ChatOptions.builder()
                .maxTokens(300)
                .build())
            .build();
        this.promptProvider = promptProvider;
        this.storyFragmentRepository = storyFragmentRepository;
    }

    public void generate() {

        // Generate and save stories for StoryTheme == MOON
        StoryTheme theme = StoryTheme.MOON;
        for (var style : StoryStyle.values()) {
            for (int part = 1; part <= PromptProvider.TOTAL_PARTS; part++) {
                Prompt prompt = promptProvider.buildPrompt(part, theme, style);
                ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
                String content = chatResponse.getResult().getOutput().getText();
                String responseId = chatResponse.getMetadata().getId();
                storyFragmentRepository.save(new StoryFragment(null, theme, part, style, StoryModel.SMALL, content, responseId));
            }
        }

        // Generate and save stories for StoryTheme == CIRCUS
        theme = StoryTheme.CIRCUS;
        for (var model : StoryModel.values()) {
            for (int part = 1; part <= PromptProvider.TOTAL_PARTS; part++) {
                Prompt prompt = promptProvider.buildPrompt(part, theme, StoryStyle.FUNNY).mutate()
                    .chatOptions(ChatOptions.builder()
                        .model(StoryModel.SMALL.equals(model) ? MODEL_SMALL : MODEL_LARGE)
                        .build())
                    .build();
                ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();
                String content = chatResponse.getResult().getOutput().getText();
                String responseId = chatResponse.getMetadata().getId();
                storyFragmentRepository.save(new StoryFragment(null, theme, part, StoryStyle.FUNNY, model, content, responseId));
            }
        }
    }

}
