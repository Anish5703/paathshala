package com.paathshala.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class AiChatService {

    @Value("${spring.ai.ollama.chat.options.model}")
    private String defaultAiModel;
    private final OllamaChatModel chatModel;
    private final Logger log ;
    public AiChatService(OllamaChatModel chatModel)
    {
        this.chatModel = chatModel;
        this.log = LoggerFactory.getLogger(AiChatService.class);
    }

    public String getAiResponse(String message)
    {
        String response;
        try {
             response = chatModel.call(new Prompt(message, OllamaChatOptions.builder()
                    .model(defaultAiModel).temperature(0.7).build()
            )).getResult().getOutput().getText();
            if (response==null) response = "How can i help you?";
        }
        catch (Exception e)
        {
            log.warn("Ai chat client error {}",e.getLocalizedMessage());
            response = "Something wen wrong";
        }
            return response;


    }
}
