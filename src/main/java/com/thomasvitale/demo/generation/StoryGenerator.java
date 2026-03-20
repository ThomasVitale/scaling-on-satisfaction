package com.thomasvitale.demo.generation;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.observation.AdvisorObservationConvention;
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.stereotype.Service;

import com.thomasvitale.demo.generation.PromptProvider.StoryModel;
import com.thomasvitale.demo.generation.PromptProvider.StoryStyle;
import com.thomasvitale.demo.generation.PromptProvider.StoryTheme;
import com.thomasvitale.demo.story.StoryFragment;
import com.thomasvitale.demo.story.StoryFragmentRepository;

import io.micrometer.observation.ObservationRegistry;

@Service
public class StoryGenerator {

    private final ChatClient chatClient;
    private final ChatClient anthropicChatClient;
    private final PromptProvider promptProvider;
    private final StoryFragmentRepository storyFragmentRepository;

    public StoryGenerator(MistralAiChatModel mistralAiChatModel, 
                          AnthropicChatModel anthropicChatModel,
                          ObservationRegistry observationRegistry,
                          ChatClientObservationConvention chatClientObservationConvention,
                          AdvisorObservationConvention advisorObservationConvention,
                          PromptProvider promptProvider,
                          StoryFragmentRepository storyFragmentRepository
    ) {
        this.chatClient = ChatClient.builder(mistralAiChatModel, observationRegistry, chatClientObservationConvention, advisorObservationConvention)
            .defaultOptions(ChatOptions.builder()
                .maxTokens(300)
                .build())
            .build();
        this.anthropicChatClient = ChatClient.builder(anthropicChatModel, observationRegistry, chatClientObservationConvention, advisorObservationConvention)
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
                Prompt prompt = promptProvider.buildPrompt(part, theme, StoryStyle.FUNNY);
                ChatResponse chatResponse = switch(model) {
                    case SMALL -> chatClient.prompt(prompt).call().chatResponse();
                    case LARGE -> anthropicChatClient.prompt(prompt).call().chatResponse();
                };
                String content = chatResponse.getResult().getOutput().getText();
                String responseId = chatResponse.getMetadata().getId();
                storyFragmentRepository.save(new StoryFragment(null, theme, part, StoryStyle.FUNNY, model, content, responseId));
            }
        }
    }

}
