package com.thomasvitale.demo.observation;

import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

public final class ChatModelObservationContentProcessor {

    private ChatModelObservationContentProcessor() {
    }

    public record ChatMessage(String role, List<ChatMessagePart> parts) {}

    public record ChatMessagePart(String type, String content) {}

    public static List<ChatMessage> prompt(ChatModelObservationContext context) {
        List<Message> messages = context.getRequest().getInstructions();
        if (CollectionUtils.isEmpty(messages)) {
            return List.of();
        }

        return messages.stream()
            .filter(message -> message instanceof AbstractMessage)
            .map(message -> (AbstractMessage) message)
            .map(message -> new ChatMessage(
                message.getMessageType().getValue(),
                List.of(new ChatMessagePart("text", message.getText()))))
            .toList();
    }

    public static List<ChatMessage> completion(ChatModelObservationContext context) {
        if (context.getResponse() == null || CollectionUtils.isEmpty(context.getResponse().getResults())) {
            return List.of();
        }

        if (context.getResponse().getResult() == null || !StringUtils.hasText(context.getResponse().getResult().getOutput().getText())) {
            return List.of();
        }

        return context.getResponse()
                .getResults()
                .stream()
                .map(Generation::getOutput)
                .filter(output -> StringUtils.hasText(output.getText()))
                .map(message -> (AbstractMessage) message)
                .map(message -> new ChatMessage(
                    message.getMessageType().getValue(),
                    List.of(new ChatMessagePart("text", message.getText()))))
                .toList();
    }

}
