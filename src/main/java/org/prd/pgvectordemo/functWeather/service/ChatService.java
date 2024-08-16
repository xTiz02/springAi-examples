package org.prd.pgvectordemo.functWeather.service;

import lombok.extern.slf4j.Slf4j;


import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatService {

    private final OllamaChatModel chatModel;

    public ChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
/*
                )
                .defaultSystem("You are a helpful AI Assistant answering questions about cities around the world.")
                .defaultFunctions("currentWeatherFunction")
                .build();*/
    }

    public String functionChat(String query) {
        ChatResponse response = chatModel.call(new Prompt(
                (query),
                OllamaOptions.builder().withFunction("currentWeatherFunction").build()));

        log.info("Response: {}", response);
        log.info("Metadata: {}", response.getMetadata());
        log.info("Output: {}", response.getResult().getMetadata());
        return response.getResult().getOutput().getContent();
    }

    public String chat(String query) {
        ChatResponse response = chatModel.call(new Prompt(query));
        return response.getResult().getOutput().getContent();
    }
}
