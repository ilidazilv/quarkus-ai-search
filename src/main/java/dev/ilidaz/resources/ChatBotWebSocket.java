package dev.ilidaz.resources;

import dev.ilidaz.dtos.SearchResponseDto;
import dev.ilidaz.services.BotService;
import dev.ilidaz.services.SearchService;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.UUID;

@WebSocket(path = "/chatbot")
public class ChatBotWebSocket {

    @Inject
    SearchService searchService;

    private final ThreadLocal<String> lastMessageId = new ThreadLocal<>();

    @OnOpen
    public SearchResponseDto onOpen() {
        // Generate a unique message ID for this response
        String messageId = UUID.randomUUID().toString();
        SearchResponseDto response = new SearchResponseDto(
                "Hello, I'm Bob, how can I help you?",
                Collections.emptyList(),
                messageId
        );

        lastMessageId.set(messageId);

        return response;
    }


    @OnTextMessage
    public SearchResponseDto onMessage(String message) {
        // Generate a unique message ID for this response
        String messageId = UUID.randomUUID().toString();

        // Get search response from service
        SearchResponseDto response = searchService.chat(message);

        // Set message ID to avoid duplication
        response.setMessageId(messageId);
        lastMessageId.set(messageId);

        return response;
    }

    @OnClose
    public void onClose() {
        // Clean up resources
        lastMessageId.remove();
    }

}